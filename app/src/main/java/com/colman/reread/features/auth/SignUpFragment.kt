package com.colman.reread.features.auth

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import com.colman.reread.R
import com.colman.reread.base.loadBitmapFromUri
import com.colman.reread.data.repository.RemoteCountryRepository
import com.colman.reread.databinding.FragmentSignUpBinding
import com.colman.reread.model.Country
import com.colman.reread.model.User

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()
    private var selectedProfileImage: Bitmap? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri ?: return@registerForActivityResult
        loadProfileImageFromUri(uri)
    }

    private fun loadProfileImageFromUri(uri: Uri) {
        val bitmap = loadBitmapFromUri(requireContext(), uri)
        if (bitmap != null) {
            selectedProfileImage = bitmap
            binding.ivProfileImagePreview.setImageBitmap(bitmap)
        } else {
            selectedProfileImage = null
            binding.ivProfileImagePreview.setImageResource(R.drawable.ic_person)
            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupImagePicker()
        setupDropdowns()
        setupListeners()
        observeViewModel()
    }

    private fun setupImagePicker() {
        binding.btnSelectProfileImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

    private fun setupDropdowns() {
        val countryInput = binding.etCountry.editText as? AutoCompleteTextView

        viewLifecycleOwner.lifecycleScope.launch {
            val countries: List<Country> = RemoteCountryRepository.shared.getCountries()
            val countryNames = countries.mapNotNull { it.name }.sorted()
            val countryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, countryNames)
            countryInput?.setAdapter(countryAdapter)
        }
        countryInput?.threshold = 1

        countryInput?.setOnClickListener { countryInput.showDropDown() }
        countryInput?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) countryInput.showDropDown()
        }
    }

    private fun setupListeners() {
        binding.btnSignUp.setOnClickListener {
            if (validateInputs()) {
                val name = binding.etName.editText?.text?.toString().orEmpty().trim()
                val email = binding.etEmail.editText?.text?.toString().orEmpty().trim()
                val password = binding.etPassword.editText?.text?.toString().orEmpty()
                val phone = binding.etPhone.editText?.text?.toString().orEmpty().trim()
                val country = binding.etCountry.editText?.text?.toString().orEmpty().trim()

                val user = User(
                    name = name,
                    email = email,
                    phone = phone,
                    country = country
                )
                viewModel.signup(user, password, selectedProfileImage)
            }
        }

        binding.tvGoToSignIn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.AuthState.Loading -> {
                    binding.btnSignUp.isEnabled = false
                }
                is AuthViewModel.AuthState.Success -> {
                    binding.btnSignUp.isEnabled = true
                    findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToHomeFragment())
                    viewModel.resetState()
                }
                is AuthViewModel.AuthState.Error -> {
                    binding.btnSignUp.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.btnSignUp.isEnabled = true
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        var valid = true
        val name = binding.etName.editText?.text?.toString().orEmpty().trim()
        val email = binding.etEmail.editText?.text?.toString().orEmpty().trim()
        val password = binding.etPassword.editText?.text?.toString().orEmpty()
        val phone = binding.etPhone.editText?.text?.toString().orEmpty().trim()
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

        if (phone.isEmpty()) {
            binding.etPhone.error = "Phone is required"
            valid = false
        } else {
            binding.etPhone.error = null
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
