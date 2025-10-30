package com.wordfightmobile.ui

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

    Crossfade(gamesViewModel.currentGame.value?.turn) { turn ->
        Column(
            horizontalAlignment =  Alignment.CenterHorizontally
        ) {
            val isTurn by remember { derivedStateOf {
                turn == authViewModel.uid
            } }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(.9f).height(30.dp)) {
                Text(game.scores[0].toString())
                Text(game.scores[1].toString())
            }
            if (game.playerNames.size >= 2)  {
                Text("${game.playerNames[0]} vs ${game.playerNames[1]}",
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                )
            }
            GameGrid(game.blocks, GridSize.Large, clickEnabled = isTurn && !game.finished)
            Text(gamesViewModel.currentWord.joinToString(separator = "", transform = {it.letter.toString()}),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                fontWeight = FontWeight.Bold)
            if (isTurn && !game.finished) {
                Button({
                    gamesViewModel.submitWord {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    }
                    Toast.makeText(context, "Submitting...", Toast.LENGTH_LONG).show()
                }) {
                    Text("Submit")
                }
            } else {
                if (game.finished) {
                    Text("Game Over", fontSize = 20.sp)
                } else {
                    Text("Waiting", fontSize = 20.sp)
                }
            }

        }

    }
}