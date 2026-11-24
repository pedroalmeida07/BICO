package com.example.bico

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.bico.Cadastro.CadCliente.CadastroClienteNome
import com.example.bico.Cadastro.CadPrestador.CadastroPrestadorNome
import com.example.bico.Cliente.HomeCliente
import com.example.bico.Login.PaginaLogin
import com.example.bico.Prestador.HomePrestador
import com.example.bico.databinding.ActivityMainBinding
import com.example.bico.model.Cliente
import com.example.bico.model.Prestador
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isCheckingAuth = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { isCheckingAuth }
        super.onCreate(savedInstanceState)

        // Animação de saída: fade out
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            val splashScreenView = splashScreenViewProvider.view
            val fadeOut = ObjectAnimator.ofFloat(
                splashScreenView,
                View.ALPHA,
                1f, 0f
            )
            fadeOut.interpolator = AccelerateInterpolator()
            fadeOut.duration = 300L

            fadeOut.doOnEnd { splashScreenViewProvider.remove() }
            fadeOut.start()
        }

        //deixa a barra de status com icones pretos
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

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAuth()

        // Evento de clique
        binding.btnEntrar.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, PaginaLogin::class.java)
            startActivity(intent) // Inicia a nova tela
        }
        binding.btnCadastrar.setOnClickListener {
            UserRepository.resetTempUser()
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, CadastroClienteNome::class.java)
            startActivity(intent) // Inicia a nova tela
        }
        binding.txtCadastrarPrestador.setOnClickListener {
            UserRepository.resetTempUser()
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, CadastroPrestadorNome::class.java)
            startActivity(intent) // Inicia a nova tela
        }
    }

    private fun checkAuth() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            isCheckingAuth = false
        } else {
            lifecycleScope.launch {
                val repository = UserRepository(this@MainActivity)
                val usuario = repository.usuarioLogado()

                when (usuario) {
                    is Cliente -> {
                        val intent = Intent(this@MainActivity, HomeCliente::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    is Prestador -> {
                        val intent = Intent(this@MainActivity, HomePrestador::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    else -> {
                        isCheckingAuth = false
                    }
                }
            }
        }
    }
}
