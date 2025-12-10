package com.example.notificationreader

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var iconView: ImageView
    private lateinit var titleView: TextView
    private lateinit var subTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var permissionButton: Button

    private val navUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val title = intent.getStringExtra("title")
            val subText = intent.getStringExtra("subText")
            val progress = intent.getIntExtra("progress", -1)
            val progressMax = intent.getIntExtra("progressMax", -1)
            val bitmap = intent.getParcelableExtra<Bitmap>("iconBitmap")

            titleView.text = title ?: ""
            subTextView.text = subText ?: ""

            if (progress >= 0 && progressMax > 0) {
                progressBar.max = progressMax
                progressBar.progress = progress
            }

            if (bitmap != null) {
                iconView.setImageBitmap(bitmap)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        iconView = findViewById(R.id.navIcon)
        titleView = findViewById(R.id.navTitle)
        subTextView = findViewById(R.id.navSubText)
        progressBar = findViewById(R.id.navProgress)
        permissionButton = findViewById(R.id.permissionButton)

        permissionButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        ContextCompat.registerReceiver(
            this,
            navUpdateReceiver,
            IntentFilter("MAPS_NAV_UPDATE"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        updatePermissionButton()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(navUpdateReceiver)
    }

    private fun isNotificationListenerEnabled(): Boolean {
        val cn = ComponentName(this, MyNotificationListener::class.java)
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return flat != null && flat.contains(cn.flattenToString())
    }

    private fun updatePermissionButton() {
        if (isNotificationListenerEnabled()) {
            permissionButton.visibility = Button.GONE
        } else {
            permissionButton.visibility = Button.VISIBLE
        }
    }
}
