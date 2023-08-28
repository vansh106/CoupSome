package `in`.coupsome.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import `in`.coupsome.R
import `in`.coupsome.SplashActivity

class FcmService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FcmService.kt", "YASH => onMessageReceived:11 $message")
        Log.d("FcmService.kt", "YASH => onMessageReceived:14 ${message.notification}")
        if (message.data.isNotEmpty()) {
            val title = message.data["title"]
            val body = message.data["body"]
            sendNotification(title, body)
        } else if (message.notification != null) {
            sendNotification(message.notification?.title, message.notification?.body)
        }
    }

    private fun sendNotification(title: String?, body: String?) {
        Log.d("FcmService.kt", "YASH => sendNotification:24 $title $body")
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val channelId = "Default"
        val notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)

            val notification = Notification.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()
            notificationManager.notify(0, notification)
        } else {
            val notification = Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()
            notificationManager.notify(0, notification)
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        FirebaseAuth.getInstance().currentUser?.uid?.let {
            FirebaseDatabase.getInstance().getReference("Users")
                .child(it)
                .child("fcm_token")
                .setValue(token)
        }
    }

}