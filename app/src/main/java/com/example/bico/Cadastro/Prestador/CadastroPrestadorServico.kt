package com.example.bico.Cadastro.Prestador

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityCadastroPrestadorServicoBinding

class CadastroPrestadorServico : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroPrestadorServicoBinding

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

        binding = ActivityCadastroPrestadorServicoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pega os serviços do strings.xml
        val servicos = resources.getStringArray(R.array.servicos)

        // Cria o adapter
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            servicos
        )

        // Liga o adapter ao AutoCompleteTextView
        binding.autoCompleteServico.setAdapter(adapter)

        // Só mostra as opções depois de 3 caracteres
        binding.autoCompleteServico.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val texto = s?.toString()?.trim() ?: ""

                if (texto.length >= 3) {
                    adapter.filter.filter(texto)
                    binding.autoCompleteServico.showDropDown()
                } else {
                    binding.autoCompleteServico.dismissDropDown()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.buttonAvancar.setOnClickListener {

            val tipo = binding.autoCompleteServico.text.toString()
            val local = binding.editTextLocal.text.toString()

            if (tipo.isEmpty() || local.isEmpty()) {
                Toast.makeText(
                    this,
                    "Preencha o serviço e a localização",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Salva no objeto temporário
            UserRepository.tempUser = UserRepository.tempUser.copy(
                servicos = listOf(tipo),
                local = local
            )

            val intent = Intent(
                this,
                CadastroPrestadorEmail::class.java
            )

            startActivity(intent)
        }

        binding.sair.setOnClickListener {
            finish()
        }
    }
}