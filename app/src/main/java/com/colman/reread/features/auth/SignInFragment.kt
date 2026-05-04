package com.colman.reread.features.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Patterns
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.colman.reread.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

    private var binding: FragmentSignInBinding? = null
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        binding?.btnSignIn?.setOnClickListener {
            if (validateInputs()) {
                val email = binding?.etEmail?.editText?.text?.toString().orEmpty().trim()
                val password = binding?.etPassword?.editText?.text?.toString().orEmpty()
                viewModel.login(email, password)
            }
        }

        binding?.tvGoToSignUp?.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
        }
    }

    private fun observeViewModel() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.AuthState.Loading -> {
                    binding?.btnSignIn?.isEnabled = false
                }
                is AuthViewModel.AuthState.Success -> {
                    binding?.btnSignIn?.isEnabled = true
                    findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToHomeFragment())
                    viewModel.resetState()
                }
                is AuthViewModel.AuthState.Error -> {
                    binding?.btnSignIn?.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding?.btnSignIn?.isEnabled = true
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        var valid = true
        val email = binding?.etEmail?.editText?.text?.toString().orEmpty().trim()
        val password = binding?.etPassword?.editText?.text?.toString().orEmpty()

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding?.etEmail?.error = "Enter a valid email address"
            valid = false
        } else {
            binding?.etEmail?.error = null
        }

        if (password.length < 6) {
            binding?.etPassword?.error = "Password must be at least 6 characters"
            valid = false
        } else {
            binding?.etPassword?.error = null
        }

        return valid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
