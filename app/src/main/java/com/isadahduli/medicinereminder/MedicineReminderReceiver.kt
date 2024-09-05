package com.isadahduli.medicinereminder

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.isadahduli.medicinereminder.Database.MedicineDBOperations
import java.text.SimpleDateFormat
import java.util.*

class MedicineReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medicineName = intent.getStringExtra("medicine_name") ?: "Medicine"
        val medicineId = intent.getLongExtra("medicine_id", -1)
        val medicineEndDate = intent.getStringExtra("medicine_end_date")
        val medicineTime = intent.getStringExtra("medicine_time")
        val medicineUnit = intent.getStringExtra("medicine_unit")
        val medicineQuantity = intent.getStringExtra("medicine_quantity")
        val medicineInterval = intent.getLongExtra("medicine_interval", 0L)

        // Combine the end date and time into a single Date object
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        val endDateTime: Date? = sdf.parse("$medicineEndDate $medicineTime")

        if (endDateTime == null) {
            Log.e("MedicineReminderReceiver", "Failed to parse end date and time.")
            return
        }

        // Get the current time
        val currentTime = Calendar.getInstance().time

        // Check if the current time is after the end date and time
        if (currentTime.after(endDateTime)) {
            cancelNotification(context, medicineId)
            Log.d("MedicineReminderReceiver", "Notification cancelled as medicine end date has passed.")
            return
        }

        // Check for notification permission on Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.e("MedicineReminderReceiver", "Notification permission not granted.")
                return
            }
        }

        // Create and show the notification with medicine details
        val notificationBuilder = NotificationCompat.Builder(context, "medicine_reminder_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Time to take your medicine")
            .setContentText("Take $medicineName: $medicineQuantity $medicineUnit, every ${medicineInterval / 60000} minutes")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setLights(0xFF00FFFF.toInt(), 500, 2000)

        with(NotificationManagerCompat.from(context)) {
            notify(medicineId.toInt(), notificationBuilder.build())
        }

        // Schedule the next notification based on the interval
        scheduleNextNotification(context, medicineId, medicineName, medicineQuantity!!,
            medicineUnit.toString(), endDateTime, medicineInterval)
    }

    private fun cancelNotification(context: Context, medicineId: Long) {
        val medicineDBOperations = MedicineDBOperations(context)
        medicineDBOperations.open()
        val requestCode = medicineDBOperations.getRequestCodeForMedicine(medicineId)
        medicineDBOperations.close()

        if (requestCode != -1) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, MedicineReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(requestCode)

            Log.d("MedicineReminderReceiver", "Notification and Alarm canceled for medicineId: $medicineId")
        } else {
            Log.e("MedicineReminderReceiver", "Failed to find requestCode for medicineId: $medicineId")
        }
    }

    private fun scheduleNextNotification(
        context: Context,
        medicineId: Long,
        medicineName: String,
        medicineQuantity: String,
        medicineUnit: String,
        endDateTime: Date,
        intervalMillis: Long
    ) {
        val medicineDBOperations = MedicineDBOperations(context)
        medicineDBOperations.open()
        val requestCode = medicineDBOperations.getRequestCodeForMedicine(medicineId)
        medicineDBOperations.close()

        if (requestCode != -1) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val nextTime = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis() + intervalMillis
            }

            if (nextTime.time.before(endDateTime)) {
                val intent = Intent(context, MedicineReminderReceiver::class.java).apply {
                    putExtra("medicine_name", medicineName)
                    putExtra("medicine_id", medicineId)
                    putExtra("medicine_end_date", SimpleDateFormat("yyyy-MM-dd", Locale.US).format(endDateTime))
                    putExtra("medicine_time", SimpleDateFormat("HH:mm", Locale.US).format(nextTime.time))
                    putExtra("medicine_unit", medicineUnit)
                    putExtra("medicine_quantity", medicineQuantity)
                    putExtra("medicine_interval", intervalMillis)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // Check if exact alarm permissions are granted
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                    val intentExactAlarm = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    intentExactAlarm.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intentExactAlarm)
                }

                // Schedule the alarm for the exact time
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextTime.timeInMillis,
                    pendingIntent
                )

                Log.d("MedicineReminderReceiver", "Next notification scheduled for ${nextTime.time}")
            } else {
                Log.d("MedicineReminderReceiver", "No more notifications scheduled; end time has passed.")
            }
        } else {
            Log.e("MedicineReminderReceiver", "Failed to find requestCode for medicineId: $medicineId")
        }
    }
}
