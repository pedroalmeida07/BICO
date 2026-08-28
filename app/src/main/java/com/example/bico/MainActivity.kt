package com.example.bico

import android.animation.ObjectAnimator
import android.graphics.Color
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.bico.Cadastro.CadCliente.CadastroClienteNome
import com.example.bico.Cadastro.CadPrestador.CadastroPrestadorNome
import com.example.bico.Login.PaginaLogin
import com.example.bico.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
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
}
