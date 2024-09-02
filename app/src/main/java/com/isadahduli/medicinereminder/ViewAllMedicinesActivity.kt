package com.isadahduli.medicinereminder

import android.app.AlarmManager
import android.app.ListActivity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.isadahduli.medicinereminder.Database.MedicineDBOperations
import com.isadahduli.medicinereminder.Model.Medicine

class ViewAllMedicinesActivity : ListActivity() {  // Use `:` for class inheritance in Kotlin
    private lateinit var medicineOps: MedicineDBOperations  // Using `lateinit` for non-nullable property initialization
    private var medicines: MutableList<Medicine> = mutableListOf()  // Initialize with an empty mutable list
    private lateinit var adapter: MedicineAdapter  // Using `lateinit` for non-nullable property initialization

    override fun onCreate(savedInstanceState: Bundle?) {  // Override using `override` keyword
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_medicines)

        medicineOps = MedicineDBOperations(this)
        medicineOps.open()  // Open the database connection
        medicines = medicineOps.getAllMedicines().toMutableList()  // Ensure `medicines` is a mutable list

        adapter = MedicineAdapter(this, medicines)
        listAdapter = adapter  // Set the adapter using `listAdapter` property
    }

    fun removeMedicine(medicine: Medicine) {
        // Remove the selected medicine from the database
        medicineOps.deleteMedicine(medicine.MedicineID)

        // Cancel notification by casting the ID to `Int`
        cancelNotification(medicine.MedicineID.toInt())

        // Remove the item from the list and notify the adapter
        medicines.remove(medicine)
        adapter.notifyDataSetChanged()

        Toast.makeText(this, "Medicine removed successfully!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        medicineOps.close()  // Close the database connection
    }

    private fun cancelNotification(notificationId: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MedicineReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }
}
