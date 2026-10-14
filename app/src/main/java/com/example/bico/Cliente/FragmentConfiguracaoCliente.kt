package com.example.bico.Cliente

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bico.databinding.FragmentConfiguracaoClienteBinding

class FragmentConfiguracaoCliente : Fragment() {

    private var _binding: FragmentConfiguracaoClienteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfiguracaoClienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutPrivacidade.setOnClickListener {
            val intent = Intent(requireContext(), PrivacidadeCliente::class.java)
            startActivity(intent)
        }

        binding.layoutEditarPerfil.setOnClickListener {
            val intent = Intent(requireContext(), EditarCliente::class.java)
            startActivity(intent)
        }

        binding.layoutFavoritos.setOnClickListener {
            val intent = Intent(requireContext(), FavoritosCliente::class.java)
            startActivity(intent)
        }

        binding.layoutAjuda.setOnClickListener {
            val intent = Intent(requireContext(), AjudaCliente::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
