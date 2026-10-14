package com.example.bico.Prestador

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bico.databinding.FragmentConfiguracoesPrestadorBinding

class FragmentConfiguracoesPrestador : Fragment() {

    private var _binding: FragmentConfiguracoesPrestadorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfiguracoesPrestadorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutPrivacidade.setOnClickListener {
            val intent = Intent(requireContext(), PrivacidadePrestador::class.java)
            startActivity(intent)
        }

        binding.layoutAjuda.setOnClickListener {
            val intent = Intent(requireContext(), AjudaPrestador::class.java)
            startActivity(intent)
        }

        binding.layoutEditarPerfil.setOnClickListener {
            val intent = Intent(requireContext(), EditarPrestador::class.java)
            startActivity(intent)
        }

        binding.layoutImpulso.setOnClickListener {
            val intent = Intent(requireContext(), PlanosPrestador::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
