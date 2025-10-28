package com.wordfightmobile.viewModels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.functions.functions
import com.wordfightmobile.data.Block
import com.wordfightmobile.data.Game
import kotlinx.coroutines.launch

class GamesViewModel : ViewModel() {
    private var functions = Firebase.functions
    private var db = Firebase.firestore
    private var auth = Firebase.auth
    val games = mutableStateListOf<Game>()
    var gameId : MutableState<String?> = mutableStateOf(null)
    val currentGame = derivedStateOf {
        Log.d("gameId",gameId.value.toString())
        games.find { it.id == gameId.value && gameId.value != null}
    }
    val currentWord = mutableStateListOf<Block>()
    val scope = viewModelScope

    fun getGames() {
        scope.launch {
            db.collection("games").addSnapshotListener { docs,e ->
                games.clear()
                docs?.forEach { doc ->
                    val game = doc.toObject<Game>(Game::class.java)
                    games.add(game.copy(id=doc.id))
                }
            }
        }
    }
    fun createGame(opponent: String = "opponent", opponentName: String = "opponent") {
        functions.getHttpsCallable("create_game")
            .call(
                hashMapOf(
                    "user" to auth.uid.toString(),
                    "user_name" to auth.currentUser?.displayName,
                    "opponent" to opponent,
                    "opponent_name" to opponentName
                )
            ).continueWith { task ->
                val result = task.result?.data as Map<String, *>
                gameId.value = result["id"] as String
                currentWord.clear()
                Log.d("newgame", gameId.value.toString())
            }
    }
    fun submitWord() {
        if (currentWord.isEmpty()) return
        Log.d("function","calling...")
        functions.getHttpsCallable("submit_word")
            .call(
                hashMapOf(
                    "gameId" to gameId.value,
                    "word" to currentWord.toList().map { mapOf(
                        "letter" to it.letter,
                        "index" to it.index,
                        "allegiance" to it.allegiance,
                        "surrounded" to it.surrounded
                    ) }
                )
            ).continueWith { task ->
                val result = task.result?.data as Map<String, *>
                currentWord.clear()
                Log.d("newgame", result.toString())
            }
    }
}