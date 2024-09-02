package com.isadahduli.medicinereminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity




class MainMenuActivity : AppCompatActivity() {
    private var GoToAddMedicineButton: Button? = null
    private var ViewAllMedicineButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        GoToAddMedicineButton = findViewById<View>(R.id.button_goToAddMedicine) as Button
        ViewAllMedicineButton = findViewById<View>(R.id.button_gotViewAllMedicines) as Button
        createNotificationChannel()
    }

    fun goToAddMedicine(view: View?) {
        GoToAddMedicineButton!!.setOnClickListener {
            val i = Intent(
                this@MainMenuActivity,
                AddMedicineActivity::class.java
            )
            startActivity(i)
        }
    }

    fun goToViewAllMedicine(view: View?) {
        ViewAllMedicineButton!!.setOnClickListener {
            val i = Intent(
                this@MainMenuActivity,
                ViewAllMedicinesActivity::class.java
            )
            startActivity(i)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Medicine Reminder Channel"
            val descriptionText = "Channel for medicine reminder notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("medicine_reminder_channel", name, importance).apply {
                description = descriptionText
                lightColor = Color.GREEN
                enableLights(true)
                enableVibration(true)  // Optional: Enable vibration along with the LED
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}