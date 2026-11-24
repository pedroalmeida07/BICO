package com.example.bico.Cadastro.CadCliente

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityCadastroClienteLocalBinding
import com.example.bico.utils.MaskWatcher
import com.example.bico.utils.ViaCepClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CadastroClienteLocal : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroClienteLocalBinding
    private var jobBuscaCep: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.WHITE,
                Color.WHITE
            )
        )

        binding = ActivityCadastroClienteLocalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCepListener()
        setupListeners()
    }

    private fun setupCepListener() {
        binding.editTextCEP.addTextChangedListener(MaskWatcher("#####-###", binding.editTextCEP))
        binding.editTextCEP.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val cepLimpo = MaskWatcher.unmask(s.toString())
                if (cepLimpo.length == 8) {
                    buscarEnderecoPorCep(cepLimpo)
                } else {
                    binding.editTextCEP.error = null
                }
            }
        })
    }

    private fun buscarEnderecoPorCep(cep: String) {
        jobBuscaCep?.cancel()
        jobBuscaCep = lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = ViaCepClient.apiService.getEndereco(cep)
                withContext(Dispatchers.Main) {
                    if (response.erro == true || response.localidade.isNullOrEmpty()) {
                        Toast.makeText(this@CadastroClienteLocal, getString(R.string.msg_cep_nao_encontrado), Toast.LENGTH_SHORT).show()
                        binding.editTextCEP.error = getString(R.string.err_cep_invalido_ou_nao_encontrado)
                    } else {
                        binding.editTextCEP.error = null
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CadastroClienteLocal, getString(R.string.msg_erro_buscar_cep), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.buttonAvancar.setOnClickListener {
            val cep = binding.editTextCEP.text.toString().trim()
            val numero = binding.editTextNumero.text.toString().trim()
            val complemento = binding.editTextComplemento.text.toString().trim()
            val cepLimpo = MaskWatcher.unmask(cep)

            if (cep.isEmpty() || numero.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_preencha_cep_numero), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (cepLimpo.length != 8) {
                binding.editTextCEP.error = getString(R.string.err_cep_invalido)
                return@setOnClickListener
            }

            if (binding.editTextCEP.error != null) {
                Toast.makeText(this, getString(R.string.msg_informe_cep_valido), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Salva no objeto temporário
            UserRepository.tempCliente = UserRepository.tempCliente.copy(
                local = "$cep, $numero - $complemento",
                cep = cep,
                numero = numero,
                complemento = complemento
            )

            val intent = Intent(this, CadastroClienteEmail::class.java)
            startActivity(intent)
        }

        binding.sair.setOnClickListener {
            finish()
        }
    }
}
