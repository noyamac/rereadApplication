package com.colman.reread.features.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.colman.reread.R
import com.colman.reread.databinding.ItemBookBinding
import com.colman.reread.model.Book
import com.squareup.picasso.Picasso

class BookAdapter(
    private val onBookClick: (Book) -> Unit,
    private val onEditClick: ((Book) -> Unit)? = null,
    private val onDeleteClick: ((Book) -> Unit)? = null
) : ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding, onBookClick, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BookViewHolder(
        private val binding: ItemBookBinding,
        private val onBookClick: (Book) -> Unit,
        private val onEditClick: ((Book) -> Unit)?,
        private val onDeleteClick: ((Book) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(book: Book) {
            binding.bookTitle.text = book.title
            binding.bookAuthor.text = book.author
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

            binding.btnDetails.setOnClickListener {
                onBookClick(book)
            }

            if (onEditClick != null || onDeleteClick != null) {
                binding.btnOptions.visibility = View.VISIBLE
                binding.btnOptions.setOnClickListener { view ->
                    showPopupMenu(view, book)
                }
            } else {
                binding.btnOptions.visibility = View.GONE
            }
        }

        private fun showPopupMenu(view: View, book: Book) {
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.menu_book_item_actions, popup.menu)
            
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        onEditClick?.invoke(book)
                        true
                    }
                    R.id.action_delete -> {
                        onDeleteClick?.invoke(book)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
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
