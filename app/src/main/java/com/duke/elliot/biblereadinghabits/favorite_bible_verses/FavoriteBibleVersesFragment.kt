package com.duke.elliot.biblereadinghabits.favorite_bible_verses

import android.database.DatabaseUtils
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.base.BaseFragment
import com.duke.elliot.biblereadinghabits.database.FavoriteBibleVerse
import com.duke.elliot.biblereadinghabits.databinding.FragmentFavoriteBibleVersesBinding
import com.duke.elliot.biblereadinghabits.databinding.ItemBibleVerseBinding

class FavoriteBibleVersesFragment: BaseFragment() {

    private lateinit var binding: FragmentFavoriteBibleVersesBinding
    private lateinit var viewModel: FavoriteBibleVersesViewModel
    private lateinit var recyclerViewAdapter: FavoriteBibleVerseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite_bible_verses, container, false)

        setToolbarFont(
            binding.toolbar,
            R.style.NanumMyeonjoBoldTextAppearance
        )
        applyPrimaryThemeColor(binding.toolbar)

        val viewModelFactory = FavoriteBibleVersesViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[FavoriteBibleVersesViewModel::class.java]

        viewModel.favoriteBibleVerses.observe(viewLifecycleOwner, Observer { favoriteBibleVerses ->
            if (favoriteBibleVerses.isNotEmpty()) {
                if (!::recyclerViewAdapter.isInitialized) {
                    recyclerViewAdapter = FavoriteBibleVerseAdapter()
                    binding.favoriteBibleVerseRecyclerView.adapter = recyclerViewAdapter
                    binding.favoriteBibleVerseRecyclerView.scheduleLayoutAnimation()
                    recyclerViewAdapter.submitList(favoriteBibleVerses)
                } else {
                    binding.favoriteBibleVerseRecyclerView.scheduleLayoutAnimation()
                    recyclerViewAdapter.submitList(favoriteBibleVerses)
                }
            } else
                binding.emptyMessage.visibility = View.VISIBLE
        })

        return binding.root
    }

    inner class FavoriteBibleVerseAdapter:
        ListAdapter<FavoriteBibleVerse, RecyclerView.ViewHolder>(FavoriteBibleVerseDiffCallback()) {

        inner class ViewHolder constructor(val binding: ViewDataBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(binding: ItemBibleVerseBinding, favoriteBibleVerse: FavoriteBibleVerse) {
                val book = viewModel.getBook(favoriteBibleVerse.book)
                val chapter = favoriteBibleVerse.chapter
                val verse = favoriteBibleVerse.verse
                val word = favoriteBibleVerse.word
                val bibleVerseInformation = "$book ${chapter}장 ${verse}절"

                binding.bibleVerseInformation.text = bibleVerseInformation
                binding.bibleVerseWord.text = word
            }
        }

        private fun from(parent: ViewGroup): ViewHolder {
            val binding = ItemBibleVerseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return from(parent)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val favoriteBibleVerse = getItem(position)
            (holder as ViewHolder).bind(holder.binding as ItemBibleVerseBinding, favoriteBibleVerse)
        }
    }
}

class FavoriteBibleVerseDiffCallback: DiffUtil.ItemCallback<FavoriteBibleVerse>() {
    override fun areItemsTheSame(
        oldItem: FavoriteBibleVerse,
        newItem: FavoriteBibleVerse
    ): Boolean {
        return oldItem.book == newItem.book &&
                oldItem.chapter == newItem.chapter &&
                oldItem.verse == newItem.verse
    }

    override fun areContentsTheSame(
        oldItem: FavoriteBibleVerse,
        newItem: FavoriteBibleVerse
    ): Boolean {
        return oldItem == newItem
    }
}