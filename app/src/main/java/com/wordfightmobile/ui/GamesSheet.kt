package com.wordfightmobile.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wordfightmobile.viewModels.GamesViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesSheet(scaffoldState: BottomSheetScaffoldState) {
    val viewModel : GamesViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val hasGames by remember {
        derivedStateOf {
            viewModel.games.isNotEmpty()
        }
    }
    Crossfade(hasGames, animationSpec = tween(500)) {
        if (it) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(0.dp)) {
                item {
                    Text("Games", fontSize = 25.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth())
                }
                items(viewModel.games, key = {it.id?:it.lastMove.toString()}) {
                    GameSummary(it, scaffoldState)
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button({
                            scope.launch {
                                viewModel.createGame()
                            }
                        }) {
                            Text(
                                text = "Play Against Random Opponent", modifier = Modifier.width(120.dp))
                        }
                        VerticalDivider(color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.height(60.dp).padding(horizontal = 10.dp))
                        Button({
                            scope.launch {
                                viewModel.createGame()
                            }
                        }) {
                            Text(
                                text = "Play Against A Friend")
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }

        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Games", fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())
            }

        }
    }
}