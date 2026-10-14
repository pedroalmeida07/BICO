package com.example.bico.Prestador

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.bico.R
import com.example.bico.ServicoAdapter
import com.example.bico.UserRepository
import com.example.bico.databinding.FragmentEditarPrestadorBinding
import com.example.bico.model.Prestador
import com.example.bico.utils.IbgeClient
import com.example.bico.utils.ImageUtils
import com.example.bico.utils.NoAccentsAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentEditarPrestador : Fragment() {

    private var _binding: FragmentEditarPrestadorBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: UserRepository
    private var currentUser: Prestador? = null
    private val listaCidadesFormatadas = mutableListOf<String>()

    private var fotoAlvo: Int = 0
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarPrestadorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = UserRepository(requireContext())

        setupListeners()
        loadUserData()
        carregarCidadesIbge()
    }

    private fun loadUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val user = repository.usuarioLogado()
            if (user is Prestador) {
                currentUser = user
                preencherCampos(user)
            }
        }
    }

    private fun preencherCampos(prestador: Prestador) {
        binding.txtNomePrestador.text = (prestador.usuario ?: "").ifEmpty { "UserName" }
        binding.txtDesc.text = (prestador.descricao ?: "").ifEmpty { "Adicione mais informações sobre você e seus serviços." }
        binding.txtCidade.text = (prestador.local ?: "").ifEmpty { "Local" }

        if (!prestador.fotoPerfil.isNullOrEmpty()) {
            binding.fotoPerfil.load(prestador.fotoPerfil) {
                crossfade(true)
                placeholder(R.drawable.nenuma_imagem_selecionada)
                error(R.drawable.nenuma_imagem_selecionada)
            }
        } else {
            binding.fotoPerfil.setImageResource(R.drawable.nenuma_imagem_selecionada)
        }

        val servicos = prestador.servicos ?: emptyList()
        val adapter = ServicoAdapter(servicos) { position ->
            removerServico(position)
        }
        binding.rvServicos.adapter = adapter

        atualizarVisibilidadeFotos()
    }

    private fun atualizarVisibilidadeFotos() {
        val fotos = currentUser?.fotosServico ?: emptyList()
        val temFotos = fotos.any { it.isNotEmpty() }

        if (temFotos) {
            binding.txtSemFotos.visibility = View.GONE
            binding.imgFoto1.visibility = View.VISIBLE
            binding.imgFoto2.visibility = View.VISIBLE
            binding.imgFoto3.visibility = View.VISIBLE
            binding.imgFoto4.visibility = View.VISIBLE

            carregarFotoSlot(binding.imgFoto1, fotos.getOrNull(0))
            carregarFotoSlot(binding.imgFoto2, fotos.getOrNull(1))
            carregarFotoSlot(binding.imgFoto3, fotos.getOrNull(2))
            carregarFotoSlot(binding.imgFoto4, fotos.getOrNull(3))
        } else {
            binding.txtSemFotos.visibility = View.VISIBLE
            binding.imgFoto1.visibility = View.GONE
            binding.imgFoto2.visibility = View.GONE
            binding.imgFoto3.visibility = View.GONE
            binding.imgFoto4.visibility = View.GONE
        }
    }

    private fun carregarFotoSlot(imageView: ImageView, path: String?) {
        if (!path.isNullOrEmpty()) {
            imageView.load(path) {
                crossfade(true)
                placeholder(R.drawable.ditador)
                error(R.drawable.ditador)
            }
        } else {
            imageView.setImageResource(R.drawable.ditador)
        }
    }

    private fun setupListeners() {
        binding.btnVoltar.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnEditarFoto.setOnClickListener {
            fotoAlvo = -1
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.icLapisNome.setOnClickListener { mostrarDialogEditarUsername() }
        binding.icLapisSobre.setOnClickListener { mostrarDialogEditarDescricao() }
        binding.icLapisServicos.setOnClickListener { mostrarDialogAdicionarServico() }
        binding.icLapisLocal.setOnClickListener { mostrarDialogEditarLocal() }

        binding.icLapisFotos.setOnClickListener {
            isEditModeFotos = !isEditModeFotos
            val msg = if (isEditModeFotos) "Modo Edição de Fotos ATIVADO" else "Modo Edição de Fotos DESATIVADO"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        binding.imgFoto1.setOnClickListener { handleFotoSlotClick(1) }
        binding.imgFoto2.setOnClickListener { handleFotoSlotClick(2) }
        binding.imgFoto3.setOnClickListener { handleFotoSlotClick(3) }
        binding.imgFoto4.setOnClickListener { handleFotoSlotClick(4) }
    }

    private fun handleFotoSlotClick(slotIndex: Int) {
        val fotosAtuais = (currentUser?.fotosServico ?: emptyList()).filter { it.isNotEmpty() }
        val photoUrl = fotosAtuais.getOrNull(slotIndex - 1)

        if (isEditModeFotos) {
            fotoAlvo = slotIndex
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            if (!photoUrl.isNullOrEmpty()) {
                mostrarDialogFotoExpandida(photoUrl)
            } else {
                fotoAlvo = slotIndex
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }

    private fun salvarImagemAtualizada(uri: Uri) {
        val uriPersistente = ImageUtils.persistirImagem(requireContext(), uri) ?: return

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
            viewLifecycleOwner.lifecycleScope.launch {
                repository.atualizarPrestador(userAtualizado.id ?: "", userAtualizado)
            }
            currentUser = userAtualizado
            atualizarVisibilidadeFotos()
        }
    }

    private fun mostrarDialogFotoExpandida(photoUrl: String) {
        val view = layoutInflater.inflate(R.layout.dialog_expandir_foto, null)
        val imgFoto = view.findViewById<ImageView>(R.id.imgExpandida)
        imgFoto.load(photoUrl)

        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Bico_MaterialAlertDialog)
            .setView(view)
            .setPositiveButton("Fechar", null)
            .show()
    }

    private fun mostrarDialogEditarUsername() {
        val view = layoutInflater.inflate(R.layout.dialog_editar_username, null)
        val edit = view.findViewById<TextInputEditText>(R.id.editUsername)
        edit.setText(currentUser?.usuario ?: "")

        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Bico_MaterialAlertDialog)
            .setTitle("Editar Username")
            .setView(view)
            .setPositiveButton("Salvar") { _, _ ->
                val novo = edit.text.toString().trim()
                if (novo.isNotEmpty()) {
                    currentUser?.let { u ->
                        val atualizado = u.copy(usuario = novo)
                        binding.txtNomePrestador.text = novo
                        currentUser = atualizado
                        viewLifecycleOwner.lifecycleScope.launch {
                            repository.atualizarPrestador(atualizado.id ?: "", atualizado)
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogEditarDescricao() {
        val view = layoutInflater.inflate(R.layout.dialog_editar_descricao, null)
        val edit = view.findViewById<TextInputEditText>(R.id.editDescricao)
        edit.setText(currentUser?.descricao ?: "")

        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Bico_MaterialAlertDialog)
            .setTitle("Editar Sobre")
            .setView(view)
            .setPositiveButton("Salvar") { _, _ ->
                val nova = edit.text.toString().trim()
                currentUser?.let { u ->
                    val atualizado = u.copy(descricao = nova)
                    binding.txtDesc.text = nova.ifEmpty { "Adicione mais informações sobre você e seus serviços." }
                    currentUser = atualizado
                    viewLifecycleOwner.lifecycleScope.launch {
                        repository.atualizarPrestador(atualizado.id ?: "", atualizado)
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogAdicionarServico() {
        val view = layoutInflater.inflate(R.layout.dialog_adicionar_servico, null)
        val editServico = view.findViewById<TextInputEditText>(R.id.editServico)

        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Bico_MaterialAlertDialog)
            .setTitle("Adicionar Serviço")
            .setView(view)
            .setPositiveButton("Adicionar") { _, _ ->
                val selecionado = editServico.text.toString().trim()
                if (selecionado.isNotEmpty()) {
                    currentUser?.let { u ->
                        val listaAtual = (u.servicos ?: emptyList()).toMutableList()
                        if (!listaAtual.contains(selecionado)) {
                            listaAtual.add(selecionado)
                            val atualizado = u.copy(servicos = listaAtual)
                            currentUser = atualizado
                            binding.rvServicos.adapter = ServicoAdapter(listaAtual) { pos -> removerServico(pos) }
                            viewLifecycleOwner.lifecycleScope.launch {
                                repository.atualizarPrestador(atualizado.id ?: "", atualizado)
                            }
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun removerServico(position: Int) {
        currentUser?.let { u ->
            val listaAtual = (u.servicos ?: emptyList()).toMutableList()
            if (position in listaAtual.indices) {
                listaAtual.removeAt(position)
                val atualizado = u.copy(servicos = listaAtual)
                currentUser = atualizado
                binding.rvServicos.adapter = ServicoAdapter(listaAtual) { pos -> removerServico(pos) }
                viewLifecycleOwner.lifecycleScope.launch {
                    repository.atualizarPrestador(atualizado.id ?: "", atualizado)
                }
            }
        }
    }

    private fun mostrarDialogEditarLocal() {
        val view = layoutInflater.inflate(R.layout.dialog_editar_local, null)
        val auto = view.findViewById<AutoCompleteTextView>(R.id.editLocal)

        val adapterCidades = NoAccentsAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, listaCidadesFormatadas)
        auto.setAdapter(adapterCidades)

        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Bico_MaterialAlertDialog)
            .setTitle("Editar Local de Atuação")
            .setView(view)
            .setPositiveButton("Salvar") { _, _ ->
                val novoLocal = auto.text.toString().trim()
                if (novoLocal.isNotEmpty()) {
                    currentUser?.let { u ->
                        val atualizado = u.copy(local = novoLocal)
                        binding.txtCidade.text = novoLocal
                        currentUser = atualizado
                        viewLifecycleOwner.lifecycleScope.launch {
                            repository.atualizarPrestador(atualizado.id ?: "", atualizado)
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun carregarCidadesIbge() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val listaMunicipios = withContext(Dispatchers.IO) {
                    IbgeClient.apiService.getMunicipios().map { it.nomeFormatado }
                }
                listaCidadesFormatadas.clear()
                listaCidadesFormatadas.addAll(listaMunicipios)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erro ao carregar lista de cidades", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
