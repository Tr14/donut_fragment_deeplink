package com.android.samples.donuttracker

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pushio.manager.PushIOManager

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        PushIOManager.getInstance(getApplicationContext()).setDeviceToken(s);

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val pushIOManager = PushIOManager.getInstance(applicationContext)

        if (pushIOManager.isResponsysPush(remoteMessage)) {
            pushIOManager.handleMessage(remoteMessage)
        } else {
            // Not a Responsys push notification, handle it appropriately
        }
    }
}