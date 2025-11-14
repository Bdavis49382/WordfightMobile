package com.wordfightmobile.viewModels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.functions
import com.wordfightmobile.data.Block
import com.wordfightmobile.data.Game

class GamesViewModel : ViewModel() {
    private var functions = Firebase.functions
    private var db = Firebase.firestore
    private var auth = Firebase.auth
    private var listenerRegistration: ListenerRegistration? = null
    val games = mutableStateListOf<Game>()
    var gameId : MutableState<String?> = mutableStateOf(null)
    val currentGame = derivedStateOf {
        games.find { it.id == gameId.value && gameId.value != null}
    }
    val currentWord = mutableStateListOf<Block>()

    fun getGames() {
        listenerRegistration?.remove()
        listenerRegistration = null
        listenerRegistration = db.collection("games")
            .whereArrayContains("players",auth.uid.toString())
            .orderBy("lastMove", Query.Direction.DESCENDING).addSnapshotListener { docs,e ->
                games.clear()
                docs?.forEach { doc ->
                    val game = doc.toObject<Game>(Game::class.java)
                    games.add(game.copy(id=doc.id))
                }
                games.sortBy { it.turn != auth.uid }
                if (gameId.value == null && games.isNotEmpty() && games.first().turn == auth.uid && games.first().finished == false) {
                    gameId.value = games.first().id
                }
                if (e != null) {
                    return@addSnapshotListener
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
            }
    }
    fun submitWord(onFail: (String) -> Unit) {
        if (currentWord.isEmpty()) {
            onFail("No Word Submitted")
            return
        }
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
                if (!task.isSuccessful) {
                    val e = task.exception
                    if (e is FirebaseFunctionsException) {
                        if (e.code.name == "INVALID_ARGUMENT") {
                            onFail(e.message.toString())
                        }
                    } else {
                        Log.e("submission",e.toString())
                    }
                } else {
                    currentWord.clear()
                }
            }
    }

    override fun onCleared() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }
}