package com.colman.reread.features.profile

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.colman.reread.R
import com.colman.reread.base.loadBitmapFromUri
import com.colman.reread.data.repository.RemoteCountryRepository
import com.colman.reread.databinding.FragmentEditProfileBinding
import com.colman.reread.model.Country
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditProfileViewModel by viewModels()
    private var selectedProfileImage: Bitmap? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri ?: return@registerForActivityResult
        loadImageFromUri(uri)
    }

    private fun loadImageFromUri(uri: Uri) {
        val bitmap = loadBitmapFromUri(requireContext(), uri)
        if (bitmap != null) {
            selectedProfileImage = bitmap
            binding.ivProfileImage.setImageBitmap(bitmap)
        } else {
            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDropdowns()
        observeViewModel()
        setupListeners()
    }

    private fun setupDropdowns() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val countries: List<Country> = RemoteCountryRepository.shared.getCountries()
                val countryNames = countries.mapNotNull { it.name }.sorted()
                val countryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, countryNames)
                binding.actvCountry.setAdapter(countryAdapter)
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load countries", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.actvCountry.threshold = 1
        binding.actvCountry.setOnClickListener { binding.actvCountry.showDropDown() }
        binding.actvCountry.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.actvCountry.showDropDown()
        }
    }

    private fun observeViewModel() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.etName.setText(user.name)
            binding.etPhone.setText(user.phone)
            binding.actvCountry.setText(user.country, false)
            updateProfileImage(user.profileImageUrl)
        }

        viewModel.updateStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is EditProfileViewModel.UpdateStatus.Success -> {
                    Toast.makeText(context, getString(R.string.success_profile_update), Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is EditProfileViewModel.UpdateStatus.Error -> {
                    Toast.makeText(context, status.message, Toast.LENGTH_SHORT).show()
                }
                is EditProfileViewModel.UpdateStatus.Idle -> Unit
            }
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            viewModel.saveProfile(
                name = binding.etName.text.toString(),
                phone = binding.etPhone.text.toString(),
                country = binding.actvCountry.text.toString(),
                profileImage = selectedProfileImage
            )
        }

        binding.btnChangeImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

    private fun updateProfileImage(url: String) {
        if (url.isNotEmpty()) {
            Picasso.get()
                .load(url)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(binding.ivProfileImage)
        } else {
            binding.ivProfileImage.setImageResource(R.drawable.ic_person)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
