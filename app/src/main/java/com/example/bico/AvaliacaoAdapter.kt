package com.example.bico

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bico.model.Avaliacao

class AvaliacaoAdapter(
    private val avaliacoes: List<Avaliacao>
) : RecyclerView.Adapter<AvaliacaoAdapter.AvaliacaoViewHolder>() {

    class AvaliacaoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNome: TextView = view.findViewById(R.id.txtNomeClienteAvaliacao)
        val txtNota: TextView = view.findViewById(R.id.txtNotaAvaliacao)
        val txtComentario: TextView = view.findViewById(R.id.txtComentarioAvaliacao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvaliacaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_avaliacao, parent, false)
        return AvaliacaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvaliacaoViewHolder, position: Int) {
        val item = avaliacoes[position]
        
        holder.txtNome.text = item.nomeAvaliador?.takeIf { it.isNotBlank() } ?: "Cliente"
        holder.txtNota.text = item.nota.toString()

        val comentario = item.comentario
        if (!comentario.isNullOrBlank()) {
            holder.txtComentario.text = comentario
            holder.txtComentario.visibility = View.VISIBLE
        } else {
            holder.txtComentario.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = avaliacoes.size
}
