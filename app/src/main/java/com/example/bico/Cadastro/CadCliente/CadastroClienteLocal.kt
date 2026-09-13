package com.example.bico.Cadastro.CadCliente

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityCadastroClienteLocalBinding
import com.example.bico.utils.MaskWatcher

class CadastroClienteLocal : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroClienteLocalBinding

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

        // Aplicação das máscaras
        binding.editTextCEP.addTextChangedListener(MaskWatcher("#####-###", binding.editTextCEP))
        binding.editTextTelefone.addTextChangedListener(MaskWatcher("(##) #####-####", binding.editTextTelefone))

        binding.buttonAvancar.setOnClickListener {
            val cep = binding.editTextCEP.text.toString()
            val telefone = binding.editTextTelefone.text.toString()
            val numero = binding.editTextNumero.text.toString()
            val complemento = binding.editTextComplemento.text.toString()

            if (cep.isEmpty() || telefone.isEmpty() || numero.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha o CEP, Telefone e o número", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Salva no objeto temporário
            UserRepository.tempCliente = UserRepository.tempCliente.copy(
                local = "$cep, $numero - $complemento",
                cep = MaskWatcher.unmask(cep),
                telefone = MaskWatcher.unmask(telefone),
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