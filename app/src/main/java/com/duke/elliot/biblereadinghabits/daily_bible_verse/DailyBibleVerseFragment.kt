package com.duke.elliot.biblereadinghabits.daily_bible_verse

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.base.BaseFragment
import com.duke.elliot.biblereadinghabits.bible_reading.bookmark.BibleBookmarkUtil
import com.duke.elliot.biblereadinghabits.billing.BillingActivity
import com.duke.elliot.biblereadinghabits.daily_bible_verse.DailyBibleVerseUtil.IN_ORDER
import com.duke.elliot.biblereadinghabits.daily_bible_verse.DailyBibleVerseUtil.MY_BIBLE_VERSES
import com.duke.elliot.biblereadinghabits.daily_bible_verse.DailyBibleVerseUtil.POPULAR_BIBLE_VERSES
import com.duke.elliot.biblereadinghabits.daily_bible_verse.DailyBibleVerseUtil.RANDOM
import com.duke.elliot.biblereadinghabits.daily_bible_verse.drawer.*
import com.duke.elliot.biblereadinghabits.database.BibleVerse
import com.duke.elliot.biblereadinghabits.databinding.FragmentDailyBibleVerseDrawerBinding
import com.duke.elliot.biblereadinghabits.databinding.ItemBibleVerseBinding
import com.duke.elliot.biblereadinghabits.main.MainApplication
import com.duke.elliot.biblereadinghabits.splash.SplashActivity
import com.duke.elliot.biblereadinghabits.util.BibleVerseUtil
import com.duke.elliot.biblereadinghabits.util.ThemeUtil.restoreNightMode
import com.duke.elliot.biblereadinghabits.util.getVersionName
import com.duke.elliot.biblereadinghabits.util.goToPlayStore
import com.duke.elliot.biblereadinghabits.util.shareApplication
import com.flyco.dialog.widget.NormalListDialog
import com.mancj.slideup.SlideUp
import com.mancj.slideup.SlideUpBuilder
import kotlinx.android.synthetic.main.fragment_daily_bible_verse.*
import kotlinx.android.synthetic.main.layout_navigation_drawer.view.*
import kotlinx.coroutines.*


const val DAILY_BIBLE_VERSE_DRAWER_MENU_ITEM_ID = 2104L
const val FONT_SIZE_DRAWER_MENU_ITEM_ID = 2105L
const val THEME_COLOR_DRAWER_MENU_ITEM_ID = 2106L
const val THEME_NIGHT_MODE_DRAWER_MENU_ITEM_ID = 2107L

class DailyBibleVerseFragment: BaseFragment() {

    private lateinit var binding: FragmentDailyBibleVerseDrawerBinding
    private lateinit var viewModel: DailyBibleVerseViewModel

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    private lateinit var drawerMenuItemAdapter: DrawerMenuItemAdapter
    private lateinit var slideUp: SlideUp
    private var bookmarksExist = false

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

        val viewModelFactory = DailyBibleVerseViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[DailyBibleVerseViewModel::class.java]

        setColor()

        setToolbarFont(
            binding.fragmentDailyBibleVerse.toolbar,
            R.style.NanumMyeonjoExtraBoldTextAppearance
        )
        initBookmark()
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
            R.id.search to {
                findNavController().navigate(
                    DailyBibleVerseFragmentDirections
                        .actionDailyBibleVerseFragmentToSearchFragment()
                )
            }
        )

        setDisplayBibleVerses(viewModel.displayDailyBibleVerses)

        binding.fragmentDailyBibleVerse.readBible.setOnClickListener {
            findNavController().navigate(
                DailyBibleVerseFragmentDirections
                    .actionDailyBibleVerseFragmentToBibleReadingFragment(null)
            )
        }

        return binding.root
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

        val iconColor = ContextCompat.getColor(requireContext(), R.color.color_icon)

        /** Version */
        val currentVersion = getVersionName(requireContext())
        val versionText = if (currentVersion == SplashActivity.latestVersionName) {
            currentVersion + " ${getString(R.string.using_the_latest_version)}"
        } else
            currentVersion + " ${getString(R.string.latest_version)}: ${SplashActivity.latestVersionName}"

        drawerMenuItemAdapter = DrawerMenuItemAdapter(
            arrayListOf(
                DrawerMenuItem(
                    0L,
                    DRAWER_MENU_ITEM_SUBTITLE,
                    title = getString(R.string.favorite)
                ),
                DrawerMenuItem(
                    0L,
                    DRAWER_MENU_ITEM_CONTENT,
                    title = getString(R.string.favorite),
                    iconResourceId = R.drawable.ic_round_star_24,
                    iconColor = iconColor
                ) {
                    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
                        binding.drawerLayout.closeDrawer(GravityCompat.START)

                    findNavController().navigate(
                        DailyBibleVerseFragmentDirections
                            .actionDailyBibleVerseFragmentToFavoriteBibleVersesFragment()
                    )
                },
                DrawerMenuItem(0L, DRAWER_MENU_ITEM_DIVIDER),
                DrawerMenuItem(
                    0L,
                    DRAWER_MENU_ITEM_SUBTITLE,
                    title = getString(R.string.daily_bible_verse)
                ),
                DrawerMenuItem(
                    DAILY_BIBLE_VERSE_DRAWER_MENU_ITEM_ID,
                    DRAWER_MENU_ITEM_CONTENT,
                    title = title,
                    description = description,
                    iconResourceId = R.drawable.ic_holy_bible_50px,
                    iconColor = iconColor
                ) {
                    showRangeSelectDialog()
                },
                DrawerMenuItem(0L, DRAWER_MENU_ITEM_DIVIDER),
                DrawerMenuItem(
                    0L,
                    DRAWER_MENU_ITEM_SUBTITLE,
                    title = getString(R.string.font_size_and_theme)
                ),
                /** Font Size and Theme Color */
                DrawerMenuItem(
                    FONT_SIZE_DRAWER_MENU_ITEM_ID,
                    DRAWER_MENU_ITEM_CONTENT,
                    title = getString(R.string.font_size),
                    description = MainApplication.fontSize.toInt().toString(),
                    iconResourceId = R.drawable.ic_baseline_text_fields_24,
                    iconColor = iconColor
                ) {
                    DrawerMenuUtil.showFontSizePicker(requireContext()) { fontSize ->
                        val item = drawerMenuItemAdapter.getItemById(FONT_SIZE_DRAWER_MENU_ITEM_ID)
                        item?.let {
                            it.description = fontSize.toInt().toString()
                            drawerMenuItemAdapter.update(it)
                        }
                        dailyBibleVerseRecyclerView.adapter?.notifyDataSetChanged()
                        binding.fragmentDailyBibleVerse.dailyBibleVerseWord.setTextSize(
                            TypedValue.COMPLEX_UNIT_SP,
                            MainApplication.fontSize + 2F
                        )
                        binding.fragmentDailyBibleVerse.dailyBibleVerseInformation.setTextSize(
                            TypedValue.COMPLEX_UNIT_SP,
                            MainApplication.fontSize
                        )
                    }
                },
                DrawerMenuItem(
                    THEME_COLOR_DRAWER_MENU_ITEM_ID,
                    DRAWER_MENU_ITEM_CONTENT,
                    title = getString(R.string.theme_color),
                    iconResourceId = R.drawable.ic_rounded_square_48px,
                    iconColor = MainApplication.primaryThemeColor
                ) {
                    DrawerMenuUtil.showColorPicker(requireActivity()) { color ->
                        val item =
                            drawerMenuItemAdapter.getItemById(THEME_COLOR_DRAWER_MENU_ITEM_ID)
                        item?.let {
                            it.iconColor = color
                            drawerMenuItemAdapter.update(it)
                            setColor()
                        }
                    }
                },
                DrawerMenuItem(
                    THEME_NIGHT_MODE_DRAWER_MENU_ITEM_ID,
                    DRAWER_MENU_ITEM_CONTENT,
                    title = getString(R.string.night_mode),
                    description = when (restoreNightMode(requireContext())) {
                        MODE_NIGHT_YES -> getString(R.string.dark_theme)
                        MODE_NIGHT_NO -> getString(R.string.light_theme)
                        MODE_NIGHT_FOLLOW_SYSTEM -> getString(R.string.system_default_theme)
                        else -> throw IllegalArgumentException("Invalid night mode.")
                    },
                    iconResourceId = R.drawable.ic_day_and_night_60px,
                    iconColor = iconColor
                ) {
                    DrawerMenuUtil.showNightModePicker(requireContext()) { nightMode, nightModeString ->
                        val item = drawerMenuItemAdapter.getItemById(
                            THEME_NIGHT_MODE_DRAWER_MENU_ITEM_ID
                        )
                        item?.let {
                            setDefaultNightMode(nightMode)
                            it.description = nightModeString
                            drawerMenuItemAdapter.update(it)
                        }
                    }
                },
                DrawerMenuItem(0, DRAWER_MENU_ITEM_DIVIDER),
                DrawerMenuItem(
                    0L,
                    DRAWER_MENU_ITEM_SUBTITLE,
                    title = getString(R.string.etc)
                ),
                DrawerMenuItem(
                    0L,
                    DRAWER_MENU_ITEM_CONTENT,
                    title = getString(R.string.share_the_app),
                    iconResourceId = R.drawable.ic_baseline_share_24,
                    iconColor = iconColor
                ) {
                    shareApplication(requireContext())
                },
                DrawerMenuItem(
                    0L,
                    DRAWER_MENU_ITEM_CONTENT,
                    title = getString(R.string.donation),
                    iconResourceId = R.drawable.ic_donation_50px,
                    iconColor = iconColor
                ) {
                    startActivity(Intent(requireActivity(), BillingActivity::class.java))
                },
                DrawerMenuItem(
                    0L,
                    DRAWER_MENU_ITEM_CONTENT,
                    title = getString(R.string.version),
                    description = versionText,
                    iconResourceId = R.drawable.ic_versions_48px,
                    iconColor = iconColor
                ) {
                    if (currentVersion != SplashActivity.latestVersionName)
                        showUpdateRequestDialog()
                }
                /** Future Work
                DrawerMenuItem(
                0L,
                DRAWER_MENU_ITEM_CONTENT,
                title = getString(R.string.open_source_licenses),
                iconResourceId = R.drawable.ic_open_source_50px,
                iconColor = iconColor
                ) {

                }
                 */
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
                                    if (bookmarksExist)
                                        slideUp.hide()
                                    delay(1000L)
                                    binding.fragmentDailyBibleVerse.readBible.show()
                                    if (bookmarksExist)
                                        slideUp.show()
                                }
                            } else {
                                delay(1000L)
                                binding.fragmentDailyBibleVerse.readBible.show()
                                if (bookmarksExist)
                                    slideUp.show()
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

    private fun initBookmark() {
        val bookmarks = BibleBookmarkUtil.getBookmarks(requireContext())
        slideUp = SlideUpBuilder(binding.fragmentDailyBibleVerse.bookmarkContainer)
            .withListeners(object : SlideUp.Listener.Events {
                override fun onSlide(percent: Float) {}

                override fun onVisibilityChanged(visibility: Int) {}
            })
            .withStartGravity(Gravity.BOTTOM)
            .withLoggingEnabled(true)
            .withStartState(SlideUp.State.HIDDEN)
            // .withSlideFromOtherView(binding.root) This prevents the drawer from closing.
            .build()

        bookmarksExist = if (bookmarks.isNotEmpty()) {
            binding.fragmentDailyBibleVerse.bookmarkContainer.visibility = View.VISIBLE
            binding.fragmentDailyBibleVerse.bookmark.visibility = View.VISIBLE
            slideUp.show()
            true
        } else {
            binding.fragmentDailyBibleVerse.bookmarkContainer.visibility = View.GONE
            binding.fragmentDailyBibleVerse.bookmark.visibility = View.GONE
            slideUp.hide()
            false
        }

        val bookmarkTexts = bookmarks.map { it.second }

        if (bookmarksExist) {
            binding.fragmentDailyBibleVerse.bookmark.setOnClickListener {
                val normalListDialog =
                    NormalListDialog(requireContext(), bookmarkTexts.toTypedArray())
                normalListDialog.title(getString(R.string.bookmark))
                normalListDialog.titleBgColor(MainApplication.primaryThemeColor)
                normalListDialog.setOnOperItemClickL { _, _, position, _ ->
                    findNavController().navigate(
                        DailyBibleVerseFragmentDirections
                            .actionDailyBibleVerseFragmentToBibleReadingFragment(bookmarks[position].first)
                    )

                    normalListDialog.dismiss()
                }
                normalListDialog.show()
            }
        }
    }

    private fun setDisplayBibleVerses(bibleVerses: List<BibleVerse>) {
        val bibleVerse = bibleVerses[0]
        val bibleVerseInformation = "${viewModel.getBook(bibleVerse.book)} ${bibleVerse.chapter}장 ${bibleVerse.verse}절"
        binding.fragmentDailyBibleVerse.dailyBibleVerseWord.text = bibleVerse.word
        binding.fragmentDailyBibleVerse.dailyBibleVerseInformation.text = bibleVerseInformation

        binding.fragmentDailyBibleVerse.dailyBibleVerseWord.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            MainApplication.fontSize + 2F
        )
        binding.fragmentDailyBibleVerse.dailyBibleVerseInformation.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            MainApplication.fontSize
        )

        val dailyBibleVerseAdapter = DailyBibleVerseAdapter()
        binding.fragmentDailyBibleVerse.dailyBibleVerseRecyclerView.adapter =
            dailyBibleVerseAdapter
        binding.fragmentDailyBibleVerse.dailyBibleVerseRecyclerView.scheduleLayoutAnimation()
        dailyBibleVerseAdapter.submitList(bibleVerses)

        /** Context Menu */
        registerForContextMenu(binding.fragmentDailyBibleVerse.dailyBibleVerseRecyclerView)

        /** More */
        binding.fragmentDailyBibleVerse.more.setOnClickListener {
            showPopupMenu(it, bibleVerse)
        }
    }

    private fun setColor() {
        setBackgroundColor(binding.fragmentDailyBibleVerse.toolbar)
        binding.fragmentDailyBibleVerse.readBible.backgroundTintList = ColorStateList.valueOf(
            MainApplication.primaryThemeColor
        )
        binding.fragmentDailyBibleVerse.bookmark.setColorFilter(MainApplication.primaryThemeColor)

        binding.fragmentDailyBibleVerse.readBible.invalidate()
        binding.fragmentDailyBibleVerse.bookmark.invalidate()

        requireActivity().window.statusBarColor = MainApplication.primaryThemeColor
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

        val normalListDialog =
            NormalListDialog(requireContext(), titles)
        normalListDialog.title(getString(R.string.select_range))
        normalListDialog.titleBgColor(MainApplication.primaryThemeColor)
        normalListDialog.setOnOperItemClickL { _, _, position, _ ->
            /** POPULAR_BIBLE_VERSES = 0
             * MY_BIBLE_VERSES = 1
             * RANDOM = 2
             * IN_ORDER = 3
             */
            DailyBibleVerseUtil.setRange(requireContext(), position)
            val item = drawerMenuItemAdapter.getItemById(DAILY_BIBLE_VERSE_DRAWER_MENU_ITEM_ID)
            item?.let {
                it.title = titles[position]
                it.description = descriptionArray[position]
                drawerMenuItemAdapter.update(it)
            }
            normalListDialog.dismiss()
        }
        normalListDialog.show()
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
                binding.bibleVerseInformation.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    MainApplication.fontSize + 2F
                )
                binding.bibleVerseWord.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    MainApplication.fontSize
                )

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
                    showToast(getString(R.string.add_to_favorites_success))
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

    private fun BibleVerse.toText(): String {
        val bookString = viewModel.getBook(book)
        return "$bookString ${chapter}장 ${verse}절 \n" +
                word
    }

    private fun showUpdateRequestDialog() {
        SweetAlertDialog(requireContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
            .setTitleText(getString(R.string.update_request_title))
            .setContentText(getString(R.string.update_request_message))
            .setConfirmText(getString(R.string.update))
            .setCustomImage(R.drawable.ic_holy_bible_100px)
            .setConfirmClickListener { sDialog ->
                goToPlayStore(requireContext())
                sDialog.dismissWithAnimation()
            }
            .show()
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