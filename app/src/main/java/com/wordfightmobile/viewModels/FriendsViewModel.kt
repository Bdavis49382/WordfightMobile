package com.wordfightmobile.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.wordfightmobile.data.Player
import java.util.UUID

enum class OpenAlert {
    Friends, ConfirmFriendship, CreateGame, None
}

class FriendsViewModel: ViewModel() {
    var inviteCode: MutableState<String?> = mutableStateOf(null)
    val users = Firebase.firestore.collection("users")
    val friendName: MutableState<String?> = mutableStateOf(null)
    val friendId: MutableState<String?> = mutableStateOf(null)
    val friends = mutableStateListOf<Player>()
    val openAlert = mutableStateOf(OpenAlert.None)

    fun closeAlerts() {
        openAlert.value = OpenAlert.None
    }

    fun getInviteCode(uid: String) {
        users.document(uid).get().addOnCompleteListener { task ->
            val data = task.result.data
            if (data != null) {
                val friendCode = data["friendCode"]
                if (friendCode != null) {
                    inviteCode.value = friendCode.toString()
                } else {
                    inviteCode.value = UUID.randomUUID().toString()
                    users.document(uid).update("friendCode",inviteCode.value)
                }
            }
        }
    }

    fun acceptInviteCode(code: String) {
        users.whereEqualTo("friendCode",code).get().addOnCompleteListener { task ->
            val data = task.result
            if (data.size() == 1) {
                val player = data.first()
                val uid = Firebase.auth.uid.toString()
                if (player.id != uid) {
                    friendName.value = player.get("name").toString()
                    friendId.value = player.id
                    users.document(uid).update("friends", FieldValue.arrayUnion(player.id))
                    users.document(player.id).update("friends", FieldValue.arrayUnion(uid))
                }
            }
        }
    }

    fun getFriends(uid: String) {
        users.get().addOnCompleteListener { task ->

            val players = task.result?.map {
                val p = it.toObject<Player>(Player::class.java)
                p.id = it.id
                p
            }
            if (players != null) {
                val friendsData : List<String>? = players.find { it.id == uid}?.friends
                friends.clear()
                friends.addAll(players.filter { friendsData?.contains(it.id) == true })
            }
        }
    }
}