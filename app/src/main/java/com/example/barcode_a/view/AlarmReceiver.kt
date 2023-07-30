package com.example.barcode_a.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Handle the alarm event (e.g., show a notification, play a sound)
        // In this example, we simply show a toast message
        Toast.makeText(context, "Alarm triggered!", Toast.LENGTH_SHORT).show()

        // Vibrate the device when the alarm is triggered
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            val pattern = longArrayOf(0, 500, 200, 500) // Vibration pattern (wait, vibrate, wait, vibrate)
            vibrator.vibrate(pattern, -1)
        }
    }
}