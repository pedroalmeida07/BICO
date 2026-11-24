package com.example.bico.utils

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.example.bico.Cliente.ConfiguracaoCliente
import com.example.bico.Cliente.HomeCliente
import com.example.bico.Cliente.PesquisaCliente
import com.example.bico.Prestador.ConfiguracoesPrestador
import com.example.bico.Prestador.EditarPrestador
import com.example.bico.Prestador.HomePrestador
import com.example.bico.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Activity.avisarChatEmBreve() {
    Toast.makeText(this, R.string.msg_chat_em_breve, Toast.LENGTH_SHORT).show()
}

fun Activity.setupBottomNavCliente(bottomNav: BottomNavigationView, selectedItemId: Int = 0) {
    if (selectedItemId != 0) {
        bottomNav.selectedItemId = selectedItemId
    } else {
        bottomNav.menu.setGroupCheckable(0, true, false)
        for (i in 0 until bottomNav.menu.size()) {
            bottomNav.menu.getItem(i).isChecked = false
        }
        bottomNav.menu.setGroupCheckable(0, true, true)
    }

    bottomNav.setOnItemSelectedListener { item ->
        if (item.itemId == selectedItemId) return@setOnItemSelectedListener true
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, HomeCliente::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
                true
            }
            R.id.nav_pesquisa -> {
                val intent = Intent(this, PesquisaCliente::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
                true
            }
            R.id.nav_chat -> {
                avisarChatEmBreve()
                true
            }
            R.id.nav_config -> {
                val intent = Intent(this, ConfiguracaoCliente::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
                true
            }
            else -> false
        }
    }
}

fun Activity.setupBottomNavPrestador(bottomNav: BottomNavigationView, selectedItemId: Int = 0) {
    if (selectedItemId != 0) {
        bottomNav.selectedItemId = selectedItemId
    } else {
        bottomNav.menu.setGroupCheckable(0, true, false)
        for (i in 0 until bottomNav.menu.size()) {
            bottomNav.menu.getItem(i).isChecked = false
        }
        bottomNav.menu.setGroupCheckable(0, true, true)
    }

    bottomNav.setOnItemSelectedListener { item ->
        if (item.itemId == selectedItemId) return@setOnItemSelectedListener true
        when (item.itemId) {
            R.id.nav_home_prestador -> {
                val intent = Intent(this, HomePrestador::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
                true
            }
            R.id.nav_chat_prestador -> {
                avisarChatEmBreve()
                true
            }
            R.id.nav_config_prestador -> {
                val intent = Intent(this, ConfiguracoesPrestador::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
                true
            }
            R.id.nav_perfil_prestador -> {
                val intent = Intent(this, EditarPrestador::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
                true
            }
            else -> false
        }
    }
}
