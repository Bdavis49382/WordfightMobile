package com.wordfightmobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wordfightmobile.R
import com.wordfightmobile.data.Block
import com.wordfightmobile.viewModels.AuthViewModel
import com.wordfightmobile.viewModels.GamesViewModel

enum class GridSize { Small, Large}

@Composable
fun GameGrid(blocks: List<Block>, size: GridSize,clickEnabled: Boolean = false) {
    val authViewModel : AuthViewModel = viewModel()
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        for (i in 0..20 step 5) {
            Row(modifier = Modifier.fillMaxWidth(.9f), horizontalArrangement = Arrangement.Center) {
                blocks.subList(i, i + 5).forEach {
                    BlockView(
                        it,
                        authViewModel.uid.toString(),
                        gridSize = size,
                        clickEnabled = clickEnabled
                    )
                }
            }
        }
    }
}

@Composable
fun BlockView(block: Block, user: String, modifier: Modifier = Modifier, gridSize: GridSize = GridSize.Large, clickEnabled: Boolean = false) {
    var clicked by remember {mutableStateOf(block.clicked)}
    val gamesViewModel : GamesViewModel = viewModel()
    val context = LocalContext.current
    val background = Color(ContextCompat.getColor(context, when (block.allegiance) {
        user -> if (block.surrounded) R.color.solid_blue else R.color.blue
        "none" -> R.color.tan
        else -> if (block.surrounded) R.color.solid_red else R.color.red
    }))
    val color = Color.Black
    val alpha by remember {derivedStateOf {
        if (clicked) .3f else 1f
    }}
    val blockSize = if (gridSize == GridSize.Large) 70.dp else 20.dp
    val corners = if (gridSize == GridSize.Large) 10.dp else 3.dp
    Box(contentAlignment = Alignment.Center, modifier = modifier
        .size(blockSize)
        .padding(2.dp)
        .clip(RoundedCornerShape(corners))
        .background(background.copy(alpha = alpha), shape = RoundedCornerShape(corners))
        .alpha(alpha)
        .clickable(onClick = {
            if (clickEnabled) {
                if (!clicked) {
                    gamesViewModel.currentWord.add(block)
                } else {
                    gamesViewModel.currentWord.remove(block)
                }
                clicked = !clicked
            }
        })
    ) {
        if (gridSize == GridSize.Large) {
            Text(
                block.letter.toString(),
                color = color,
                textAlign = TextAlign.Center,
                fontSize = if (gridSize == GridSize.Large) 30.sp else 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.height(blockSize).wrapContentHeight(align = Alignment.CenterVertically)
            )

        }
    }
}