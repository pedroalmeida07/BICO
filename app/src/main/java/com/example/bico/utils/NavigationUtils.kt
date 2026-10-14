package com.example.bico.utils

import android.app.Activity
import android.content.Intent
import com.example.bico.Cliente.MainClienteActivity
import com.example.bico.Prestador.MainPrestadorActivity
import com.example.bico.R
import com.google.android.material.bottomnavigation.BottomNavigationView

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
        val intent = Intent(this, MainClienteActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            putExtra("TARGET_ITEM_ID", item.itemId)
        }
        startActivity(intent)
        true
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
        val intent = Intent(this, MainPrestadorActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            putExtra("TARGET_ITEM_ID", item.itemId)
        }
        startActivity(intent)
        true
    }
}
