package com.example.bico.Cliente

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.ItemPrestadorPesquisaBinding
import com.example.bico.model.Prestador
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrestadorAdapter(
    private var prestadores: List<Prestador> = emptyList(),
    private val onItemClick: (Prestador) -> Unit
) : RecyclerView.Adapter<PrestadorAdapter.PrestadorViewHolder>() {

    fun submitList(newList: List<Prestador>) {
        prestadores = newList
        notifyDataSetChanged()
    }

    class PrestadorViewHolder(val binding: ItemPrestadorPesquisaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrestadorViewHolder {
        val binding = ItemPrestadorPesquisaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PrestadorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrestadorViewHolder, position: Int) {
        val prestador = prestadores[position]
        val binding = holder.binding
        val context = holder.itemView.context

        // Nome + Serviço principal
        val nome = prestador.nome ?: "Prestador"
        val primeiroServico = prestador.servicos?.firstOrNull()
        val titulo = if (!primeiroServico.isNullOrEmpty()) {
            "$nome - $primeiroServico"
        } else {
            nome
        }
        binding.txtNomeEProfissao.text = titulo

        // Descrição / Sobre
        val desc = prestador.descricao ?: ""
        binding.txtDescricao.text = desc.ifEmpty { "Sem descrição informada." }

        // Local
        binding.txtLocal.text = prestador.local ?: ""

        // Avaliação
        binding.txtAvaliacao.text = formatarAvaliacao(prestador.notaMedia, prestador.totalAvaliacoes)

        // Foto de Perfil
        if (!prestador.fotoPerfil.isNullOrEmpty()) {
            binding.imgFotoPerfil.load(prestador.fotoPerfil) {
                crossfade(true)
                placeholder(R.drawable.user)
                error(R.drawable.user)
            }
        } else {
            binding.imgFotoPerfil.setImageResource(R.drawable.user)
        }

        // Verificar se é favorito
        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val prefName = if (uid.isNotEmpty()) "bico_favoritos_cache_$uid" else "bico_favoritos_cache"
        val prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

        var isFavorito = false
        val jsonMap = prefs.getString("map_favoritos", null)
        if (!jsonMap.isNullOrEmpty()) {
            try {
                val type = object : com.google.gson.reflect.TypeToken<Map<String, Prestador>>() {}.type
                val map: Map<String, Prestador> = com.google.gson.Gson().fromJson(jsonMap, type)
                isFavorito = map.values.any { 
                    (prestador.id != null && it.id == prestador.id) || 
                    (!prestador.usuario.isNullOrEmpty() && it.usuario.equals(prestador.usuario, ignoreCase = true)) ||
                    (!prestador.email.isNullOrEmpty() && it.email.equals(prestador.email, ignoreCase = true))
                }
            } catch (_: Exception) {}
        }

        if (!isFavorito) {
            val json = prefs.getString("json_favoritos", null)
            val ids = prefs.getStringSet("ids_favoritos", emptySet()) ?: emptySet()
            if (!json.isNullOrEmpty()) {
                try {
                    val type = object : com.google.gson.reflect.TypeToken<List<Prestador>>() {}.type
                    val favoritosList: List<Prestador> = com.google.gson.Gson().fromJson(json, type)
                    isFavorito = favoritosList.any { 
                        (prestador.id != null && it.id == prestador.id) || 
                        (!prestador.usuario.isNullOrEmpty() && it.usuario.equals(prestador.usuario, ignoreCase = true)) ||
                        (!prestador.email.isNullOrEmpty() && it.email.equals(prestador.email, ignoreCase = true))
                    }
                } catch (_: Exception) {}
            }
            if (!isFavorito && ids.isNotEmpty()) {
                isFavorito = ids.any { id ->
                    id == prestador.id || id.equals(prestador.usuario, ignoreCase = true) || id.equals(prestador.email, ignoreCase = true)
                }
            }
        }

        if (isFavorito) {
            binding.btnFavorito.setImageResource(R.drawable.ic_estrela_cheia)
        } else {
            binding.btnFavorito.setImageResource(R.drawable.ic_estrela_vazia)
        }

        // Clique no botão de favorito da lista
        binding.btnFavorito.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val repository = UserRepository(context)
                val prestadorId = prestador.id ?: prestador.usuario ?: ""
                if (isFavorito) {
                    repository.removerFavorito(prestadorId)
                    binding.btnFavorito.setImageResource(R.drawable.ic_estrela_vazia)
                    isFavorito = false
                    Toast.makeText(context, "Removido dos favoritos", Toast.LENGTH_SHORT).show()
                } else {
                    repository.adicionarFavorito(prestadorId)
                    binding.btnFavorito.setImageResource(R.drawable.ic_estrela_cheia)
                    isFavorito = true
                    Toast.makeText(context, "Prestador adicionado aos favoritos!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Clique no item
        holder.itemView.setOnClickListener {
            onItemClick(prestador)
        }
    }

    override fun getItemCount(): Int = prestadores.size

    private fun formatarAvaliacao(nota: Double?, total: Int?): String {
        val totalStr = total ?: 0
        if (nota == null || nota <= 0.0) {
            return "★ ★ ★ ★ ☆ ($totalStr)"
        }
        val cheias = nota.toInt().coerceIn(0, 5)
        val temMeia = (nota - cheias) >= 0.5 && cheias < 5
        val vazias = (5 - cheias - if (temMeia) 1 else 0).coerceAtLeast(0)

        val sb = StringBuilder()
        repeat(cheias) { sb.append("★ ") }
        if (temMeia) sb.append("½ ")
        repeat(vazias) { sb.append("☆ ") }
        sb.append("($totalStr)")
        return sb.toString().trim()
    }
}
