package com.colman.reread.features.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.colman.reread.databinding.FragmentSignUpBinding
import com.colman.reread.model.User

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AuthViewModel by viewModels()

    private val countryToCities = mapOf(
        "Israel" to listOf(
            "Tel Aviv", "Jerusalem", "Haifa", "Rishon LeZion", "Petah Tikva", "Ashdod",
            "Netanya", "Beer Sheva", "Holon", "Bnei Brak", "Ramat Gan", "Rehovot",
            "Bat Yam", "Ashkelon", "Herzliya", "Kfar Saba", "Hadera", "Modiin",
            "Nazareth", "Eilat"
        ),
        "United States" to listOf(
            "New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia",
            "San Antonio", "San Diego", "Dallas", "San Jose", "Austin", "Jacksonville",
            "Fort Worth", "Columbus", "Charlotte", "Indianapolis", "San Francisco", "Seattle",
            "Denver", "Boston"
        ),
        "United Kingdom" to listOf(
            "London", "Manchester", "Birmingham", "Leeds", "Glasgow", "Liverpool",
            "Bristol", "Sheffield", "Edinburgh", "Leicester", "Coventry", "Bradford",
            "Cardiff", "Belfast", "Nottingham", "Hull", "Newcastle", "Stoke-on-Trent",
            "Southampton", "Derby"
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDropdowns()
        setupListeners()
        observeViewModel()
    }

    private fun setupDropdowns() {
        val countryInput = binding.etCountry.editText as? AutoCompleteTextView
        val cityInput = binding.etCity.editText as? AutoCompleteTextView
        var activeCountryForCity: String? = null

        val countries = countryToCities.keys.toList()
        val countryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, countries)
        countryInput?.setAdapter(countryAdapter)
        countryInput?.threshold = 1
        cityInput?.threshold = 1

        countryInput?.setOnClickListener { countryInput.showDropDown() }
        countryInput?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) countryInput.showDropDown()
        }

        cityInput?.setOnClickListener {
            if (binding.etCity.isEnabled) cityInput.showDropDown()
        }
        cityInput?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.etCity.isEnabled) cityInput.showDropDown()
        }

        binding.etCity.isEnabled = false
        cityInput?.setText("", false)

        fun updateCityDropdown(selectedCountry: String) {
            val cities = countryToCities[selectedCountry].orEmpty()
            val cityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, cities)

            binding.etCountry.error = null
            binding.etCity.error = null
            binding.etCity.isEnabled = cities.isNotEmpty()
            cityInput?.setText("", false)
            cityInput?.setAdapter(cityAdapter)
            activeCountryForCity = selectedCountry
        }

        countryInput?.setOnItemClickListener { _, _, position, _ ->
            val selectedCountry = countries[position]
            updateCityDropdown(selectedCountry)
        }

        countryInput?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                val selectedCountry = s?.toString().orEmpty().trim()
                if (countryToCities.containsKey(selectedCountry)) {
                    if (activeCountryForCity != selectedCountry) {
                        updateCityDropdown(selectedCountry)
                    }
                } else {
                    binding.etCity.isEnabled = false
                    binding.etCity.error = null
                    cityInput?.setText("", false)
                    cityInput?.setAdapter(null)
                    activeCountryForCity = null
                }
            }
        })
    }

    private fun setupListeners() {
        binding.btnSignUp.setOnClickListener {
            if (validateInputs()) {
                val name = binding.etName.editText?.text?.toString().orEmpty().trim()
                val email = binding.etEmail.editText?.text?.toString().orEmpty().trim()
                val password = binding.etPassword.editText?.text?.toString().orEmpty()
                val phone = binding.etPhone.editText?.text?.toString().orEmpty().trim()
                val country = binding.etCountry.editText?.text?.toString().orEmpty().trim()
                val city = binding.etCity.editText?.text?.toString().orEmpty().trim()

                val user = User(
                    name = name,
                    email = email,
                    phone = phone,
                    country = country,
                    city = city
                )
                viewModel.signup(user, password)
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
        val city = binding.etCity.editText?.text?.toString().orEmpty().trim()

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
        } else if (!countryToCities.containsKey(country)) {
            binding.etCountry.error = "Please select a country from the list"
            valid = false
        } else {
            binding.etCountry.error = null
        }

        if (city.isEmpty()) {
            binding.etCity.error = "Please select a city"
            valid = false
        } else if (!countryToCities[country].orEmpty().contains(city)) {
            binding.etCity.error = "Please select a city from the list"
            valid = false
        } else {
            binding.etCity.error = null
        }

        return valid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
