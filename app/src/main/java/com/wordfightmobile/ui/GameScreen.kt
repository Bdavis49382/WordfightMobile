package com.wordfightmobile.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wordfightmobile.data.Game
import com.wordfightmobile.viewModels.AuthViewModel
import com.wordfightmobile.viewModels.GamesViewModel

@Composable
fun GameScreen(game: Game) {
    val gamesViewModel: GamesViewModel = viewModel()
    val authViewModel : AuthViewModel = viewModel()

    if (game.playerNames.size >= 2)  {
        Text("${game.playerNames[0]} vs ${game.playerNames[1]}",
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
    GameGrid(game.blocks, GridSize.Large, clickEnabled = true)
    Text(gamesViewModel.currentWord.joinToString(separator = "", transform = {it.letter.toString()}),
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        fontWeight = FontWeight.Bold)
    if (game.turn == authViewModel.uid) {
        Button({
            gamesViewModel.submitWord()
        }) {
            Text("Submit")
        }
    } else {
        Text("Waiting", fontSize = 20.sp)
    }
}