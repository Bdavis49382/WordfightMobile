package com.wordfightmobile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wordfightmobile.viewModels.FriendsViewModel
import com.wordfightmobile.viewModels.GamesViewModel

@Composable
fun AcceptFriendAlert(onDismiss: () -> Unit) {
    val friendsViewModel: FriendsViewModel = viewModel()
    val gamesViewModel: GamesViewModel = viewModel()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(0.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                if (friendsViewModel.friendName.value == null) {
                    Text(textAlign = TextAlign.Center,
                        text = "Friend Connection Attempt Failed")
                    Button({ onDismiss() }, modifier = Modifier.padding(top = 20.dp)) {
                        Text("Dismiss")
                    }
                } else {
                    Text(textAlign = TextAlign.Center,
                        text = "You and ${friendsViewModel.friendName.value} are now friends. Would you like to start a game with them?")
                    Row(horizontalArrangement = Arrangement.Center) {
                        Button({
                            gamesViewModel.createGame(opponent = friendsViewModel.friendId.value.toString(),
                                opponentName = friendsViewModel.friendName.value.toString())
                            onDismiss()
                        }, modifier = Modifier.padding(top = 20.dp, end = 10.dp)) {
                            Text("Start Game")
                        }
                        Button({ onDismiss() }, modifier = Modifier.padding(top = 20.dp)) {
                            Text("Dismiss")
                        }
                    }

                }

            }
        }
    }
}