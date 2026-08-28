package com.example.bico

import android.content.Intent
 import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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
import com.example.bico.databinding.ActivityEditarPrestadorBinding
import com.example.bico.model.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class EditarPrestador : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPrestadorBinding
    private lateinit var repository: UserRepository
    private var currentUser: User? = null
    
    private var fotoAlvo: Int = 0 // 0: Horizontal, 1-4: Fotos Inferiores
    private var isEditModeFotos = false

    // 1. Lançador para o Recortador de Imagem
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uriContent = result.uriContent
            if (uriContent != null) {
                salvarImagemAtualizada(uriContent)
            }
        } else {
            val exception = result.error
            // Opcional: lidar com erro
        }
    }

    // 2. Modificado para abrir o recortador após selecionar a mídia
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            // Configurações do WhatsApp-like Cropper
            val options = CropImageOptions().apply {
                // Define a escala/proporção baseada no alvo
                if (fotoAlvo == 0) {
                    aspectRatioX = 16
                    aspectRatioY = 9
                    fixAspectRatio = true // Proporção fixa para a capa
                } else {
                    aspectRatioX = 155
                    aspectRatioY = 130
                    fixAspectRatio = true // Quadrada para os serviços
                }
                guidelines = CropImageView.Guidelines.ON
                backgroundColor = Color.BLACK
                
                // Garantir visibilidade do botão de conclusão
                activityTitle = "Recortar Foto"
                cropMenuCropButtonTitle = "Concluir"
            }
            
            cropImage.launch(CropImageContractOptions(uri, options))
        }
    }

    private fun salvarImagemAtualizada(uri: Uri) {
        currentUser?.let { u ->
            val userAtualizado = if (fotoAlvo == 0) {
                binding.imgFotoHorizontalPrestador.load(uri)
                u.copy(fotoHorizontalPrestador = uri.toString())
            } else {
                val fotosAtuais = u.fotosServico.filter { it.isNotEmpty() }.toMutableList()
                val index = fotoAlvo - 1
                if (index < fotosAtuais.size) {
                    fotosAtuais[index] = uri.toString()
                } else {
                    fotosAtuais.add(uri.toString())
                }
                u.copy(fotosServico = fotosAtuais)
            }
            repository.atualizarUsuario(userAtualizado)
            currentUser = userAtualizado
            atualizarVisibilidadeFotos()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPrestadorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        repository = UserRepository(this)
        currentUser = repository.getUsuarioLogado()

        setupEdgeToEdge()
        setupListeners()
        loadUserData()
    }

    private fun setupEdgeToEdge() {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.WHITE, Color.WHITE)
        )
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupListeners() {
        binding.icHome.setOnClickListener { finish() }

        binding.btnEditarFoto.setOnClickListener {
            fotoAlvo = 0
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
            binding.txtNomePrestador.text = user.usuario.ifEmpty { "UserName" }
            binding.txtCidade.text = user.local.ifEmpty { "Local" }
            binding.txtDesc.text = user.descricao.ifEmpty { "Adicione mais informações sobre você e seus serviços." }
            
            user.fotoHorizontalPrestador?.let { 
                binding.imgFotoHorizontalPrestador.load(it.toUri()) 
            }

            setupRecyclerView(user.servicos)
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
            repository.atualizarUsuario(userAtualizado)
            currentUser = userAtualizado
            (binding.rvServicos.adapter as? ServicoAdapter)?.apply {
                // Idealmente o adapter deveria lidar com a atualização da lista interna
                // mas para manter compatibilidade com o adapter atual:
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
                    imageView.visibility = android.view.View.VISIBLE
                    imageView.load(fotos[index].toUri())
                    imageView.alpha = 1.0f
                }
                isEditModeFotos && index == fotos.size && index < 4 -> {
                    imageView.visibility = android.view.View.VISIBLE
                    imageView.setImageResource(R.drawable.nenuma_imagem_selecionada)
                    imageView.alpha = 0.4f
                }
                else -> imageView.visibility = android.view.View.GONE
            }
        }
        binding.txtSemFotos.visibility = if (fotos.isEmpty() && !isEditModeFotos) android.view.View.VISIBLE else android.view.View.GONE
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
            val novaLista = u.fotosServico.filter { it.isNotEmpty() }.toMutableList()
            if (index < novaLista.size) {
                novaLista.removeAt(index)
                val userAtualizado = u.copy(fotosServico = novaLista)
                repository.atualizarUsuario(userAtualizado)
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
                    repository.atualizarUsuario(userAtualizado)
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
        val input = view.findViewById<TextInputEditText>(R.id.editLocal)
        input.setText(currentUser?.local ?: "")

        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_Bico_MaterialAlertDialog)
            .setTitle("Editar local de atuação")
            .setView(view)
            .setPositiveButton("Confirmar") { _, _ ->
                val novoLocal = input.text.toString()
                binding.txtCidade.text = novoLocal.ifEmpty { "Local" }
                currentUser?.let { u ->
                    val userAtualizado = u.copy(local = novoLocal)
                    repository.atualizarUsuario(userAtualizado)
                    currentUser = userAtualizado
                }
            }.setNegativeButton("Cancelar", null).show()
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
