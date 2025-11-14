package com.wordfightmobile

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.credentials.CredentialManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.wordfightmobile.data.Block
import com.wordfightmobile.ui.GameGrid
import com.wordfightmobile.ui.GameScreen
import com.wordfightmobile.ui.GamesSheet
import com.wordfightmobile.ui.GridSize
import com.wordfightmobile.ui.theme.WordfightMobileTheme
import com.wordfightmobile.viewModels.AuthViewModel
import com.wordfightmobile.viewModels.GamesViewModel
import android.net.Uri
import com.wordfightmobile.ui.AcceptFriendAlert
import com.wordfightmobile.ui.CreateGameAlert
import com.wordfightmobile.ui.FriendsAlert
import com.wordfightmobile.viewModels.FriendsViewModel
import com.wordfightmobile.viewModels.OpenAlert

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val permissionState = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
        if (permissionState != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }

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
        createNotificationChannel()
        enableEdgeToEdge()
        setContent {
            val scope = rememberCoroutineScope()
            val authViewModel: AuthViewModel = viewModel()
            val gamesViewModel: GamesViewModel = viewModel()
            val friendsViewModel: FriendsViewModel = viewModel()
            val context = LocalContext.current
            val credentialManager = CredentialManager.create(context)
            val scaffoldState = rememberBottomSheetScaffoldState()
            val homeGrid = homeGrid()
            val hasGame by remember {
                derivedStateOf {
                    gamesViewModel.gameId.value != null
                }
            }
            LaunchedEffect(Unit) {
                handleIntents((context as? Activity)?.intent, openGame = { gameId ->
                    gamesViewModel.gameId.value = gameId
                }) {
                    friendsViewModel.acceptInviteCode(it)
                    friendsViewModel.openAlert.value = OpenAlert.ConfirmFriendship
                }
                if (authViewModel.checkLogin()) {
                    gamesViewModel.getGames()
                } else {
                    authViewModel.login(scope, credentialManager, context) {
                        gamesViewModel.getGames()
                        val prefs = getSharedPreferences("fcm",MODE_PRIVATE)
                        val token = prefs.getString("pendingFCMToken", null)
                        if (token != null) {
                            val db = Firebase.firestore
                            val auth = Firebase.auth
                            auth.uid?.let { uid ->
                                val img = auth.currentUser?.photoUrl
                                db.collection("users").document(uid).update(
                                    mapOf("FCMToken" to token,"img" to img))
                            }
                        }
                    }
                }
            }
            WordfightMobileTheme {
                MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColors else lightColors) {
                    BottomSheetScaffold(
                        scaffoldState = scaffoldState,
                        sheetContent = {
                            GamesSheet(scaffoldState)
                        },
                        sheetPeekHeight = 220.dp,
                        modifier = Modifier.fillMaxSize()) { innerPadding ->
                        when (friendsViewModel.openAlert.value) {
                            OpenAlert.ConfirmFriendship -> AcceptFriendAlert { friendsViewModel.openAlert.value =
                                OpenAlert.None
                            }
                            OpenAlert.None -> {}
                            OpenAlert.Friends ->
                                FriendsAlert { friendsViewModel.openAlert.value = OpenAlert.None }
                            OpenAlert.CreateGame -> CreateGameAlert()
                        }
                        Column(modifier = Modifier.padding(innerPadding),
                            horizontalAlignment =  Alignment.CenterHorizontally
                        ) {
//                            Button({
//                                scope.launch {
//                                    authViewModel.logout(credentialManager)
//                                    authViewModel.login(scope,credentialManager,context) {
//                                        gamesViewModel.getGames()
//                                        gamesViewModel.gameId.value = null
//                                    }
//                                } },
//                                modifier = Modifier.padding(top = 50.dp)) {
//                                Text(
//                                    "Log Out"
//                                )
//                            }
                            Spacer(modifier = Modifier.height(80.dp))
                            Crossfade(hasGame, animationSpec = tween(4000, delayMillis = 600)) { hasGame ->
                                if (!hasGame) {
                                    Column {
                                        Spacer(modifier = Modifier.height(70.dp))
                                        GameGrid(homeGrid, GridSize.Large, clickEnabled = false)
                                    }
                                } else {
                                    gamesViewModel.currentGame.value?.let { currentGame ->
                                        GameScreen(currentGame)
                                    }
                                }
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

    fun handleIntents(intent: Intent?, openGame: (String) -> Unit, openFriendConfirmation: (String) -> Unit) {
        intent?.let {
            it.getStringExtra("gameId")?.let {
                openGame(it)
            }
            val appLinkData: Uri? = it.data
            val code = appLinkData?.getQueryParameter("code")
            if (code != null) {
                openFriendConfirmation(code)
            }

        }
    }

    fun createNotificationChannel() {
        val channel = NotificationChannel(
            "turn_reminder_channel",
            "Turn Reminders",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Used for turn reminders"

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}