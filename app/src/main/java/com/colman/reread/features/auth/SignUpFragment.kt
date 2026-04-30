package com.colman.reread.features.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.colman.reread.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countries = listOf("Israel", "United States", "United Kingdom")
        val countryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, countries)
        (binding.etCountry.editText as? AutoCompleteTextView)?.setAdapter(countryAdapter)

        binding.btnSignUp.setOnClickListener {
            if (validateInputs()) {
                findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToHomeFragment())
            }
        }

        binding.tvGoToSignIn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun validateInputs(): Boolean {
        var valid = true
        val name = binding.etName.editText?.text?.toString().orEmpty().trim()
        val email = binding.etEmail.editText?.text?.toString().orEmpty().trim()
        val password = binding.etPassword.editText?.text?.toString().orEmpty()
        val country = binding.etCountry.editText?.text?.toString().orEmpty().trim()

        if (name.isEmpty()) {
            binding.etName.error = "Name is required"
            valid = false
        } else {
            binding.etName.error = null
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Enter a valid email address"
            valid = false
        } else {
            binding.etEmail.error = null
        }

        if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            valid = false
        } else {
            binding.etPassword.error = null
        }

        if (country.isEmpty()) {
            binding.etCountry.error = "Please select a country"
            valid = false
        } else {
            binding.etCountry.error = null
        }

        return valid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
