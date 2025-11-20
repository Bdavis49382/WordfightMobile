package com.wordfightmobile.ui

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.wordfightmobile.viewModels.FriendsViewModel
import com.wordfightmobile.viewModels.GamesViewModel
import com.wordfightmobile.viewModels.OpenAlert
import com.wordfightmobile.viewModels.TutorialViewModel

@Composable
fun GameScreen(game: Game) {
    val gamesViewModel: GamesViewModel = viewModel()
    val friendsViewModel: FriendsViewModel = viewModel()
    val authViewModel : AuthViewModel = viewModel()
    val tutorialViewModel: TutorialViewModel = viewModel()
    val context = LocalContext.current

    LaunchedEffect(gamesViewModel.currentGame.value?.id) {
        if (gamesViewModel.games.size == 1 && game.usedWords.isEmpty()) {
            tutorialViewModel.getSlides()
            tutorialViewModel.open.value = true
        }
    }

    Crossfade(gamesViewModel.currentGame.value?.turn) { turn ->
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
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
            if (isTurn && !game.finished) {
                Text(gamesViewModel.currentWord.joinToString(separator = "", transform = {it.letter.toString()}),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    fontWeight = FontWeight.Bold)
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
                    Text("Game Over", fontSize = 20.sp, modifier = Modifier.padding(top = 5.dp))
                    Button({
                        val index = game.players.indexOf(authViewModel.uid.toString())
                        if (index != -1) {
                            // load the information for the other player
                            friendsViewModel.setFriend(game.players[(index + 1) % 2],game.playerNames[(index + 1) % 2])
                            friendsViewModel.openAlert.value = OpenAlert.CreateGame
                        }
                    }) {
                        Text("Rematch")
                    }
                } else {
                    Text("Waiting", fontSize = 20.sp, modifier = Modifier.padding(top = 5.dp))
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }

    }
}