package com.colman.reread.features.myposts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.colman.reread.R
import com.colman.reread.databinding.FragmentMyPostsBinding
import com.colman.reread.features.home.BookAdapter

class MyPostsFragment : Fragment() {

    private var _binding: FragmentMyPostsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyPostsViewModel by viewModels()
    
    private val adapter = BookAdapter(
        onBookClick = { book ->
            val action = MyPostsFragmentDirections.actionMyPostsFragmentToBookDetailsFragment(book)
            findNavController().navigate(action)
        },
        onEditClick = { book ->
            val action = MyPostsFragmentDirections.actionMyPostsFragmentToEditPostFragment(book)
            findNavController().navigate(action)
        },
        onDeleteClick = { book ->
            viewModel.deleteBook(book)
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.recyclerView.adapter = adapter
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.books.observe(viewLifecycleOwner) { books ->
            adapter.submitList(books)
        }

        viewModel.deleteStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is MyPostsViewModel.DeleteStatus.Success -> {
                    Toast.makeText(context, getString(R.string.toast_delete_clicked, status.bookTitle), Toast.LENGTH_SHORT).show()
                    viewModel.resetDeleteStatus()
                }
                is MyPostsViewModel.DeleteStatus.Error -> {
                    Toast.makeText(context, getString(status.messageResId), Toast.LENGTH_SHORT).show()
                    viewModel.resetDeleteStatus()
                }
                MyPostsViewModel.DeleteStatus.Idle -> {
                    // Do nothing
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
