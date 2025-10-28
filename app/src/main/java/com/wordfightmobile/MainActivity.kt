package com.wordfightmobile

import android.os.Build
import androidx.credentials.CredentialManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.wordfightmobile.data.Block
import com.wordfightmobile.ui.GameGrid
import com.wordfightmobile.ui.GameScreen
import com.wordfightmobile.ui.GamesSheet
import com.wordfightmobile.ui.GridSize
import com.wordfightmobile.ui.theme.WordfightMobileTheme
import com.wordfightmobile.viewModels.AuthViewModel
import com.wordfightmobile.viewModels.GamesViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        val darkColors = darkColorScheme(
            primaryContainer = Color(0xFFD3AE61),
            onPrimaryContainer = Color.Black,
            secondaryContainer = Color(0xFF6080D0),
            onSecondaryContainer = Color.Black,
            tertiaryContainer = Color(0xFFEE6258),
            onTertiaryContainer = Color.Black
        )
        val lightColors = lightColorScheme(
            primaryContainer = Color(0xFFD3AE61),
            onPrimaryContainer = Color.Black,
            secondaryContainer = Color(0xFF8EAAF6),
            onSecondaryContainer = Color.Black,
            tertiaryContainer = Color(0xFFEE6258),
            onTertiaryContainer = Color.Black
        )

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val scope = rememberCoroutineScope()
            val authViewModel: AuthViewModel = viewModel()
            val gamesViewModel: GamesViewModel = viewModel()
            val context = LocalContext.current
            val credentialManager = CredentialManager.create(context)
            LaunchedEffect(Unit) {
                if (authViewModel.checkLogin()) {
                    Log.d("user", authViewModel.uid.toString())
                    gamesViewModel.getGames()
                } else {
                    authViewModel.login(scope, credentialManager, context) {
                        Log.d("user", authViewModel.uid.toString())
                        gamesViewModel.getGames()
                    }
                }
//                authViewModel.logout(credentialManager)

            }
            WordfightMobileTheme {
                MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColors else lightColors) {
                    BottomSheetScaffold(sheetContent = {
                            GamesSheet()
                        },
                        sheetPeekHeight = 220.dp,
                        modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding), verticalArrangement = Arrangement.Center,
                            horizontalAlignment =  Alignment.CenterHorizontally
                            ) {
                            if (gamesViewModel.currentGame.value == null) {
                                GameGrid(homeGrid(), GridSize.Large, clickEnabled = false)
                            }
                            gamesViewModel.currentGame.value?.let { game ->
                                GameScreen(game)
                            }
                        }
                    }
                }
            }
        }
    }
    fun homeGrid(): List<Block> {
        val dice = listOf<String>("AAEEGN","ELRTTY","AOOTTW","ABBJOO","EHRTVW","CIMOTU","DISTTY","EIOSST","DELRVY","ACHOPS","HIMNQU","EEINSU","EEGHNW","AFFKPS","HLNNRZ","DEILRX","AAEEGN","ACHOPS","AFFKPS","DEILRX","DELRVY","EEGHNW","EIOSST","HIMNQU","HLNNRZ")
        val grid = dice.shuffled().mapIndexed { i,die ->  Block(letter=die.random().toString(),i, clicked = true) }.toMutableList()
        "WORD".forEachIndexed { i,letter ->
            grid[5+i]= grid[5+i].copy(letter = letter.toString(), allegiance = Firebase.auth.uid.toString(), clicked = false)
        }
        "FIGHT".forEachIndexed { i,letter ->
            grid[10+i]= grid[10+i].copy(letter = letter.toString(), allegiance = "notUser", clicked = false)
        }
        return grid
    }
}