package com.example.bico.Prestador

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.bico.R
import com.example.bico.databinding.ActivityMainPrestadorBinding

class MainPrestadorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainPrestadorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        binding = ActivityMainPrestadorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.parentMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNav = binding.layoutBarraInferior.navPrestador

        if (savedInstanceState == null) {
            val targetItemId = intent.getIntExtra("TARGET_ITEM_ID", R.id.nav_home_prestador)
            bottomNav.selectedItemId = targetItemId
            selecionarAba(targetItemId)
        }

        bottomNav.setOnItemSelectedListener { item ->
            selecionarAba(item.itemId)
            true
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val bottomNav = binding.layoutBarraInferior.navPrestador
        val targetItemId = intent.getIntExtra("TARGET_ITEM_ID", R.id.nav_home_prestador)
        bottomNav.selectedItemId = targetItemId
        selecionarAba(targetItemId)
    }

    private fun selecionarAba(itemId: Int) {
        when (itemId) {
            R.id.nav_home_prestador -> trocarFragment(FragmentHomePrestador())
            R.id.nav_chat_prestador -> trocarFragment(FragmentChatPrestador())
            R.id.nav_config_prestador -> trocarFragment(FragmentConfiguracoesPrestador())
            R.id.nav_perfil_prestador -> trocarFragment(FragmentEditarPrestador())
        }
    }

    private fun trocarFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_prestador, fragment)
            .commit()
    }
}
