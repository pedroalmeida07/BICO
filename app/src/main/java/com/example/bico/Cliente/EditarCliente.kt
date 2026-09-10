package com.example.bico.Cliente

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityEditarClienteBinding
import com.example.bico.model.User
import com.example.bico.utils.ImageUtils
import com.example.bico.utils.MaskWatcher
import kotlinx.coroutines.launch

class EditarCliente : AppCompatActivity() {

    private lateinit var binding: ActivityEditarClienteBinding
    private lateinit var repository: UserRepository
    private var currentUser: User? = null

    // Lançador para o Recortador de Imagem
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uriContent = result.uriContent
            if (uriContent != null) {
                atualizarFotoPerfil(uriContent)
            }
        }
    }

    // Lançador para selecionar imagem da galeria
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val options = CropImageOptions().apply {
                aspectRatioX = 1
                aspectRatioY = 1
                fixAspectRatio = true
                guidelines = CropImageView.Guidelines.ON
                backgroundColor = Color.BLACK
                activityTitle = "Recortar Foto"
                cropMenuCropButtonTitle = "Concluir"
            }
            cropImage.launch(CropImageContractOptions(uri, options))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.WHITE, Color.WHITE)
        )
        binding = ActivityEditarClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = UserRepository(this)

        setupEdgeToEdge()
        loadUserData()
        setupListeners()
        setupMasks()
    }

    private fun setupMasks() {
        binding.editTextCep.addTextChangedListener(MaskWatcher("#####-###", binding.editTextCep))
        binding.editTextTelefone.addTextChangedListener(MaskWatcher("(##) #####-####", binding.editTextTelefone))
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.parentMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            currentUser = repository.getUsuarioLogado()
            currentUser?.let { user ->
                binding.editTextNome.setText(user.nome)
                binding.editTextTelefone.setText(user.telefone)
                binding.editTextCep.setText(user.cep)
                binding.editTextNumero.setText(user.numero)
                binding.editTextComplemento.setText(user.complemento)

                if (!user.fotoPerfil.isNullOrEmpty()) {
                    binding.fotoPerfil.load(user.fotoPerfil) {
                        crossfade(true)
                        placeholder(R.drawable.user)
                        error(R.drawable.user)
                    }
                } else {
                    binding.fotoPerfil.setImageResource(R.drawable.user)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.sair.setOnClickListener { finish() }

        binding.btnEditarFoto.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnSalvar.setOnClickListener {
            salvarAlteracoes()
        }

        // Navegação da barra inferior
        binding.icHome.setOnClickListener {
            val intent = Intent(this, HomeCliente::class.java)
            startActivity(intent)
            finish()
        }
        binding.icPesquisa.setOnClickListener {
            val intent = Intent(this, PesquisaCliente::class.java)
            startActivity(intent)
            finish()
        }
        // Chat e Config já podem estar mapeados ou serem implementados depois, 
        // mantendo consistência com o ic_config_laranja (ativa)
    }

    private fun atualizarFotoPerfil(uri: Uri) {
        val uriPersistente = ImageUtils.persistirImagem(this, uri)
        if (uriPersistente != null) {
            binding.fotoPerfil.load(uriPersistente)
            currentUser = currentUser?.copy(fotoPerfil = uriPersistente.toString())
        } else {
            Toast.makeText(this, "Erro ao processar imagem", Toast.LENGTH_SHORT).show()
        }
    }

    private fun salvarAlteracoes() {
        val nome = binding.editTextNome.text.toString()
        val telefone = MaskWatcher.unmask(binding.editTextTelefone.text.toString())
        val cep = MaskWatcher.unmask(binding.editTextCep.text.toString())
        val numero = binding.editTextNumero.text.toString()
        val complemento = binding.editTextComplemento.text.toString()

        if (nome.isEmpty()) {
            binding.editTextNome.error = "Nome não pode ser vazio"
            return
        }

        currentUser?.let { user ->
            val userAtualizado = user.copy(
                nome = nome,
                telefone = telefone,
                cep = cep,
                numero = numero,
                complemento = complemento,
                local = "${binding.editTextCep.text}, $numero - $complemento"
            )

            lifecycleScope.launch {
                val sucesso = repository.atualizarUsuario(userAtualizado.id ?: "", userAtualizado)
                if (sucesso) {
                    currentUser = userAtualizado
                    Toast.makeText(this@EditarCliente, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditarCliente, "Erro ao salvar alterações", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}