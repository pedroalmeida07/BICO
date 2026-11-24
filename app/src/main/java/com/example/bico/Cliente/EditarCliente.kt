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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.bico.Login.PaginaLogin
import com.example.bico.MainActivity
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityEditarClienteBinding
import com.example.bico.model.Cliente
import com.example.bico.utils.MaskWatcher
import com.example.bico.utils.setupBottomNavCliente
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import android.widget.TextView
import kotlinx.coroutines.launch

class EditarCliente : AppCompatActivity() {

    private lateinit var binding: ActivityEditarClienteBinding
    private lateinit var repository: UserRepository
    private var currentUser: Cliente? = null

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
                activityTitle = getString(R.string.title_recortar_foto)
                cropMenuCropButtonTitle = getString(R.string.btn_concluir)
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

        setupBottomNavCliente(binding.layoutBarraInferior.navCliente, 0)
        setupEdgeToEdge()
        setupMasks()
        loadUserData()
        setupListeners()
    }

    private fun setupMasks() {
        binding.editTextCep.addTextChangedListener(MaskWatcher("#####-###", binding.editTextCep))
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
            val user = repository.usuarioLogado(forceRefresh = true)
            if (user is Cliente) {
                currentUser = user
                binding.editTextNome.setText(user.nome)
                binding.editTextEmail.setText(user.email)
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

        binding.btnExcluir.setOnClickListener {
            excluirConta()
        }

        binding.btnSair.setOnClickListener {
            repository.deslogar()
            val intent = Intent(this, PaginaLogin::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }

    private fun excluirConta() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_alerta, null)

        val txtMessage = view.findViewById<TextView>(R.id.textviewMessage)
        val btnOk = view.findViewById<MaterialButton>(R.id.buttonOk)

        txtMessage.text = getString(R.string.dialog_confirmar_exclusao_conta_msg)

        btnOk.setOnClickListener {
            bottomSheetDialog.dismiss()
            lifecycleScope.launch {
                val sucesso = repository.deletarCliente()
                if (sucesso) {
                    Toast.makeText(this@EditarCliente, getString(R.string.msg_conta_excluida_sucesso), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@EditarCliente, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@EditarCliente, getString(R.string.msg_erro_excluir_conta), Toast.LENGTH_SHORT).show()
                }
            }
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun atualizarFotoPerfil(uri: Uri) {
        val user = currentUser
        if (user == null) {
            Toast.makeText(this, getString(R.string.msg_erro_processar_imagem), Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val url = repository.enviarImagem(uri)
            if (url == null) {
                Toast.makeText(this@EditarCliente, getString(R.string.msg_erro_processar_imagem), Toast.LENGTH_SHORT).show()
                return@launch
            }

            val userAtualizado = user.copy(fotoPerfil = url)

            if (repository.atualizarCliente(userAtualizado.id ?: "", userAtualizado)) {
                currentUser = userAtualizado
                binding.fotoPerfil.load(url) {
                    crossfade(true)
                    placeholder(R.drawable.user)
                    error(R.drawable.user)
                }
            } else {
                Toast.makeText(this@EditarCliente, getString(R.string.msg_erro_salvar_alteracoes), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun salvarAlteracoes() {
        val nome = binding.editTextNome.text.toString()
        val email = binding.editTextEmail.text.toString()
        val cep = binding.editTextCep.text.toString()
        val numero = binding.editTextNumero.text.toString()
        val complemento = binding.editTextComplemento.text.toString()

        if (nome.isEmpty()) {
            binding.editTextNome.error = getString(R.string.err_nome_vazio)
            return
        }

        if (email.isEmpty()) {
            binding.editTextEmail.error = getString(R.string.err_email_vazio)
            return
        }



        currentUser?.let { user ->
            val userAtualizado = user.copy(
                nome = nome,
                cep = cep,
                numero = numero,
                complemento = complemento,
                local = "${binding.editTextCep.text}, $numero - $complemento"
            )

            lifecycleScope.launch {
                val emailMudou = email != (user.email ?: "")
                if (emailMudou) {
                    val emailAtualizado = repository.atualizarEmailCliente(email)
                    if (emailAtualizado) {
                        Toast.makeText(
                            this@EditarCliente,
                            getString(R.string.msg_email_alterado_sucesso),
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(this@EditarCliente, PaginaLogin::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                        return@launch
                    } else {
                        Toast.makeText(this@EditarCliente, getString(R.string.msg_erro_salvar_email), Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                }

                val sucesso = repository.atualizarCliente(userAtualizado.id ?: "", userAtualizado)
                if (sucesso) {
                    currentUser = userAtualizado
                    Toast.makeText(this@EditarCliente, getString(R.string.msg_dados_atualizados_sucesso), Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditarCliente, getString(R.string.msg_erro_salvar_alteracoes), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
