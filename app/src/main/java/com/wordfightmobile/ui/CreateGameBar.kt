package com.wordfightmobile.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wordfightmobile.viewModels.GamesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGameBar(scaffoldState: BottomSheetScaffoldState) {
    val scope = rememberCoroutineScope()
    val viewModel : GamesViewModel = viewModel()
    val context = LocalContext.current

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Button({
            scope.launch {
                viewModel.createGame()
                scaffoldState.bottomSheetState.partialExpand()
                Toast.makeText(context, "Finding game for you...", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(
                text = "Play Against Random Opponent", modifier = Modifier.width(120.dp))
        }
        VerticalDivider(color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.height(60.dp).padding(horizontal = 10.dp))
        Button({
            scope.launch {
                Toast.makeText(context, "Not implemented yet.", Toast.LENGTH_LONG).show()
            }
        }) {
            Text(
                text = "Play Against A Friend")
        }
    }
}