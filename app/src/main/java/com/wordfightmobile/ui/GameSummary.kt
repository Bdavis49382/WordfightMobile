package com.wordfightmobile.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wordfightmobile.data.Game
import com.wordfightmobile.viewModels.AuthViewModel
import com.wordfightmobile.viewModels.GamesViewModel
import kotlinx.coroutines.launch
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

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun GameSummary(game: Game, scaffoldState: BottomSheetScaffoldState) {
    val authViewModel : AuthViewModel = viewModel()
    val gamesViewModel : GamesViewModel = viewModel()
    val scope = rememberCoroutineScope()
    fun onClick() {
        gamesViewModel.gameId.value = game.id
        gamesViewModel.currentWord.clear()
        scope.launch {
            scaffoldState.bottomSheetState.partialExpand()
        }
    }
    Row(modifier = Modifier.fillMaxWidth(.95f).clickable {
        onClick()
    }) {
        Box(modifier = Modifier.fillMaxWidth(.4f)) {
            GameGrid(game.blocks, GridSize.Small, clickEnabled = false, onClick = { onClick()})
        }
        Column {
            Text(if (game.players[0] == authViewModel.uid) game.playerNames[1] else game.playerNames[0])
            if (game.finished) {
                Text("Game finished " + formatDate(game.lastMove.toInstant()))
            } else {
                Text(if (game.turn == authViewModel.uid) "Your Turn" else "Their Turn")
                Text("Last Move: " + formatDate(game.lastMove.toInstant()))
            }
        }
    }
}