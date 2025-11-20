package com.wordfightmobile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WordsRow(usedWords: List<String>) {
    if (usedWords.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Words", fontSize = 25.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(15.dp), modifier = Modifier.fillMaxWidth().padding(top = 5.dp) ) {
                    items(items=usedWords.reversed(), key = {it}) { word ->
                        Text(word.lowercase().capitalize(Locale.current))
                    }
                }
            }
        }
    }

}