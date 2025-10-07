package com.example.habbitmate.services

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.habbitmate.R
import com.example.habbitmate.data.PreferencesHelper

class HydrationNotificationService : BroadcastReceiver() {
    
    companion object {
        // Actions for starting/stopping/showing hydration reminders (BroadcastReceiver-based)
        const val CHANNEL_ID = "hydration_reminder_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "start_hydration_reminders"
        const val ACTION_STOP = "stop_hydration_reminders"
        const val ACTION_SHOW_NOTIFICATION = "show_hydration_notification"
        
        fun startService(context: Context, intervalMinutes: Int) {
            val intent = Intent(context, HydrationNotificationService::class.java).apply {
                action = ACTION_START
                putExtra("interval_minutes", intervalMinutes)
            }
            context.sendBroadcast(intent)
        }
        
        fun stopService(context: Context) {
            val intent = Intent(context, HydrationNotificationService::class.java).apply {
                action = ACTION_STOP
            }
            context.sendBroadcast(intent)
        }
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_START -> {
                val intervalMinutes = intent.getIntExtra("interval_minutes", 60)
                scheduleRepeatingNotification(context, intervalMinutes)
            }
            ACTION_STOP -> {
                cancelRepeatingNotification(context)
            }
            ACTION_SHOW_NOTIFICATION -> {
                showNotification(context)
            }
        }
    }
    
    private fun scheduleRepeatingNotification(context: Context, intervalMinutes: Int) {
    // Schedule next hydration reminder; use exact alarms when possible to fire during Doze and avoid duplicate alarms.
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HydrationNotificationService::class.java).apply {
            action = ACTION_SHOW_NOTIFICATION
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val intervalMillis = intervalMinutes * 60 * 1000L
        val triggerAtMillis = System.currentTimeMillis() + intervalMillis
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                intervalMillis,
                pendingIntent
            )
        }
    }
    
    private fun cancelRepeatingNotification(context: Context) {
        // Cancel scheduled hydration alarm using the same PendingIntent
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HydrationNotificationService::class.java).apply {
            action = ACTION_SHOW_NOTIFICATION
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
    }
    
    // Check preferences, post hydration notification, and reschedule the next reminder.
    private fun showNotification(context: Context) {
        val preferencesHelper = PreferencesHelper(context)
        
        if (!preferencesHelper.isHydrationEnabled()) {
            return
        }
        // Check day-of-week selection
        val calendar = java.util.Calendar.getInstance()
        val today = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        if (!preferencesHelper.isHydrationDayEnabled(today)) {
            // Reschedule next reminder without showing
            val intervalMinutes = preferencesHelper.getHydrationInterval()
            scheduleRepeatingNotification(context, intervalMinutes)
            return
        }
        
    // Build and post the hydration notification (NotificationChannel set elsewhere)
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle(context.getString(R.string.hydration_notification_title))
            .setContentText(context.getString(R.string.hydration_notification_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setOngoing(false)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
        
        // Schedule next notification
        val intervalMinutes = preferencesHelper.getHydrationInterval()
        scheduleRepeatingNotification(context, intervalMinutes)
    }
}
