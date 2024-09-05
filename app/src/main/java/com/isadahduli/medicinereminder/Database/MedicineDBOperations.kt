package com.isadahduli.medicinereminder.Database

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.isadahduli.medicinereminder.MedicineReminderReceiver
import com.isadahduli.medicinereminder.Model.Medicine

class MedicineDBOperations(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val LOGTAG = "APPOINT_SYS"
        private const val DATABASE_NAME = "medicine.db"
        private const val DATABASE_VERSION = 2  // Incremented version to handle upgrades

        private val allColumns = arrayOf(
            MedicineDBHandler.COLUMN_MEDICINE_ID,
            MedicineDBHandler.COLUMN_MEDICINE_NAME,
            MedicineDBHandler.COLUMN_MEDICINE_QUANTITY,
            MedicineDBHandler.COLUMN_MEDICINE_UNIT,
            MedicineDBHandler.COLUMN_MEDICINE_TIME,
            MedicineDBHandler.COLUMN_MEDICINE_MEAL,
            MedicineDBHandler.COLUMN_MEDICINE_INTERVAL,
            MedicineDBHandler.COLUMN_MEDICINE_START,
            MedicineDBHandler.COLUMN_MEDICINE_END,
            MedicineDBHandler.COLUMN_REQUEST_CODE  // Include request_code in allColumns
        )
    }

    private var database: SQLiteDatabase? = null

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_MEDICINE_TABLE = """
            CREATE TABLE ${MedicineDBHandler.TABLE_MEDICINE} (
                ${MedicineDBHandler.COLUMN_MEDICINE_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${MedicineDBHandler.COLUMN_MEDICINE_NAME} TEXT,
                ${MedicineDBHandler.COLUMN_MEDICINE_QUANTITY} TEXT,
                ${MedicineDBHandler.COLUMN_MEDICINE_UNIT} TEXT,
                ${MedicineDBHandler.COLUMN_MEDICINE_TIME} TEXT,
                ${MedicineDBHandler.COLUMN_MEDICINE_MEAL} TEXT,
                ${MedicineDBHandler.COLUMN_MEDICINE_INTERVAL} TEXT,
                ${MedicineDBHandler.COLUMN_MEDICINE_START} TEXT,
                ${MedicineDBHandler.COLUMN_MEDICINE_END} TEXT,
                ${MedicineDBHandler.COLUMN_REQUEST_CODE} INTEGER 
            )
        """.trimIndent()
        db.execSQL(CREATE_MEDICINE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE ${MedicineDBHandler.TABLE_MEDICINE} ADD COLUMN ${MedicineDBHandler.COLUMN_REQUEST_CODE} INTEGER DEFAULT 0")
        }
    }

    fun open() {
        Log.i(LOGTAG, "Database Opened")
        database = writableDatabase
    }

    override fun close() {
        database?.let {
            if (it.isOpen) {
                Log.i(LOGTAG, "Database Closed")
                it.close()
            }
        }
    }

    fun addMedicine(medicine: Medicine): Medicine {
        val requestCode = (medicine.MedicineName + medicine.MedicineTime.replace(":", "")).hashCode()

        val values = ContentValues().apply {
            put(MedicineDBHandler.COLUMN_MEDICINE_NAME, medicine.MedicineName)
            put(MedicineDBHandler.COLUMN_MEDICINE_QUANTITY, medicine.MedicineQuantity)
            put(MedicineDBHandler.COLUMN_MEDICINE_UNIT, medicine.MedicineUnit)
            put(MedicineDBHandler.COLUMN_MEDICINE_TIME, medicine.MedicineTime)
            put(MedicineDBHandler.COLUMN_MEDICINE_MEAL, medicine.MedicineMeal)
            put(MedicineDBHandler.COLUMN_MEDICINE_INTERVAL, medicine.MedicineInterval)
            put(MedicineDBHandler.COLUMN_MEDICINE_START, medicine.MedicineStartDate)
            put(MedicineDBHandler.COLUMN_MEDICINE_END, medicine.MedicineEndDate)
            put(MedicineDBHandler.COLUMN_REQUEST_CODE, requestCode)  // Store the request code
        }

        val insertMedicineID = database?.insert(MedicineDBHandler.TABLE_MEDICINE, null, values) ?: -1
        medicine.MedicineID = insertMedicineID
        medicine.RequestCode = requestCode  // Ensure it's stored in the Medicine object as well
        return medicine
    }

    fun getMedicine(id: Long): Medicine? {
        val cursor: Cursor? = database?.query(
            MedicineDBHandler.TABLE_MEDICINE,
            allColumns,
            "${MedicineDBHandler.COLUMN_MEDICINE_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                return Medicine(
                    MedicineID = it.getLong(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_MEDICINE_ID)),
                    MedicineName = it.getString(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_MEDICINE_NAME)),
                    MedicineQuantity = it.getString(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_MEDICINE_QUANTITY)),
                    MedicineUnit = it.getString(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_MEDICINE_UNIT)),
                    MedicineTime = it.getString(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_MEDICINE_TIME)),
                    MedicineMeal = it.getString(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_MEDICINE_MEAL)),
                    MedicineInterval = it.getString(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_MEDICINE_INTERVAL)),
                    MedicineStartDate = it.getString(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_MEDICINE_START)),
                    MedicineEndDate = it.getString(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_MEDICINE_END)),
                    RequestCode = it.getInt(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_REQUEST_CODE))  // Retrieve request_code
                )
            }
        }
        return null
    }

    fun getAllMedicines(): List<Medicine> {
        val medicines = mutableListOf<Medicine>()
        val cursor: Cursor? = database?.query(
            MedicineDBHandler.TABLE_MEDICINE,
            allColumns,
            null,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val medicine = Medicine(
                    MedicineID = it.getLong(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_MEDICINE_ID)),
                    MedicineName = it.getString(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_MEDICINE_NAME)),
                    MedicineQuantity = it.getString(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_MEDICINE_QUANTITY)),
                    MedicineUnit = it.getString(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_MEDICINE_UNIT)),
                    MedicineTime = it.getString(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_MEDICINE_TIME)),
                    MedicineMeal = it.getString(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_MEDICINE_MEAL)),
                    MedicineInterval = it.getString(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_MEDICINE_INTERVAL)),
                    MedicineStartDate = it.getString(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_MEDICINE_START)),
                    MedicineEndDate = it.getString(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_MEDICINE_END)),
                    RequestCode = it.getInt(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_REQUEST_CODE))  // Retrieve request_code
                )
                medicines.add(medicine)
            }
        }
        return medicines
    }

    fun updateMedicine(medicine: Medicine): Int {
        val values = ContentValues().apply {
            put(MedicineDBHandler.COLUMN_MEDICINE_NAME, medicine.MedicineName)
            put(MedicineDBHandler.COLUMN_MEDICINE_QUANTITY, medicine.MedicineQuantity)
            put(MedicineDBHandler.COLUMN_MEDICINE_UNIT, medicine.MedicineUnit)
            put(MedicineDBHandler.COLUMN_MEDICINE_TIME, medicine.MedicineTime)
            put(MedicineDBHandler.COLUMN_MEDICINE_MEAL, medicine.MedicineMeal)
            put(MedicineDBHandler.COLUMN_MEDICINE_INTERVAL, medicine.MedicineInterval)
            put(MedicineDBHandler.COLUMN_MEDICINE_START, medicine.MedicineStartDate)
            put(MedicineDBHandler.COLUMN_MEDICINE_END, medicine.MedicineEndDate)
            put(MedicineDBHandler.COLUMN_REQUEST_CODE, medicine.RequestCode)  // Ensure request_code is updated
        }

        return database?.update(
            MedicineDBHandler.TABLE_MEDICINE,
            values,
            "${MedicineDBHandler.COLUMN_MEDICINE_ID} = ?",
            arrayOf(medicine.MedicineID.toString())
        ) ?: 0
    }

    fun deleteMedicine(medicineId: Long, context: Context) {
        // Use getRequestCodeForMedicine to retrieve the requestCode before deleting the medicine
        val requestCode = getRequestCodeForMedicine(medicineId)

        if (requestCode != -1) {
            cancelNotification(context, requestCode)
        }

        // Now delete the medicine from the database
        database?.delete(
            MedicineDBHandler.TABLE_MEDICINE,
            "${MedicineDBHandler.COLUMN_MEDICINE_ID} = ?",
            arrayOf(medicineId.toString())
        )
    }

    private fun cancelNotification(context: Context, requestCode: Int) {
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
    }

    fun isMedicineNameUnique(medicineName: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${MedicineDBHandler.TABLE_MEDICINE} WHERE ${MedicineDBHandler.COLUMN_MEDICINE_NAME} = ?", arrayOf(medicineName))
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count == 0
    }

    fun getRequestCodeForMedicine(medicineId: Long): Int {
        val cursor: Cursor? = database?.query(
            MedicineDBHandler.TABLE_MEDICINE,
            arrayOf(MedicineDBHandler.COLUMN_REQUEST_CODE),
            "${MedicineDBHandler.COLUMN_MEDICINE_ID} = ?",
            arrayOf(medicineId.toString()),
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                return it.getInt(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_REQUEST_CODE))
            }
        }

        Log.e("MedicineReminderReceiver", "Failed to find requestCode for medicineId: $medicineId")
        return -1  // Return a default value to indicate failure
    }


}
