package com.example.bico

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomePrestador : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //deixa a barra de status com icones pretos
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                ContextCompat.getColor(this, R.color.azul),
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.WHITE,
                Color.WHITE
            )
        )
        setContentView(R.layout.activity_home_prestador)

        //Mostra o primeiro nome do prestador na pagina home
        val repository = UserRepository(this)
        val usuario = repository.getUsuarioLogado()
        val txtNomeUsuario = findViewById<TextView>(R.id.txtNomeUsuario)
        txtNomeUsuario.text = usuario?.primeiroNome ?: "Usuário"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.ic_user_barra).setOnClickListener {
            val intent = Intent(this, EditarPrestador::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.imgUser).setOnClickListener {
            val intent = Intent(this, EditarPrestador::class.java)
            startActivity(intent)
        }
    }
}