package com.colman.reread.features.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.colman.reread.R
import com.colman.reread.model.Book
import com.colman.reread.databinding.ItemBookBinding
import com.squareup.picasso.Picasso

class BookAdapter : ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BookViewHolder(private val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            binding.bookTitle.text = book.title
            binding.bookAuthor.text = book.author
            
            // Fixed warning: Use string resource with placeholders for formatting
            binding.bookPrice.text = itemView.context.getString(R.string.price_format, book.price)
            
            val placeholder = R.drawable.default_book_cover
            
            if (book.imageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(book.imageUrl)
                    .placeholder(placeholder)
                    .error(placeholder)
                    .into(binding.bookCover)
            } else {
                binding.bookCover.setImageResource(placeholder)
            }
        }
    }

    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }
}
