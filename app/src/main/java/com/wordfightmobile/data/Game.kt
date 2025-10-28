package com.wordfightmobile.data

import com.google.firebase.Timestamp

data class Game(
    val blocks: List<Block> = listOf<Block>(),
    val finished: Boolean = false,
    val players: List<String> = listOf<String>(),
    val playerNames: List<String> = listOf<String>(),
    val scores: List<Int> = listOf<Int>(),
    val turn: String = "",
    val id: String? = null,
    val usedWords: List<String> = listOf<String>(),
    val lastMove: Timestamp = Timestamp.now()
)