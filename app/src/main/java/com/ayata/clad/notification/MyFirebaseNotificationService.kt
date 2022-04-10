package com.ayata.clad.notification

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.profile.reviews.ReviewFromActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Override base class methods to handle any events required by the application.
 * All methods are invoked on a background thread, and may be called when the app is in the background or not open.
 *
 *  The registration token may change when:
 *  - The app deletes Instance ID
 *  - The app is restored on a new device
 *  - The user uninstalls/reinstall the app
 *  - The user clears app data.
 */
class MyFirebaseNotificationService : FirebaseMessagingService() {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("SellerFirebaseService ", "Refreshed token :: $token")
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        Log.d("checktoken", "sendRegistrationToServer: " + token);
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.i("SellerFirebaseService ", "Message :: ${remoteMessage.notification?.title}")
        val title = remoteMessage.notification?.title
        val message = remoteMessage.notification?.body
        ShowNotification(
            title,
            message
        )
    }

    private fun ShowNotification(title: String?, message: String?) {
        Log.d("received", "ShowNotification: " + title);
        val myIntent = Intent(this, ReviewFromActivity::class.java)
        myIntent.setFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    or Intent.FLAG_ACTIVITY_SINGLE_TOP
        )

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            myIntent,
            0
        )
        val builder = NotificationCompat.Builder(this, "Medipuzzle")
            .setAutoCancel(true)
            .setContentTitle(title)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setSmallIcon(R.mipmap.ic_logo_app)
        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(100, builder.build())
    }
}