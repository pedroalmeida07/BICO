package com.example.bico.Cliente

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.bico.R
import com.example.bico.databinding.ItemPrestadorDestaqueBinding
import com.example.bico.model.Prestador

class PrestadorDestaqueAdapter(
    private var prestadores: List<Prestador> = emptyList(),
    private val onItemClick: (Prestador) -> Unit
) : RecyclerView.Adapter<PrestadorDestaqueAdapter.PrestadorViewHolder>() {

    fun submitList(newList: List<Prestador>) {
        prestadores = newList
        notifyDataSetChanged()
    }

    class PrestadorViewHolder(val binding: ItemPrestadorDestaqueBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrestadorViewHolder {
        val binding = ItemPrestadorDestaqueBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PrestadorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrestadorViewHolder, position: Int) {
        val prestador = prestadores[position]
        val binding = holder.binding

        binding.txtNomeDestaque.text = prestador.nomeExibicao
        binding.txtServicoDestaque.text = prestador.servicos?.firstOrNull() ?: ""
        binding.txtLocalDestaque.text = prestador.local ?: ""
        binding.txtRatingDestaque.text = formatarAvaliacao(prestador.notaMedia, prestador.totalAvaliacoes)

        if (!prestador.fotoPerfil.isNullOrEmpty()) {
            binding.imgFotoDestaque.load(prestador.fotoPerfil) {
                crossfade(true)
                placeholder(R.drawable.user)
                error(R.drawable.user)
            }
        } else {
            binding.imgFotoDestaque.setImageResource(R.drawable.user)
        }

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
