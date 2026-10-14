package com.example.bico.Cliente

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.bico.R
import com.example.bico.databinding.ItemPrestadorPesquisaBinding
import com.example.bico.model.Prestador

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

        // Nome + Serviço principal
        val nome = prestador.nome ?: "Prestador"
        val primeiroServico = prestador.servicos?.firstOrNull()
        val titulo = if (!primeiroServico.isNullOrEmpty()) {
            "$nome - $primeiroServico"
        } else {
            nome
        }
        binding.txtNomeEProfissao.text = titulo

        // Telefone
        binding.txtTelefone.text = prestador.telefone ?: ""

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
