package com.jainpanchang.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootAndAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return

        when (action) {
            "com.jainpanchang.ACTION_TRIGGER_REMINDER" -> {
                val title = intent.getStringExtra("extra_title") ?: "જૈન પંચાંગ સ્મરણ"
                val message = intent.getStringExtra("extra_message") ?: "આજનો સમય અને તિથિ"
                val reqCode = intent.getIntExtra("extra_req_code", 1001)
                AlarmScheduler.showNotification(context, reqCode, title, message)
            }
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                // Alarms are persistent and rescheduled
            }
        }
    }
}
