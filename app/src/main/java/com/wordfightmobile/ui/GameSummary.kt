package com.wordfightmobile.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wordfightmobile.data.Game
import com.wordfightmobile.viewModels.AuthViewModel
import com.wordfightmobile.viewModels.GamesViewModel
import java.time.Duration
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.S)
fun formatDate(lastMove: Instant): String {
    val timeSince = Duration.between(lastMove, Instant.now())
    return if (timeSince.toMinutes() < 2) {
       "Now"
    } else if (timeSince.toHours() < 1) {
        "${timeSince.toMinutesPart()} minutes ago"
    } else if (timeSince.toDays() < 1) {
       "${timeSince.toHoursPart()} hours and ${timeSince.toMinutesPart()} minutes ago"
    } else if (timeSince.toDays() < 7) {
       "${timeSince.toDaysPart()} days and ${timeSince.toHoursPart()} hours ago"
    } else if (timeSince.toDays() < 365) {
       "${timeSince.toDays()/7} weeks and ${timeSince.toDays()%7} days ago"
    } else {
       "Over a year ago"
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun GameSummary(game: Game) {
    val authViewModel : AuthViewModel = viewModel()
    val gamesViewModel : GamesViewModel = viewModel()
    Row(modifier = Modifier.clickable {
        gamesViewModel.gameId.value = game.id
        gamesViewModel.currentWord.clear()
    }) {
        Box(modifier = Modifier.fillMaxWidth(.4f)) {
            GameGrid(game.blocks, GridSize.Small)
        }
        Column {
            Text(if (game.players[0] == authViewModel.uid) game.players[1] else game.players[0])
            Text(if (game.turn == authViewModel.uid) "Your Turn" else "Their Turn")
            Text("Last Move: " + formatDate(game.lastMove.toInstant()))

        }
    }
}