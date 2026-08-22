package com.example.bico

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditarPrestador : AppCompatActivity() {
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
        setContentView(R.layout.activity_editar_prestador)

        findViewById<ImageView>(R.id.ic_home).setOnClickListener {
            finish()
        }

        //Mostra o UserName do prestador
        val repository = UserRepository(this)
        val usuario = repository.getUsuarioLogado()
        val txtNomeUsuario = findViewById<TextView>(R.id.txtNomePrestador)
        txtNomeUsuario.text = usuario?.usuario ?: "UserName"

        val txtLocal = findViewById<TextView>(R.id.txtCidade)
        txtLocal.text = usuario?.local ?: "Local"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}