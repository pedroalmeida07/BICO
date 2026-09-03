package com.example.bico.Cliente

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
import androidx.lifecycle.lifecycleScope
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.ActivityCadastroPrestadorEmailBinding
import com.example.bico.databinding.ActivityHomeClienteBinding
import kotlinx.coroutines.launch

class HomeCliente : AppCompatActivity() {

    private lateinit var binding: ActivityHomeClienteBinding

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
        binding = ActivityHomeClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Mostra o primeiro nome do cliente na pagina home
        val repository = UserRepository(this)
        lifecycleScope.launch {
            val usuario = repository.getUsuarioLogado()
            binding.txtNomeUsuario.text = usuario?.primeiroNome ?: "Usuário"
        }

        binding.icPesquisa.setOnClickListener {
            val intent = Intent(this, PesquisaCliente::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.parentMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.imgUser.setOnClickListener {
            val intent = Intent(this, EditarCliente::class.java)
            startActivity(intent)
        }
    }
}