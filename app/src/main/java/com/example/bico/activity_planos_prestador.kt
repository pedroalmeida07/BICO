package com.example.bico

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bico.Prestador.ConfiguracoesPrestador
import com.example.bico.Prestador.EditarPrestador
import com.example.bico.Prestador.HomePrestador
import com.example.bico.databinding.ActivityPlanosPrestadorBinding

class activity_planos_prestador : AppCompatActivity() {

    private lateinit var binding: ActivityPlanosPrestadorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlanosPrestadorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Botão voltar da barra superior (seta) -> Retorna para a página de configurações
        binding.sair.setOnClickListener {
            finish()
        }

        // Barra inferior
        // Ícone de casa -> Página inicial de prestador (HomePrestador)
        binding.icHome.setOnClickListener {
            val intent = Intent(this, HomePrestador::class.java)
            startActivity(intent)
        }

        // Ícone de engrenagem -> Página de configurações (ConfiguracoesPrestador)
        binding.icConfig.setOnClickListener {
            val intent = Intent(this, ConfiguracoesPrestador::class.java)
            startActivity(intent)
        }

        // Ícone de perfil -> Página de editar perfil (EditarPrestador)
        binding.icUserBarra.setOnClickListener {
            val intent = Intent(this, EditarPrestador::class.java)
            startActivity(intent)
        }
    }
}
