package com.example.bico.Prestador

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.bico.R
import com.example.bico.UserRepository
import kotlinx.coroutines.launch

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

        carregarDadosUsuario()

        // Lógica para mostrar/esconder o card de serviços
        val cardServico = findViewById<CardView>(R.id.cardProximoServico)
        val txtSemServicos = findViewById<TextView>(R.id.txtSemServicos)

        // implementar funcao pra checar true/false, se tem serviços marcados ou nao
        val temServicos = false // retonar false -> Nao mostra servicos | retornar true -> mostra card de servico

        if (temServicos) {
            cardServico.visibility = View.VISIBLE
            txtSemServicos.visibility = View.GONE
        } else {
            cardServico.visibility = View.GONE
            txtSemServicos.visibility = View.VISIBLE
        }

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

    override fun onResume() {
        super.onResume()
        carregarDadosUsuario()
    }

    private fun carregarDadosUsuario() {
        val repository = UserRepository(this)
        lifecycleScope.launch {
            val usuario = repository.getUsuarioLogado()
            usuario?.let { 
                findViewById<TextView>(R.id.txtNomeUsuario).text = it.primeiroNome.ifEmpty { "Usuário" }
                val imgUser = findViewById<ImageView>(R.id.imgUser)
                val icUserBarra = findViewById<ImageView>(R.id.ic_user_barra)
                
                if (!it.fotoPerfil.isNullOrEmpty()) {
                    imgUser.load(it.fotoPerfil) {
                        crossfade(true)
                        placeholder(R.drawable.user)
                        error(R.drawable.user)
                    }
                    icUserBarra.load(it.fotoPerfil) {
                        crossfade(true)
                        placeholder(R.drawable.user)
                        error(R.drawable.user)
                        // Para o ícone da barra, podemos querer remover o tint se for uma foto
                        target { drawable ->
                            icUserBarra.setImageDrawable(drawable)
                            icUserBarra.colorFilter = null
                        }
                    }
                } else {
                    imgUser.setImageResource(R.drawable.user)
                    icUserBarra.setImageResource(R.drawable.user)
                }
            }
        }
    }
}