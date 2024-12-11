package com.example.chatter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Random

class FirebaseMessageService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        message.notification?.let {
            showNotification(it.title, it.body)
        }
    }

    private fun showNotification(title: String?, message: String?) {
        Firebase.auth.currentUser?.let {
            if(title?.contains(it.displayName.toString()) == true || message?.contains(it.displayName.toString()) == true) return
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random().nextInt(1000)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =   NotificationChannel("messages", "Messages", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        } else {

        }
        val notification = NotificationCompat.Builder(this, "messages")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        notificationManager.notify(notificationId, notification)

    }
}