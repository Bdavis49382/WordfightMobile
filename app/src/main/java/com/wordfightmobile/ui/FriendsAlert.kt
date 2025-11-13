package com.wordfightmobile.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.wordfightmobile.R
import com.wordfightmobile.viewModels.AuthViewModel
import com.wordfightmobile.viewModels.FriendsViewModel
import com.wordfightmobile.viewModels.OpenAlert

@Composable
fun FriendsAlert(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()
    val friendsViewModel: FriendsViewModel = viewModel()
    LaunchedEffect(Unit) {
        friendsViewModel.getInviteCode(authViewModel.uid.toString())
        friendsViewModel.getFriends(authViewModel.uid.toString())
    }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .padding(0.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.fillMaxSize(.95f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Friends", fontSize = 25.sp, modifier = Modifier.padding(20.dp))
                LazyColumn(modifier = Modifier.fillMaxHeight(.7f), verticalArrangement = Arrangement.spacedBy(15.dp)) {
                    items(friendsViewModel.friends, key = { it.id}) { friend ->
                        Card(modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.onBackground)
                            ) {
                            Row(horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth(.9f)
                                    .background(MaterialTheme.colorScheme.onBackground)
                                    .clip(RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                                    .clickable {
                                        friendsViewModel.friendName.value = friend.name
                                        friendsViewModel.friendId.value = friend.id
                                        friendsViewModel.openAlert.value = OpenAlert.CreateGame
                                    }
                            ) {
                                if (friend.img != null) {
                                    AsyncImage(model = friend.img,
                                        contentDescription=friend.name,
                                        modifier = Modifier.size(50.dp).clip(CircleShape))
                                } else {
                                    Icon(painter = painterResource(R.drawable.baseline_person_24), tint = MaterialTheme.colorScheme.background, contentDescription = null, modifier = Modifier.size(50.dp))
                                }
                                Text(friend.name, fontSize = 20.sp, modifier = Modifier.padding(horizontal=20.dp),
                                    color = MaterialTheme.colorScheme.background)
                            }

                        }
                    }
                }
                Button({
                    val message = "Hey I want to be friends with you on Wordfight! This way we can easily start games together.\n If you don't have the app find it here: ${context.getString(R.string.app_link)} \nOnce it's downloaded, friend me here: https://wordfight-64703.web.app/?code=${friendsViewModel.inviteCode.value}"
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, message)
                    }
                    val chooser = Intent.createChooser(intent, "Send Invite")
                    context.startActivity(chooser)
                }, modifier = Modifier.padding(top = 20.dp)) {
                    Text("Invite A Friend")
                }
            }
        }
    }
}