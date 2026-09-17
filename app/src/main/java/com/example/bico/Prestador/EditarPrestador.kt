package com.example.bico.Prestador

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.bico.R
import com.example.bico.ServicoAdapter
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityEditarPrestadorBinding
import com.example.bico.model.Prestador
import com.example.bico.utils.ImageUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import androidx.lifecycle.lifecycleScope
import com.example.bico.utils.IbgeClient
import com.example.bico.utils.NoAccentsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.AutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout

class EditarPrestador : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPrestadorBinding
    private lateinit var repository: UserRepository
    private var currentUser: Prestador? = null
    private val listaCidadesFormatadas = mutableListOf<String>()

    private var fotoAlvo: Int = 0 // -1: Perfil, 0: Horizontal, 1-4: Fotos Inferiores
    private var isEditModeFotos = false

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uriContent = result.uriContent
            if (uriContent != null) {
                salvarImagemAtualizada(uriContent)
            }
        }
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val options = CropImageOptions().apply {
                when (fotoAlvo) {
                    -1 -> {
                        aspectRatioX = 1
                        aspectRatioY = 1
                        fixAspectRatio = true
                    }
                    0 -> {
                        aspectRatioX = 16
                        aspectRatioY = 9
                        fixAspectRatio = true
                    }
                    else -> {
                        aspectRatioX = 155
                        aspectRatioY = 130
                        fixAspectRatio = true
                    }
                }
                guidelines = CropImageView.Guidelines.ON
                backgroundColor = Color.BLACK
                activityTitle = "Recortar Foto"
                cropMenuCropButtonTitle = "Concluir"
            }
            cropImage.launch(CropImageContractOptions(uri, options))
        }
    }

    private fun salvarImagemAtualizada(uri: Uri) {
        val uriPersistente = ImageUtils.persistirImagem(this, uri) ?: return

        currentUser?.let { u ->
            val userAtualizado = when (fotoAlvo) {
                -1 -> {
                    binding.fotoPerfil.load(uriPersistente)
                    u.copy(fotoPerfil = uriPersistente.toString())
                }
                else -> {
                    val fotosAtuais = (u.fotosServico ?: emptyList()).filter { it.isNotEmpty() }.toMutableList()
                    val index = fotoAlvo - 1
                    if (index < fotosAtuais.size) {
                        fotosAtuais[index] = uriPersistente.toString()
                    } else {
                        fotosAtuais.add(uriPersistente.toString())
                    }
                    u.copy(fotosServico = fotosAtuais)
                }
            }
            lifecycleScope.launch {
                repository.atualizarPrestador(userAtualizado.id ?: "", userAtualizado)
            }
            currentUser = userAtualizado
            atualizarVisibilidadeFotos()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPrestadorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = UserRepository(this)
        lifecycleScope.launch {
            val user = repository.usuarioLogado()
            if (user is Prestador) {
                currentUser = user
                loadUserData()
            }
        }
        carregarCidadesIbge()

        setupEdgeToEdge()
        setupListeners()
    }

    private fun setupEdgeToEdge() {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.WHITE, Color.WHITE)
        )
        ViewCompat.setOnApplyWindowInsetsListener(binding.parentMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupListeners() {
        binding.icHome.setOnClickListener { finish() }

        binding.btnEditarFotoPerfil.setOnClickListener {
            fotoAlvo = -1
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.icLapisSobre.setOnClickListener { showDialogEditarSobre() }
        binding.icLapisServicos.setOnClickListener { showDialogAdicionarServico() }
        binding.icLapisLocal.setOnClickListener { showDialogEditarLocal() }

        binding.icLapisFotos.setOnClickListener {
            isEditModeFotos = !isEditModeFotos
            val color = if (isEditModeFotos) "#FF8C00".toColorInt() else Color.BLACK
            binding.icLapisFotos.setColorFilter(color)
            atualizarVisibilidadeFotos()
        }

        setupFotoClickListener(binding.imgFoto1, 0)
        setupFotoClickListener(binding.imgFoto2, 1)
        setupFotoClickListener(binding.imgFoto3, 2)
        setupFotoClickListener(binding.imgFoto4, 3)
    }

    private fun loadUserData() {
        currentUser?.let { user ->
            binding.txtNomePrestador.text = (user.usuario ?: "").ifEmpty { "UserName" }
            binding.txtCidade.text = (user.local ?: "").ifEmpty { "Local" }
            binding.txtDesc.text = (user.descricao ?: "").ifEmpty { "Adicione mais informações sobre você e seus serviços." }

            if (!user.fotoPerfil.isNullOrEmpty()) {
                binding.fotoPerfil.load(user.fotoPerfil) {
                    crossfade(true)
                    placeholder(R.drawable.user)
                    error(R.drawable.user)
                }
            } else {
                binding.fotoPerfil.setImageResource(R.drawable.user)
            }

            setupRecyclerView(user.servicos ?: emptyList())
            atualizarVisibilidadeFotos()
        }
    }

    private fun setupRecyclerView(servicos: List<String>) {
        val listaServicos = servicos.toMutableList()
        val adapter = ServicoAdapter(listaServicos) { position ->
            MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_Bico_MaterialAlertDialog)
                .setTitle("Remover Serviço")
                .setMessage("Deseja remover \"${listaServicos[position]}\"?")
                .setPositiveButton("Remover") { _, _ ->
                    listaServicos.removeAt(position)
                    updateUserServices(listaServicos)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
        binding.rvServicos.adapter = adapter
    }

    private fun updateUserServices(novaLista: List<String>) {
        currentUser?.let { u ->
            val userAtualizado = u.copy(servicos = novaLista)
            lifecycleScope.launch {
                repository.atualizarPrestador(userAtualizado.id ?: "", userAtualizado)
            }
            currentUser = userAtualizado
            (binding.rvServicos.adapter as? ServicoAdapter)?.apply {
                notifyDataSetChanged()
            }
        }
    }

    private fun atualizarVisibilidadeFotos() {
        val imageViews = listOf(binding.imgFoto1, binding.imgFoto2, binding.imgFoto3, binding.imgFoto4)
        val fotos = currentUser?.fotosServico?.filter { it.isNotEmpty() } ?: emptyList()

        imageViews.forEachIndexed { index, imageView ->
            when {
                index < fotos.size -> {
                    imageView.visibility = View.VISIBLE
                    imageView.load(fotos[index].toUri())
                    imageView.alpha = 1.0f
                }
                isEditModeFotos && index == fotos.size && index < 4 -> {
                    imageView.visibility = View.VISIBLE
                    imageView.setImageResource(R.drawable.nenuma_imagem_selecionada)
                    imageView.alpha = 0.4f
                }
                else -> imageView.visibility = View.GONE
            }
        }
        binding.txtSemFotos.visibility = if (fotos.isEmpty() && !isEditModeFotos) View.VISIBLE else View.GONE
    }

    private fun setupFotoClickListener(imageView: ImageView, index: Int) {
        imageView.setOnClickListener {
            val fotos = currentUser?.fotosServico?.filter { it.isNotEmpty() } ?: emptyList()
            if (isEditModeFotos) {
                if (index < fotos.size) {
                    MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_Bico_MaterialAlertDialog)
                        .setTitle("Opções da Foto")
                        .setItems(arrayOf("Alterar Foto", "Remover Foto")) { _, which ->
                            if (which == 0) {
                                fotoAlvo = index + 1
                                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            } else removerFoto(index)
                        }.show()
                } else {
                    fotoAlvo = index + 1
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            } else if (index < fotos.size) expandirFoto(fotos[index])
        }
    }

    private fun removerFoto(index: Int) {
        currentUser?.let { u ->
            val novaLista = (u.fotosServico ?: emptyList()).filter { it.isNotEmpty() }.toMutableList()
            if (index < novaLista.size) {
                novaLista.removeAt(index)
                val userAtualizado = u.copy(fotosServico = novaLista)
                lifecycleScope.launch {
                    repository.atualizarPrestador(userAtualizado.id ?: "", userAtualizado)
                }
                currentUser = userAtualizado
                atualizarVisibilidadeFotos()
            }
        }
    }

    private fun showDialogEditarSobre() {
        val view = layoutInflater.inflate(R.layout.dialog_editar_descricao, null)
        val input = view.findViewById<TextInputEditText>(R.id.editDescricao)
        input.setText(currentUser?.descricao ?: "")

        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_Bico_MaterialAlertDialog)
            .setTitle("Editar Sobre")
            .setView(view)
            .setPositiveButton("Salvar") { _, _ ->
                val novaDesc = input.text.toString()
                binding.txtDesc.text = novaDesc.ifEmpty { "Adicione mais informações..." }
                currentUser?.let { u ->
                    val userAtualizado = u.copy(descricao = novaDesc)
                    lifecycleScope.launch {
                        repository.atualizarPrestador(userAtualizado.id ?: "", userAtualizado)
                    }
                    currentUser = userAtualizado
                }
            }.setNegativeButton("Cancelar", null).show()
    }

    private fun showDialogAdicionarServico() {
        val view = layoutInflater.inflate(R.layout.dialog_adicionar_servico, null)
        val input = view.findViewById<TextInputEditText>(R.id.editServico)

        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_Bico_MaterialAlertDialog)
            .setTitle("Adicionar Serviço")
            .setView(view)
            .setPositiveButton("Adicionar") { _, _ ->
                val novo = input.text.toString()
                if (novo.isNotEmpty()) {
                    val lista = currentUser?.servicos?.toMutableList() ?: mutableListOf()
                    lista.add(novo)
                    updateUserServices(lista)
                }
            }.setNegativeButton("Cancelar", null).show()
    }

    private fun showDialogEditarLocal() {
        val view = layoutInflater.inflate(R.layout.dialog_editar_local, null)
        val input = view.findViewById<AutoCompleteTextView>(R.id.editLocal)
        val layout = view.findViewById<TextInputLayout>(R.id.txtInputLayoutLocal)
        
        input.setText(currentUser?.local ?: "")
        
        val adapterCidades = NoAccentsAdapter(this, android.R.layout.simple_dropdown_item_1line, listaCidadesFormatadas)
        input.setAdapter(adapterCidades)

        val dialog = MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_Bico_MaterialAlertDialog)
            .setTitle("Editar local de atuação")
            .setView(view)
            .setPositiveButton("Confirmar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnShowListener {
            val button = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val novoLocal = input.text.toString().trim()
                
                if (novoLocal.isEmpty()) {
                    layout.error = "O local não pode estar vazio"
                } else if (listaCidadesFormatadas.isNotEmpty() && !listaCidadesFormatadas.contains(novoLocal)) {
                    layout.error = "Selecione uma cidade válida da lista"
                } else {
                    binding.txtCidade.text = novoLocal
                    currentUser?.let { u ->
                        val userAtualizado = u.copy(local = novoLocal)
                        lifecycleScope.launch {
                            repository.atualizarPrestador(userAtualizado.id ?: "", userAtualizado)
                        }
                        currentUser = userAtualizado
                    }
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun carregarCidadesIbge() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val resposta = IbgeClient.apiService.getMunicipios()
                val nomes = resposta.map { it.nomeFormatado }
                withContext(Dispatchers.Main) {
                    listaCidadesFormatadas.clear()
                    listaCidadesFormatadas.addAll(nomes)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarPrestador, "Erro ao carregar lista de cidades", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun expandirFoto(uriString: String) {
        val view = layoutInflater.inflate(R.layout.dialog_expandir_foto, null)
        val imageView = view.findViewById<ImageView>(R.id.imgExpandida)

        imageView.load(uriString.toUri()) {
            placeholder(R.drawable.nenuma_imagem_selecionada)
            error(R.drawable.nenuma_imagem_selecionada)
        }

        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_Bico_MaterialAlertDialog)
            .setView(view)
            .setPositiveButton("Fechar", null)
            .show()
    }
}
