package com.wordfightmobile.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wordfightmobile.R
import com.wordfightmobile.viewModels.GamesViewModel

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesSheet(scaffoldState: BottomSheetScaffoldState) {
    val viewModel : GamesViewModel = viewModel()
    var showFinished by remember { mutableStateOf(false) }
    val hasGames by remember {
        derivedStateOf {
            viewModel.games.isNotEmpty()
        }
    }
    val finishedGames by remember {
        derivedStateOf {
            viewModel.games.partition { it.finished }
        }
    }
    Box(modifier = Modifier.fillMaxHeight()) {
        Crossfade(hasGames, animationSpec = tween(700)) {
            if (it) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(0.dp)) {
                    item {
                        Text("Games", fontSize = 25.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth())
                    }
                    items(finishedGames.second, key = {it.id?:it.lastMove.toString()}) {
                        GameSummary(it, scaffoldState)
                    }
                    item {
                        CreateGameBar(scaffoldState)
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()) {
                            Text("Finished Games")
                            Button({
                                showFinished = !showFinished
                            }, modifier = Modifier
                                .padding(start = 10.dp)
                            ) {
                                if (showFinished) {
                                    Icon(painter = painterResource(R.drawable.baseline_arrow_drop_up_24),contentDescription = "Dropdown arrow.")
                                } else {
                                    Icon(painter = painterResource(R.drawable.baseline_arrow_drop_down_24),contentDescription = "Dropdown arrow.")

                                }
                            }
                        }
                    }
                    items(finishedGames.first.takeWhile { showFinished }, key = {it.id?:it.lastMove.toString()}) {
                        GameSummary(it, scaffoldState, modifier = Modifier.animateItem())
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
                    Spacer(modifier = Modifier.height(20.dp))
                    CreateGameBar(scaffoldState)
                }

            }
        }

    }
}