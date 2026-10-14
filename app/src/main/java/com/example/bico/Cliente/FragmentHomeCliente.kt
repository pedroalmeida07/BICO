package com.example.bico.Cliente

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.bico.R
import com.example.bico.UserRepository
import com.example.bico.databinding.FragmentHomeClienteBinding
import kotlinx.coroutines.launch

class FragmentHomeCliente : Fragment() {

    // 1. Definição do ViewBinding seguro para Fragment
    private var _binding: FragmentHomeClienteBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: UserRepository

    // 2. Inflar o layout no onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeClienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 3. Toda a lógica do aplicativo entra no onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Usamos requireContext() no lugar de 'this'
        repository = UserRepository(requireContext())
        carregarDadosUsuario()

        // Clique na foto de perfil para editar
        binding.imgUser.setOnClickListener {
            val intent = Intent(requireContext(), EditarCliente::class.java)
            startActivity(intent)
        }

        // Exemplo de clique em prestador 1
        binding.pagPrestador1.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val usuarioBuscado = "hugoneves"
                val listaPrestadores = repository.buscarPrestadores(username = usuarioBuscado)

                val prestadorDoBanco = listaPrestadores?.find {
                    it.usuario?.equals(usuarioBuscado, ignoreCase = true) == true ||
                            it.nome?.contains(usuarioBuscado, ignoreCase = true) == true
                } ?: listaPrestadores?.firstOrNull()

                if (prestadorDoBanco != null) {
                    val intent = Intent(requireContext(), VerPrestadorCliente::class.java).apply {
                        putExtra("prestador", prestadorDoBanco)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Prestador não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        carregarDadosUsuario()
    }

    private fun carregarDadosUsuario() {
        // Usamos viewLifecycleOwner.lifecycleScope para Coroutines no Fragment
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

    // 4. Limpeza da View para evitar memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}