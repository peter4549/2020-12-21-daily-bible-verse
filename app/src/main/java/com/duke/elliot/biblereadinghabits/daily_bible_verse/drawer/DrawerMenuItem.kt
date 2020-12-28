package com.duke.elliot.biblereadinghabits.daily_bible_verse.drawer

class DrawerMenuItem (
    val id: Long,
    val viewType: Int,
    var title: String? = null,
    var description: String? = null,
    var iconResourceId: Int? = null,
    var iconColor: Int? = null,
    var onClickListener: (() -> Unit)? = null
)