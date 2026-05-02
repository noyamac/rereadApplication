package com.colman.reread.features.myposts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.colman.reread.R
import com.colman.reread.databinding.FragmentEditPostBinding
import com.squareup.picasso.Picasso

class EditPostFragment : Fragment() {

    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditPostViewModel by viewModels()
    private val args: EditPostFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupListeners()
        observeViewModel()
    }

    private fun setupUI() {
        val book = args.book
        binding.etTitle.setText(book.title)
        binding.etAuthor.setText(book.author)
        binding.etPrice.setText(book.price.toString())
        binding.etDescription.setText(book.description)
        binding.etSummary.setText(book.summary)
        binding.etContactPhone.setText(book.contactPhone)
        binding.etImageUrl.setText(book.imageUrl)
        
        updateImagePreview(book.imageUrl)
    }

    private fun setupListeners() {
        binding.btnUpdate.setOnClickListener {
            viewModel.updateBook(
                id = args.book.id,
                title = binding.etTitle.text.toString(),
                author = binding.etAuthor.text.toString(),
                priceStr = binding.etPrice.text.toString(),
                description = binding.etDescription.text.toString(),
                summary = binding.etSummary.text.toString(),
                contactPhone = binding.etContactPhone.text.toString(),
                imageUrl = binding.etImageUrl.text.toString(),
                sellerName = args.book.sellerName,
                sellerEmail = args.book.sellerEmail
            )
        }

        binding.btnUpdatePreview.setOnClickListener {
            updateImagePreview(binding.etImageUrl.text.toString())
        }
    }

    private fun updateImagePreview(url: String) {
        val placeholder = R.drawable.default_book_cover
        if (url.isNotEmpty()) {
            Picasso.get()
                .load(url)
                .placeholder(placeholder)
                .error(placeholder)
                .into(binding.ivBookCover)
        } else {
            binding.ivBookCover.setImageResource(placeholder)
        }
    }

    private fun observeViewModel() {
        viewModel.updateStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, getString(R.string.success_update), Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
