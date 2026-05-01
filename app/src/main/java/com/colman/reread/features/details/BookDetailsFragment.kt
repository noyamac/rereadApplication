package com.colman.reread.features.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.colman.reread.R
import com.colman.reread.databinding.FragmentBookDetailsBinding
import com.colman.reread.model.Book
import com.squareup.picasso.Picasso

class BookDetailsFragment : Fragment() {

    private var _binding: FragmentBookDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: BookDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val book = args.book
        setupUI(book)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupUI(book: Book) {
        binding.tvTitle.text = book.title
        binding.tvAuthor.text = getString(R.string.author_format, book.author)
        binding.tvPrice.text = getString(R.string.price_format, book.price)
        binding.tvDescription.text = book.description
        binding.tvSummary.text = book.summary
        binding.tvSellerName.text = book.sellerName
        binding.tvSellerEmail.text = book.sellerEmail

        val placeholder = R.drawable.default_book_cover
        if (book.imageUrl.isNotEmpty()) {
            Picasso.get()
                .load(book.imageUrl)
                .placeholder(placeholder)
                .error(placeholder)
                .into(binding.ivBookCover)
        } else {
            binding.ivBookCover.setImageResource(placeholder)
        }

        binding.btnContact.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${book.contactPhone}")
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
