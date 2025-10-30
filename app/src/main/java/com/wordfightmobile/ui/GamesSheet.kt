package com.wordfightmobile.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
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

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesSheet(scaffoldState: BottomSheetScaffoldState) {
    val viewModel : GamesViewModel = viewModel()
    val hasGames by remember {
        derivedStateOf {
            viewModel.games.isNotEmpty()
        }
    }
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
                items(viewModel.games, key = {it.id?:it.lastMove.toString()}) {
                    GameSummary(it, scaffoldState)
                }
                item {
                    CreateGameBar(scaffoldState)
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