package com.example.notificationreader

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.graphics.drawable.toBitmap

class MyNotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName != "com.google.android.apps.maps") return

        val extras = sbn.notification.extras

        val title = extras.getCharSequence("android.title")?.toString()
        val subText = extras.getCharSequence("android.subText")?.toString()
        val progressMax = extras.getInt("android.progressMax", -1)
        val progress = extras.getInt("android.progress", -1)

        val icon: Icon? = extras.getParcelable("android.largeIcon")
        var bitmap: Bitmap? = null

        if (icon != null) {
            try {
                val drawable = icon.loadDrawable(this)
                bitmap = drawable?.toBitmap()
            } catch (e: Exception) {
                Log.e("MapsIcon", "Failed to process icon: $e")
            }
        }

        Log.d("MapsNotif", """
            --- MAPS UPDATE ---
            title       = $title
            subText     = $subText
            progress    = $progress
            progressMax = $progressMax
            iconBitmap  = ${bitmap != null}
        """.trimIndent())

        // Broadcast the data to the UI
        val intent = Intent("MAPS_NAV_UPDATE")
        intent.setPackage(this.packageName)
        intent.putExtra("title", title)
        intent.putExtra("subText", subText)
        intent.putExtra("progress", progress)
        intent.putExtra("progressMax", progressMax)
        intent.putExtra("iconBitmap", bitmap)
        sendBroadcast(intent)
    }
}
