package com.duke.elliot.biblereadinghabits.favorite_bible_verses

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.base.BaseFragment
import com.duke.elliot.biblereadinghabits.database.FavoriteBibleVerse
import com.duke.elliot.biblereadinghabits.databinding.FragmentFavoriteBibleVersesBinding
import com.duke.elliot.biblereadinghabits.databinding.ItemBibleVerseBinding
import com.duke.elliot.biblereadinghabits.main.MainApplication
import com.duke.elliot.biblereadinghabits.util.BibleVerseUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class FavoriteBibleVersesFragment: BaseFragment() {

    private lateinit var binding: FragmentFavoriteBibleVersesBinding
    private lateinit var viewModel: FavoriteBibleVersesViewModel
    private lateinit var recyclerViewAdapter: FavoriteBibleVerseAdapter

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite_bible_verses, container, false)

        setToolbarFont(
            binding.toolbar,
            R.style.NanumMyeonjoExtraBoldTextAppearance
        )
        setBackgroundColor(binding.toolbar)

        setDisplayHomeAsUpEnabled(binding.toolbar)
        setOnHomePressedCallback {
            findNavController().popBackStack()
        }

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
            } else {
                binding.emptyMessage.visibility = View.VISIBLE
                binding.favoriteBibleVerseRecyclerView.visibility = View.GONE
            }
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

                binding.bibleVerseInformation.setTextSize(TypedValue.COMPLEX_UNIT_SP, MainApplication.fontSize + 2)
                binding.bibleVerseWord.setTextSize(TypedValue.COMPLEX_UNIT_SP, MainApplication.fontSize )

                binding.bibleVerseContainer.setOnLongClickListener {
                    showPopupMenu(it, favoriteBibleVerse)
                    true
                }
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

        private fun showPopupMenu(view: View, favoriteBibleVerse: FavoriteBibleVerse) {
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.inflate(R.menu.menu_favorite_bible_verse)
            popupMenu.setOnMenuItemClickListener { item ->

                when (item.itemId) {
                    R.id.copyToClipboard -> {
                        BibleVerseUtil.copyToClipboard(requireActivity(), favoriteBibleVerse.toText())
                        true
                    }
                    R.id.share -> {
                        BibleVerseUtil.share(requireActivity(), favoriteBibleVerse.toText())
                        true
                    }
                    R.id.deleteFromFavorites -> {
                        BibleVerseUtil.deleteFromFavorites(
                            coroutineScope,
                            viewModel.favoriteBibleVerseDao,
                            favoriteBibleVerse
                        )
                        showToast(getString(R.string.delete_from_favorites_success))
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }

        private fun FavoriteBibleVerse.toText(): String {
            val bookString = viewModel.getBook(book)
            return "$bookString ${chapter}장 ${verse}절 \n" +
                    word
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