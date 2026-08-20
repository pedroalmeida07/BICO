package com.example.bico

import android.animation.ObjectAnimator
import android.graphics.Color
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.bico.Cadastro.Cliente.CadastroClienteNome
import com.example.bico.Cadastro.Prestador.CadastroPrestadorNome
import com.example.bico.Login.PaginaLogin
import com.example.bico.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Animação de saída: desce um pouco e sobe
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            val splashScreenView = splashScreenViewProvider.view
            val slideDownUp = ObjectAnimator.ofFloat(
                splashScreenView,
                View.TRANSLATION_Y,
                0f, 800f, -splashScreenView.height.toFloat()
            )
            slideDownUp.interpolator = AnticipateInterpolator(1.5f)
            slideDownUp.duration = 1000L

            slideDownUp.doOnEnd { splashScreenViewProvider.remove() }
            slideDownUp.start()
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
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, CadastroClienteNome::class.java)
            startActivity(intent) // Inicia a nova tela
        }
        binding.txtCadastrarPrestador.setOnClickListener {
            // Criar o Intent para abrir a outra Activity
            val intent = Intent(this, CadastroPrestadorNome::class.java)
            startActivity(intent) // Inicia a nova tela
        }
    }
}
