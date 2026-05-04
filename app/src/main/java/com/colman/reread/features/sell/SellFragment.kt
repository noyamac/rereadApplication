package com.colman.reread.features.sell

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.colman.reread.R
import com.colman.reread.base.loadBitmapFromUri
import com.colman.reread.databinding.FragmentSellBinding
import com.google.android.material.snackbar.Snackbar

class SellFragment : Fragment() {

    private var binding: FragmentSellBinding? = null
    private var selectedBookImage: Bitmap? = null

    private val viewModel: SellViewModel by viewModels()

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri ?: return@registerForActivityResult
        loadBookImageFromUri(uri)
    }

    private fun loadBookImageFromUri(uri: Uri) {
        val bitmap = loadBitmapFromUri(requireContext(), uri)
        if (bitmap != null) {
            selectedBookImage = bitmap
            binding?.ivBookImagePreview?.setImageBitmap(bitmap)
        } else {
            selectedBookImage = null
            binding?.ivBookImagePreview?.setImageResource(R.drawable.default_book_cover)
            binding?.root?.let { Snackbar.make(it, "Failed to load image", Snackbar.LENGTH_SHORT).show() }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSellBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding?.btnSelectBookImage?.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding?.btnSubmit?.setOnClickListener {
            setSubmittingState(true)
            viewModel.postBook(
                title = binding?.etTitle?.text.toString(),
                author = binding?.etAuthor?.text.toString(),
                priceStr = binding?.etPrice?.text.toString(),
                description = binding?.etDescription?.text.toString(),
                summary = binding?.etSummary?.text.toString(),
                image = selectedBookImage
            )
        }
    }

    private fun observeViewModel() {
        viewModel.postStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is SellViewModel.PostStatus.Success -> {
                    setSubmittingState(false)
                    Toast.makeText(context, getString(R.string.success_post), Toast.LENGTH_LONG).show()
                    clearFields()
                    viewModel.resetStatus()
                }
                is SellViewModel.PostStatus.Error -> {
                    setSubmittingState(false)
                    Toast.makeText(context, getString(status.messageResId), Toast.LENGTH_SHORT).show()
                }
                is SellViewModel.PostStatus.Idle -> {
                    setSubmittingState(false)
                }
            }
        }
    }

    private fun setSubmittingState(isSubmitting: Boolean) {
        binding?.btnSubmit?.isEnabled = !isSubmitting
        binding?.submitLoadingSpinner?.visibility = if (isSubmitting) View.VISIBLE else View.GONE
        binding?.btnSubmit?.text = if (isSubmitting) "" else getString(R.string.btn_post_book)
    }

    private fun clearFields() {
        binding?.etTitle?.text?.clear()
        binding?.etAuthor?.text?.clear()
        binding?.etPrice?.text?.clear()
        binding?.etDescription?.text?.clear()
        binding?.etSummary?.text?.clear()
        selectedBookImage = null
        binding?.ivBookImagePreview?.setImageResource(R.drawable.default_book_cover)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
