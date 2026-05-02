package com.colman.reread.features.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.colman.reread.R
import com.colman.reread.data.repository.UserRepository
import com.colman.reread.databinding.FragmentProfileBinding
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()

        binding.btnLogout.setOnClickListener {
            UserRepository.shared.signOut()
            findNavController().navigate(
                R.id.signInFragment,
                bundleOf(),
                navOptions {
                    popUpTo(R.id.nav_graph) {
                        inclusive = true
                    }
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCurrentUser()
    }

    private fun setupListeners() {
        binding.btnEditProfile.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment()
            findNavController().navigate(action)
        }
    }

    private fun observeViewModel() {
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            if (user == null) return@observe

            binding.tvName.text = user.name
            binding.tvEmail.text = user.email
            binding.tvPhone.text = user.phone
            binding.tvCountry.text = user.country
            binding.tvCity.text = user.city
            
            if (user.profileImageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(user.profileImageUrl)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(binding.ivProfileImage)
            } else {
                binding.ivProfileImage.setImageResource(R.drawable.ic_person)
            }
        }

        viewModel.authRequired.observe(viewLifecycleOwner) { required ->
            if (required) {
                findNavController().navigate(
                    R.id.signInFragment,
                    bundleOf(),
                    navOptions {
                        popUpTo(R.id.nav_graph) {
                            inclusive = true
                        }
                    }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
