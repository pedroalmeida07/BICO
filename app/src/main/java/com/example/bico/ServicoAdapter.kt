package com.example.bico

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ServicoAdapter(private val servicos: List<String>) :
    RecyclerView.Adapter<ServicoAdapter.ServicoViewHolder>() {

    class ServicoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtServico: TextView = view.findViewById(R.id.txtServicoTag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_servico_tag, parent, false)
        return ServicoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServicoViewHolder, position: Int) {
        holder.txtServico.text = servicos[position]
    }

    override fun getItemCount() = servicos.size
}