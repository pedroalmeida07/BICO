package com.example.bico

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.EditText
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
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

        // Mostra a descrição do prestador
        val txtDesc = findViewById<TextView>(R.id.txtDesc)
        txtDesc.text = if (usuario?.descricao?.isNotEmpty() == true) {
            usuario.descricao
        } else {
            "Adicione mais informações sobre você e seus serviços."
        }

        // Clique para editar a descrição
        findViewById<ImageView>(R.id.ic_lapisSobre).setOnClickListener {
            val view = layoutInflater.inflate(R.layout.dialog_editar_descricao, null)
            val input = view.findViewById<TextInputEditText>(R.id.editDescricao)
            input.setText(usuario?.descricao ?: "")

            MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_Bico_MaterialAlertDialog)
                .setTitle("Editar Sobre")
                .setView(view)
                .setPositiveButton("Salvar") { _, _ ->
                    val novaDesc = input.text.toString()
                    txtDesc.text = if (novaDesc.isNotEmpty()) {
                        novaDesc
                    } else {
                        "Adicione mais informações sobre você e seus serviços."
                    }
                    
                    val userLogado = repository.getUsuarioLogado()
                    userLogado?.let { u ->
                        val userAtualizado = u.copy(descricao = novaDesc)
                        repository.atualizarUsuario(userAtualizado)
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

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
        val listaServicos = usuario?.servicos?.toMutableList() ?: mutableListOf()
        
        lateinit var adapter: ServicoAdapter
        adapter = ServicoAdapter(listaServicos) { position ->
            // Clique longo para remover
            MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_Bico_MaterialAlertDialog)
                .setTitle("Remover Serviço")
                .setMessage("Deseja remover \"${listaServicos[position]}\" dos seus serviços?")
                .setPositiveButton("Remover") { _, _ ->
                    listaServicos.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    adapter.notifyItemRangeChanged(position, listaServicos.size)

                    // Atualiza no banco de dados
                    val userLogado = repository.getUsuarioLogado()
                    userLogado?.let { u ->
                        val userAtualizado = u.copy(servicos = listaServicos.toList())
                        repository.atualizarUsuario(userAtualizado)
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
        rvServicos.adapter = adapter

        // Clique para adicionar novo serviço
        findViewById<ImageView>(R.id.ic_lapisServicos).setOnClickListener {
            val view = layoutInflater.inflate(R.layout.dialog_adicionar_servico, null)
            val input = view.findViewById<TextInputEditText>(R.id.editServico)

            MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_Bico_MaterialAlertDialog)
                .setTitle("Adicionar Serviço")
                .setView(view)
                .setPositiveButton("Adicionar") { _, _ ->
                    val novoServico = input.text.toString()
                    if (novoServico.isNotEmpty()) {
                        listaServicos.add(novoServico)
                        adapter.notifyItemInserted(listaServicos.size - 1)

                        // Atualiza no banco de dados
                        val userLogado = repository.getUsuarioLogado()
                        userLogado?.let { u ->
                            val userAtualizado = u.copy(servicos = listaServicos.toList())
                            repository.atualizarUsuario(userAtualizado)
                        }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

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