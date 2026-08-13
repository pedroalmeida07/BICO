package com.example.bico

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referenciando os elementos da UI
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)
        val txtCadastrarPrestador = findViewById<TextView>(R.id.txtCadastrarPrestador)

        // Configurando a navegabilidade
        btnEntrar.setOnClickListener {
            val intent = Intent(this, PaginaLogin::class.java)
            startActivity(intent)
            finish()
        }

        btnCadastrar.setOnClickListener {
            // Supondo que CadastroClienteEmail seja a primeira tela de cadastro de cliente
            val intent = Intent(this, CadastroClienteEmail::class.java)
            startActivity(intent)
            finish()
        }

        txtCadastrarPrestador.setOnClickListener {
            val intent = Intent(this, CadastroPrestadorNome::class.java)
            startActivity(intent)
            finish()
        }
    }
}
