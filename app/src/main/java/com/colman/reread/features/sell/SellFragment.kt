package com.colman.reread.features.sell

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.colman.reread.R
import com.colman.reread.databinding.FragmentSellBinding

class SellFragment : Fragment() {

    private var _binding: FragmentSellBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SellViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnSubmit.setOnClickListener {
            viewModel.postBook(
                title = binding.etTitle.text.toString(),
                author = binding.etAuthor.text.toString(),
                priceStr = binding.etPrice.text.toString(),
                description = binding.etDescription.text.toString(),
                summary = binding.etSummary.text.toString(),
                contactPhone = binding.etContactPhone.text.toString(),
                imageUrl = binding.etImageUrl.text.toString()
            )
        }
    }

    private fun observeViewModel() {
        viewModel.postStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is SellViewModel.PostStatus.Success -> {
                    Toast.makeText(context, getString(R.string.success_post), Toast.LENGTH_LONG).show()
                    clearFields()
                    viewModel.resetStatus()
                }
                is SellViewModel.PostStatus.Error -> {
                    Toast.makeText(context, getString(status.messageResId), Toast.LENGTH_SHORT).show()
                }
                is SellViewModel.PostStatus.Idle -> {
                    // TODO: implement create in server
                }
            }
        }
    }

    private fun clearFields() {
        binding.etTitle.text?.clear()
        binding.etAuthor.text?.clear()
        binding.etPrice.text?.clear()
        binding.etDescription.text?.clear()
        binding.etSummary.text?.clear()
        binding.etContactPhone.text?.clear()
        binding.etImageUrl.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
