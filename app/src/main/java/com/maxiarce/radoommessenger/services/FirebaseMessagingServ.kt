package com.maxiarce.radoommessenger.services
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.maxiarce.radoommessenger.ChatActivity
import com.maxiarce.radoommessenger.NewMessageActivity.Companion.USER_KEY
import com.maxiarce.radoommessenger.R
import com.maxiarce.radoommessenger.models.User


class FirebaseMessagingServ : FirebaseMessagingService() {


    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        if(p0.data != null){
                sendNotification(p0)
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d("NEWTOKEN",p0)
    }


    private fun sendNotification(p0: RemoteMessage){
        val title = p0.data["title"]
        val body = p0.data["body"]
        val uid = p0.data["uid"]
        val username = p0.data["username"]
        val profileImageUrl = p0.data["profileimageurl"]
        val token = p0.data["token"]

        Log.d("message:",body)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannelId = "Randoom App"
        val notificationId = 101

        val user = User(uid!!,username!!,profileImageUrl!!,token!!)

        //create intent
        val resultIntent = Intent(this, ChatActivity::class.java)
        resultIntent.putExtra(USER_KEY,user)
        val pendingIntent = PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        //check android version for notification and channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O  ){

            val notificationChannel = NotificationChannel(notificationChannelId, "Randoom App Notification",NotificationManager.IMPORTANCE_DEFAULT)
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build()

            //Configure Notification Channel
            notificationChannel.description = "Notification channel for Randoom App"
            notificationChannel.enableLights(false)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(false)
            notificationChannel.setSound(soundUri,audioAttributes)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager.createNotificationChannel(notificationChannel)


            val notification = Notification.Builder(this,notificationChannelId)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.ic_main_notification_icon)
                    .setChannelId(notificationChannelId)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(pendingIntent)
                    .build()
            notificationManager.notify(notificationId,notification)

        }
        else{
            val notification = Notification.Builder(this)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.ic_main_notification_icon)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .build()
            notificationManager.notify(notificationId,notification)
        }




    }



}