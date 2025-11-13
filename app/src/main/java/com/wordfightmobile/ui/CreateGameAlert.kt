package com.wordfightmobile.ui

import android.widget.Toast
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wordfightmobile.viewModels.FriendsViewModel
import com.wordfightmobile.viewModels.GamesViewModel
import kotlinx.coroutines.launch

@Composable
fun CreateGameAlert() {
    val friendsViewModel: FriendsViewModel = viewModel()
    val gamesViewModel: GamesViewModel = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = {friendsViewModel.closeAlerts()}) {
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
                        text = "Something went wrong. Try again or select a different friend.")
                    Button({ friendsViewModel.closeAlerts() }, modifier = Modifier.padding(top = 10.dp)) {
                        Text("Dismiss")
                    }
                } else {
                    Text(textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        text = "Start game vs ${friendsViewModel.friendName.value}?")
                    Row(horizontalArrangement = Arrangement.Center) {
                        Button({
                            gamesViewModel.createGame(opponent = friendsViewModel.friendId.value.toString(),
                                opponentName = friendsViewModel.friendName.value.toString())
                            friendsViewModel.closeAlerts()
                            scope.launch {
                                Toast.makeText(context, "Creating game...", Toast.LENGTH_LONG).show()
                            }
                        }, modifier = Modifier.padding(top = 10.dp, end = 10.dp)) {
                            Text("Start Game")
                        }
                        Button({ friendsViewModel.closeAlerts() }, modifier = Modifier.padding(top = 10.dp)) {
                            Text("Dismiss")
                        }
                    }
                }

            }
        }
    }

}