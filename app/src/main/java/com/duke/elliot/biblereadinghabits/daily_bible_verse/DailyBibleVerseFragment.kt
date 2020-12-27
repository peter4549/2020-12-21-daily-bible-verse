package com.duke.elliot.biblereadinghabits.daily_bible_verse

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.base.BaseFragment
import com.duke.elliot.biblereadinghabits.daily_bible_verse.DailyBibleVerseUtil.IN_ORDER
import com.duke.elliot.biblereadinghabits.daily_bible_verse.DailyBibleVerseUtil.MY_BIBLE_VERSES
import com.duke.elliot.biblereadinghabits.daily_bible_verse.DailyBibleVerseUtil.POPULAR_BIBLE_VERSES
import com.duke.elliot.biblereadinghabits.daily_bible_verse.DailyBibleVerseUtil.RANDOM
import com.duke.elliot.biblereadinghabits.daily_bible_verse.drawer.DRAWER_MENU_ITEM_CONTENT
import com.duke.elliot.biblereadinghabits.daily_bible_verse.drawer.DRAWER_MENU_ITEM_SUBTITLE
import com.duke.elliot.biblereadinghabits.daily_bible_verse.drawer.DrawerMenuItem
import com.duke.elliot.biblereadinghabits.daily_bible_verse.drawer.DrawerMenuItemAdapter
import com.duke.elliot.biblereadinghabits.database.BibleVerse
import com.duke.elliot.biblereadinghabits.databinding.FragmentDailyBibleVerseDrawerBinding
import com.duke.elliot.biblereadinghabits.databinding.ItemBibleVerseBinding
import com.duke.elliot.biblereadinghabits.main.MainApplication
import com.duke.elliot.biblereadinghabits.util.BibleVerseUtil
import kotlinx.android.synthetic.main.layout_navigation_drawer.view.*
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

const val DAILY_BIBLE_VERSE_DRAWER_MENU_ITEM_ID = 1L

class DailyBibleVerseFragment: BaseFragment() {

    private lateinit var binding: FragmentDailyBibleVerseDrawerBinding
    private lateinit var viewModel: DailyBibleVerseViewModel

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    private lateinit var drawerMenuItemAdapter: DrawerMenuItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_daily_bible_verse_drawer,
            container,
            false
        )

        initDrawer()
        initOnScrollListener()

        setOptionsMenu(
            binding.fragmentDailyBibleVerse.toolbar,
            R.menu.menu_daily_bible_verse_fragment
        )

        setOnBackPressedCallback {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            else
                requireActivity().finish()
        }

        setOnHomePressedCallback {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            else
                binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        setOnOptionsItemSelectedListeners(
            R.id.favorite to {
                findNavController().navigate(
                    DailyBibleVerseFragmentDirections
                        .actionDailyBibleVerseFragmentToFavoriteBibleVersesFragment()
                )
            },
            R.id.settings to {
                showToast("Setting selected.")
            }
        )

        val viewModelFactory = DailyBibleVerseViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[DailyBibleVerseViewModel::class.java]

        when (DailyBibleVerseUtil.getRange(requireContext())) {
            POPULAR_BIBLE_VERSES -> setDisplayBibleVerses(viewModel.getDisplayPopularBibleVerses())
            MY_BIBLE_VERSES -> setDisplayBibleVerses(viewModel.getDisplayFavoriteBibleVerses())
            IN_ORDER -> setDisplayBibleVerses(viewModel.getDisplayInOrderBibleVerses())
            RANDOM -> setDisplayBibleVerses(viewModel.getDisplayRandomBibleVerses())
        }

        binding.fragmentDailyBibleVerse.readBible.setOnClickListener {
            findNavController().navigate(
                DailyBibleVerseFragmentDirections
                    .actionDailyBibleVerseFragmentToBibleReadingFragment()
            )
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setColor()
    }

    private fun initDrawer() {
        /** Daily Bible Verse Range */
        /** POPULAR_BIBLE_VERSES = 0
         * MY_BIBLE_VERSES = 1
         * RANDOM = 2
         * IN_ORDER = 3 */
        val title = when(DailyBibleVerseUtil.getRange(requireContext())) {
            POPULAR_BIBLE_VERSES -> getString(R.string.select_from_popular_bible_verses_title)
            MY_BIBLE_VERSES -> getString(R.string.select_from_favorites_title)
            RANDOM -> getString(R.string.select_at_random_title)
            IN_ORDER -> getString(R.string.select_in_order_title)
            else -> throw IllegalArgumentException("Invalid range.")
        }

        val description = when(DailyBibleVerseUtil.getRange(requireContext())) {
            POPULAR_BIBLE_VERSES -> getString(R.string.select_from_popular_bible_verses_description)
            MY_BIBLE_VERSES -> getString(R.string.select_from_favorites_description)
            RANDOM -> getString(R.string.select_at_random_description)
            IN_ORDER -> getString(R.string.select_in_order_description)
            else -> throw IllegalArgumentException("Invalid range.")
        }

        drawerMenuItemAdapter = DrawerMenuItemAdapter(
            arrayListOf(
                DrawerMenuItem(0L, DRAWER_MENU_ITEM_SUBTITLE, title = getString(R.string.daily_bible_verse)),
                DrawerMenuItem(
                    DAILY_BIBLE_VERSE_DRAWER_MENU_ITEM_ID,
                    DRAWER_MENU_ITEM_CONTENT,
                    title = title,
                    description = description,
                    iconResourceId = R.drawable.ic_cross_48px) {
                    showRangeSelectDialog()
                }
            )
        )
        binding.layoutNavigationDrawer.drawerRecyclerView.adapter = drawerMenuItemAdapter

        val toggle = ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            binding.fragmentDailyBibleVerse.toolbar,
            R.string.open,
            R.string.close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
    }

    private fun initOnScrollListener() {
        val onScrollListener = object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        coroutineScope.launch {
                            if (binding.fragmentDailyBibleVerse.readBible.isShown) {
                                if (!recyclerView.canScrollVertically(1)) {
                                    binding.fragmentDailyBibleVerse.readBible.hide()
                                    delay(1000L)
                                    binding.fragmentDailyBibleVerse.readBible.show()
                                }
                            } else {
                                delay(1000L)
                                binding.fragmentDailyBibleVerse.readBible.show()
                            }
                        }
                    }
                }
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0)
                    binding.fragmentDailyBibleVerse.readBible.hide()
                else
                    binding.fragmentDailyBibleVerse.readBible.show()
                super.onScrolled(recyclerView, dx, dy)
            }
        }

        binding.fragmentDailyBibleVerse.dailyBibleVerseRecyclerView.clearOnScrollListeners()
        binding.fragmentDailyBibleVerse.dailyBibleVerseRecyclerView.addOnScrollListener(
            onScrollListener
        )
    }

    private fun setDisplayBibleVerses(bibleVerses: List<BibleVerse>) {
        val bibleVerse = bibleVerses[0]
        val bibleVerseInformation = "${viewModel.getBook(bibleVerse.book)} ${bibleVerse.chapter}장 ${bibleVerse.verse}절"
        binding.fragmentDailyBibleVerse.dailyBibleVerseWord.text = bibleVerse.word
        binding.fragmentDailyBibleVerse.dailyBibleVerseInformation.text = bibleVerseInformation

        val dailyBibleVerseAdapter = DailyBibleVerseAdapter()
        binding.fragmentDailyBibleVerse.dailyBibleVerseRecyclerView.adapter =
            dailyBibleVerseAdapter
        binding.fragmentDailyBibleVerse.dailyBibleVerseRecyclerView.scheduleLayoutAnimation()
        dailyBibleVerseAdapter.submitList(bibleVerses)

        /** Context Menu */
        registerForContextMenu(binding.fragmentDailyBibleVerse.dailyBibleVerseRecyclerView)
    }

    private fun setColor() {
        binding.fragmentDailyBibleVerse.readBible.backgroundTintList = ColorStateList.valueOf(
            MainApplication.primaryThemeColor
        )
    }

    private fun showRangeSelectDialog() {
        val titles = arrayOf(
            getString(R.string.select_from_popular_bible_verses_title),
            getString(R.string.select_from_favorites_title),
            getString(R.string.select_at_random_title),
            getString(R.string.select_in_order_title)
        )

        val descriptionArray = arrayOf(
            getString(R.string.select_from_popular_bible_verses_description),
            getString(R.string.select_from_favorites_description),
            getString(R.string.select_at_random_description),
            getString(R.string.select_in_order_description)
        )

        val originalDrawerMenuItem = drawerMenuItemAdapter.getItemById(DAILY_BIBLE_VERSE_DRAWER_MENU_ITEM_ID)
        val originalRange = DailyBibleVerseUtil.getRange(requireContext())
        val originalTitle = originalDrawerMenuItem?.title ?: getString(R.string.select_from_popular_bible_verses_title)
        val originalDescription = originalDrawerMenuItem?.description ?: getString(R.string.select_from_popular_bible_verses_description)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.select_range))
            .setSingleChoiceItems(
                titles, 0
            ) { _, which ->
                /** POPULAR_BIBLE_VERSES = 0
                 * MY_BIBLE_VERSES = 1
                 * RANDOM = 2
                 * IN_ORDER = 3 */
                DailyBibleVerseUtil.setRange(requireContext(), which)
                val item = drawerMenuItemAdapter.getItemById(DAILY_BIBLE_VERSE_DRAWER_MENU_ITEM_ID)
                item?.let {
                    it.title = titles[which]
                    it.description = descriptionArray[which]
                    drawerMenuItemAdapter.update(it)
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialogInterface: DialogInterface, _: Int ->
                DailyBibleVerseUtil.setRange(requireContext(), originalRange)
                val item = drawerMenuItemAdapter.getItemById(DAILY_BIBLE_VERSE_DRAWER_MENU_ITEM_ID)
                item?.let {
                    it.title = originalTitle
                    it.description = originalDescription
                    drawerMenuItemAdapter.update(it)
                }
                dialogInterface.cancel()
            }
            .setPositiveButton(getString(R.string.ok)) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            .show()
    }

    inner class DailyBibleVerseAdapter:
        ListAdapter<BibleVerse, RecyclerView.ViewHolder>(BibleVerseDiffCallback()) {

        inner class ViewHolder constructor(val binding: ViewDataBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(binding: ItemBibleVerseBinding, bibleVerse: BibleVerse) {
                val book = viewModel.getBook(bibleVerse.book)
                val chapter = bibleVerse.chapter
                val verse = bibleVerse.verse
                val word = bibleVerse.word
                val bibleVerseInformation = "$book ${chapter}장 ${verse}절"

                binding.bibleVerseInformation.text = bibleVerseInformation
                binding.bibleVerseWord.text = word

                binding.bibleVerseContainer.setOnLongClickListener {
                    showPopupMenu(it, bibleVerse)
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
            val bibleVerse = getItem(position)
            (holder as ViewHolder).bind(holder.binding as ItemBibleVerseBinding, bibleVerse)
        }

        private fun showPopupMenu(view: View, bibleVerse: BibleVerse) {
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.inflate(R.menu.menu_bible_verse)
            popupMenu.setOnMenuItemClickListener { item ->

                when (item.itemId) {
                    R.id.addToFavorites -> {
                        BibleVerseUtil.addToFavorites(
                            coroutineScope,
                            viewModel.favoriteBibleVerseDao,
                            bibleVerse
                        )
                        true
                    }
                    R.id.copyToClipboard -> {
                        BibleVerseUtil.copyToClipboard(requireActivity(), bibleVerse.toText())
                        true
                    }
                    R.id.share -> {
                        BibleVerseUtil.share(requireActivity(), bibleVerse.toText())
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    fun BibleVerse.toText(): String {
        val bookString = viewModel.getBook(book)
        return "$bookString ${chapter}장 ${verse}절 \n" +
                word
    }

    class BibleVerseDiffCallback: DiffUtil.ItemCallback<BibleVerse>() {
        override fun areItemsTheSame(
            oldItem: BibleVerse,
            newItem: BibleVerse
        ): Boolean {
            return oldItem.book == newItem.book &&
                    oldItem.chapter == newItem.chapter &&
                    oldItem.verse == newItem.verse
        }

        override fun areContentsTheSame(
            oldItem: BibleVerse,
            newItem: BibleVerse
        ): Boolean {
            return oldItem == newItem
        }
    }
}



