package com.example.bico.Cliente

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
import com.example.bico.databinding.ActivityMainClienteBinding

class MainClienteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainClienteBinding

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

        binding = ActivityMainClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.parentMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNav = binding.layoutBarraInferior.navCliente

        if (savedInstanceState == null) {
            val targetItemId = intent.getIntExtra("TARGET_ITEM_ID", R.id.nav_home)
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
        val bottomNav = binding.layoutBarraInferior.navCliente
        val targetItemId = intent.getIntExtra("TARGET_ITEM_ID", R.id.nav_home)
        bottomNav.selectedItemId = targetItemId
        selecionarAba(targetItemId)
    }

    private fun selecionarAba(itemId: Int) {
        when (itemId) {
            R.id.nav_home -> trocarFragment(FragmentHomeCliente())
            R.id.nav_pesquisa -> trocarFragment(FragmentPesquisaCliente())
            R.id.nav_chat -> trocarFragment(FragmentChatCliente())
            R.id.nav_config -> trocarFragment(FragmentConfiguracaoCliente())
        }
    }

    private fun trocarFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_cliente, fragment)
            .commit()
    }
}
