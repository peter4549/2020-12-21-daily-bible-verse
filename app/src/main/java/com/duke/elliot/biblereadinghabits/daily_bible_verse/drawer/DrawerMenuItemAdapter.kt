package com.duke.elliot.biblereadinghabits.daily_bible_verse.drawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.duke.elliot.biblereadinghabits.databinding.ItemMenuContentBinding
import com.duke.elliot.biblereadinghabits.databinding.ItemMenuSubtitleBinding
import java.lang.IllegalArgumentException

const val DRAWER_MENU_ITEM_CONTENT = 1605
const val DRAWER_MENU_ITEM_SUBTITLE = 1606

class DrawerMenuItemAdapter(private val drawerMenuItems: ArrayList<DrawerMenuItem>):
    RecyclerView.Adapter<DrawerMenuItemAdapter.ViewHolder>() {

    inner class ViewHolder constructor(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(drawerMenuItem: DrawerMenuItem) {
            when(binding) {
                is ItemMenuContentBinding -> {
                    binding.title.text = drawerMenuItem.title
                    drawerMenuItem.description?.let { description ->
                        binding.description.text = description
                    } ?: run {
                        (binding as ItemMenuContentBinding).description.visibility = View.GONE
                    }
                    drawerMenuItem.iconResourceId?.let { iconResourceId ->
                        binding.icon.setImageResource(iconResourceId)
                    } ?: run {
                        (binding as ItemMenuContentBinding).icon.visibility = View.INVISIBLE
                    }
                    binding.root.setOnClickListener {
                        drawerMenuItem.onClickListener?.invoke()
                    }
                }
                is ItemMenuSubtitleBinding -> {
                    binding.subtitle.text = drawerMenuItem.title
                }
            }
        }
    }

    private fun from(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = when(viewType) {
            DRAWER_MENU_ITEM_CONTENT -> ItemMenuContentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            DRAWER_MENU_ITEM_SUBTITLE -> ItemMenuSubtitleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            else -> throw IllegalArgumentException("Invalid viewType.")
        }

        return ViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int = drawerMenuItems[position].viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return from(parent, viewType)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(drawerMenuItems[position])
    }

    override fun getItemCount(): Int = drawerMenuItems.count()

    fun getItemById(id: Long) = drawerMenuItems.find { it.id == id }

    fun update(drawerMenuItem: DrawerMenuItem) {
        val position = drawerMenuItems.indexOf(
            drawerMenuItems.find { it.id == drawerMenuItem.id }
        )
        drawerMenuItems[position] = drawerMenuItem
        notifyItemChanged(position)
    }
}