package com.wordfightmobile.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
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
import kotlin.time.toKotlinDuration

fun formatDate(lastMove: Instant): String {
    val timeSince = Duration.between(lastMove, Instant.now()).toKotlinDuration()
    return if (timeSince.inWholeMinutes < 2) {
       "Now"
    } else if (timeSince.inWholeHours < 1) {
        "${timeSince.inWholeMinutes} minutes ago"
    } else if (timeSince.inWholeDays < 1) {
       "${timeSince.inWholeHours} hours and ${timeSince.inWholeMinutes % 60} minutes ago"
    } else if (timeSince.inWholeDays < 7) {
       "${timeSince.inWholeDays} days and ${timeSince.inWholeHours % 24} hours ago"
    } else if (timeSince.inWholeDays < 365) {
       "${timeSince.inWholeDays/7} weeks and ${timeSince.inWholeDays%7} days ago"
    } else {
       "Over a year ago"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun GameSummary(game: Game,
                scaffoldState: BottomSheetScaffoldState,
                lazyListState: LazyListState,
                modifier: Modifier = Modifier) {
    val authViewModel : AuthViewModel = viewModel()
    val gamesViewModel : GamesViewModel = viewModel()
    val scope = rememberCoroutineScope()
    fun onClick() {
        gamesViewModel.gameId.value = game.id
        gamesViewModel.currentWord.clear()
        scope.launch {
            scaffoldState.bottomSheetState.partialExpand()
            lazyListState.animateScrollToItem(0)
        }
    }
    Row(modifier = modifier.fillMaxWidth(.95f)
        .clickable {
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