package com.colman.reread.features.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.colman.reread.R
import com.colman.reread.databinding.FragmentEditProfileBinding
import com.squareup.picasso.Picasso

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        setupListeners()
    }

    private fun observeViewModel() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.etName.setText(user.name)
            binding.etPhone.setText(user.phone)
            binding.etCountry.setText(user.country)
            binding.etCity.setText(user.city)
            binding.etImageUrl.setText(user.profileImageUrl)
            
            updateProfileImage(user.profileImageUrl)
        }

        viewModel.updateStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, getString(R.string.success_profile_update), Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            viewModel.saveProfile(
                name = binding.etName.text.toString(),
                phone = binding.etPhone.text.toString(),
                country = binding.etCountry.text.toString(),
                city = binding.etCity.text.toString(),
                imageUrl = binding.etImageUrl.text.toString()
            )
        }

        binding.btnChangeImage.setOnClickListener {
            updateProfileImage(binding.etImageUrl.text.toString())
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
