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

        // Lógica para mostrar/esconder o card de serviços
        val foto1 = findViewById<ImageView>(R.id.imgFoto1)
        val foto2 = findViewById<ImageView>(R.id.imgFoto2)
        val foto3 = findViewById<ImageView>(R.id.imgFoto3)
        val foto4 = findViewById<ImageView>(R.id.imgFoto4)
        val txtSemFoto = findViewById<TextView>(R.id.txtSemFotos)

        val txtLocal = findViewById<TextView>(R.id.txtCidade)
        txtLocal.text = usuario?.local ?: "Local"

        // Lógica para mostrar serviços com RecyclerView
        val rvServicos = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvServicos)
        val servicos = usuario?.servicos ?: emptyList()
        rvServicos.adapter = ServicoAdapter(servicos)

        // implementar funcao pra checar true/false, se tem fotos ou nao
        val temFotos = false // retonar false -> Nao mostra fotos | retornar true -> mostra fotos

        if (temFotos) {
            foto1.visibility = android.view.View.VISIBLE
            foto2.visibility = android.view.View.VISIBLE
            foto3.visibility = android.view.View.VISIBLE
            foto4.visibility = android.view.View.VISIBLE
            txtSemFoto.visibility = android.view.View.GONE
        } else {
            foto1.visibility = android.view.View.GONE
            foto2.visibility = android.view.View.GONE
            foto3.visibility = android.view.View.GONE
            foto4.visibility = android.view.View.GONE
            txtSemFoto.visibility = android.view.View.VISIBLE
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}