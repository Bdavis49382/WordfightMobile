package com.wordfightmobile.data

data class Block(
    val letter: String? = null,
    val index: Int? = null,
    var clicked: Boolean = false,
    val allegiance: String = "none",
    val surrounded: Boolean = false
)