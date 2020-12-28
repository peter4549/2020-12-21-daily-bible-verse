package com.duke.elliot.biblereadinghabits.daily_bible_verse.drawer

import android.app.Activity
import android.content.Context
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.main.MainApplication
import com.flyco.dialog.widget.NormalListDialog
import kotlinx.android.synthetic.main.item_menu_content.*
import petrov.kristiyan.colorpicker.ColorPicker


const val PREFERENCES_THEME = "com.duke.elliot.biblereadinghabits.daily_bible_verse.drawer" +
        ".drawer_menu_util.preferences_theme"
const val KEY_PRIMARY_THEME_COLOR = "com.duke.elliot.biblereadinghabits.daily_bible_verse.drawer" +
        ".drawer_menu_util.key_primary_theme_color"
const val KEY_FONT_SIZE = "com.duke.elliot.biblereadinghabits.daily_bible_verse.drawer" +
        ".drawer_menu_util.key_font_size"

const val DEFAULT_FONT_SIZE = 14F

object DrawerMenuUtil {

    /** Theme Color */
    fun showColorPicker(activity: Activity, onChooseCallback: (color: Int) -> Unit) {
        val themeColors = activity.resources.getIntArray(R.array.theme_colors).toList()
        val hexColors = themeColors.map { it.toHexColor() } as ArrayList

        val colorPicker = ColorPicker(activity)
        colorPicker.setOnChooseColorListener(object : ColorPicker.OnChooseColorListener {
            override fun onChooseColor(position: Int, color: Int) {
                if (color != 0) {
                    MainApplication.primaryThemeColor = color
                    storeThemeColor(activity, MainApplication.primaryThemeColor)
                    onChooseCallback.invoke(MainApplication.primaryThemeColor)
                }
            }

            override fun onCancel() {}
        })
            .setTitle(activity.getString(R.string.theme_color))
            .setColumns(6)
            .setColorButtonMargin(2, 2, 2, 2)
            .setColorButtonDrawable(R.drawable.background_white_rounded_corners)
            .setColors(hexColors)
            .setDefaultColorButton(MainApplication.primaryThemeColor)
            .show()

        colorPicker.positiveButton.text = activity.getString(R.string.ok)
        colorPicker.negativeButton.text = activity.getString(R.string.cancel)
    }

    private fun storeThemeColor(context: Context, @ColorInt color: Int) {
        val preferences = context.getSharedPreferences(PREFERENCES_THEME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(KEY_PRIMARY_THEME_COLOR, color)
        editor.apply()
    }

    @ColorInt
    fun restoreThemeColor(context: Context): Int {
        val preferences = context.getSharedPreferences(PREFERENCES_THEME, Context.MODE_PRIVATE)
        return preferences.getInt(
            KEY_PRIMARY_THEME_COLOR, ContextCompat.getColor(
                context,
                R.color.default_primary_theme
            )
        )
    }

    /** Font Size */
    fun showFontSizePicker(context: Context, selectFontSizeCallback: (fontSize: Float) -> Unit) {
        val fontSizes = arrayOf("12", "14", "16", "18", "20")

        val normalListDialog = NormalListDialog(context, fontSizes)
        normalListDialog.title(context.getString(R.string.font_size))
        normalListDialog.titleBgColor(MainApplication.primaryThemeColor)
        normalListDialog.setOnOperItemClickL { _, _, position, _ ->
            MainApplication.fontSize = fontSizes[position].toFloat()
            storeFontSize(context, MainApplication.fontSize)
            selectFontSizeCallback.invoke(MainApplication.fontSize)
            normalListDialog.dismiss()
        }
        normalListDialog.show()
    }

    private fun storeFontSize(context: Context, fontSize: Float) {
        val preferences = context.getSharedPreferences(PREFERENCES_THEME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putFloat(KEY_FONT_SIZE, fontSize)
        editor.apply()
    }

    fun restoreFontSize(context: Context): Float {
        val preferences = context.getSharedPreferences(PREFERENCES_THEME, Context.MODE_PRIVATE)
        return preferences.getFloat(KEY_FONT_SIZE, DEFAULT_FONT_SIZE)
    }
}

fun Int.toHexColor() = String.format("#%06X", 0xFFFFFF and this)