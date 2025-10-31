package com.wordfightmobile.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.wordfightmobile.MainActivity
import com.wordfightmobile.R

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateTokenInFirestore(token)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val gameId = message.data["gameId"]
        val title = message.data["title"]
        val message = message.data["message"]

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("gameId", gameId)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, "turn_reminder_channel")
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.baseline_grid_on_24)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(0, notification)
    }
    fun updateTokenInFirestore(token: String) {
        val db = Firebase.firestore
        val auth = Firebase.auth
        auth.uid?.let { uid ->
            db.collection("users").document(uid).update(
                mapOf("FCMToken" to token))
        }
    }



}