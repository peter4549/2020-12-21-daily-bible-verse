package com.duke.elliot.biblereadinghabits.base

import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.duke.elliot.biblereadinghabits.main.MainActivity

open class BaseFragment: Fragment() {

    private var menuRes: Int? = null
    private var onBackPressed: (() -> Unit)? = null
    private var optionsItemIdAndOnSelectedListeners = mutableMapOf<Int, () -> Unit>()

    protected fun setOnBackPressedCallback(onBackPressed: () -> Unit) {
        this.onBackPressed = onBackPressed

        val onBackPressedCallback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    this@BaseFragment.onBackPressed?.invoke()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    @Suppress("SameParameterValue")
    protected fun setDisplayHomeAsUpEnabled(toolbar: Toolbar, displayHomeAsUpEnable: Boolean) {
        (requireActivity() as MainActivity).setSupportActionBar(toolbar)
        (requireActivity() as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(displayHomeAsUpEnable)
        setHasOptionsMenu(true)
    }

    @Suppress("SameParameterValue")
    protected fun setOptionsMenu(menuRes: Int?) {
        this.menuRes = menuRes
        setHasOptionsMenu(true)
    }

    protected fun setOnOptionsItemSelectedListeners(vararg optionsItemIdAndOnSelectedListeners: Pair<Int, () -> Unit>) {
        optionsItemIdAndOnSelectedListeners.forEach {
            if (!this.optionsItemIdAndOnSelectedListeners.keys.contains(it.first))
                this.optionsItemIdAndOnSelectedListeners[it.first] = it.second
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        menuRes?.let {
            inflater.inflate(it, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> onBackPressed?.invoke()
            else -> optionsItemIdAndOnSelectedListeners[item.itemId]?.invoke()
        }

        return super.onOptionsItemSelected(item)
    }

    protected fun showToast(text: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(requireContext(), text, duration).show()
    }
}