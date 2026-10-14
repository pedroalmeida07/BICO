package com.example.bico.Prestador

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.FragmentHomePrestadorBinding
import kotlinx.coroutines.launch

class FragmentHomePrestador : Fragment() {

    private var _binding: FragmentHomePrestadorBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePrestadorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = UserRepository(requireContext())

        carregarDadosUsuario()

        val temServicos = false

        if (temServicos) {
            binding.cardProximoServico.visibility = View.VISIBLE
            binding.txtSemServicos.visibility = View.GONE
        } else {
            binding.cardProximoServico.visibility = View.GONE
            binding.txtSemServicos.visibility = View.VISIBLE
        }

        binding.imgUser.setOnClickListener {
            val intent = Intent(requireContext(), EditarPrestador::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        carregarDadosUsuario()
    }

    private fun carregarDadosUsuario() {
        viewLifecycleOwner.lifecycleScope.launch {
            val usuario = repository.usuarioLogado()
            usuario?.let {
                binding.txtNomeUsuario.text = it.primeiroNome.ifEmpty { "Usuário" }
                if (!it.fotoPerfil.isNullOrEmpty()) {
                    binding.imgUser.load(it.fotoPerfil) {
                        crossfade(true)
                        placeholder(R.drawable.user)
                        error(R.drawable.user)
                    }
                } else {
                    binding.imgUser.setImageResource(R.drawable.user)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
