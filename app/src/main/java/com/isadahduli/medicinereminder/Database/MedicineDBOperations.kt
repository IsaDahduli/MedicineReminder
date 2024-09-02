package com.isadahduli.medicinereminder.Database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.isadahduli.medicinereminder.Model.Medicine

class MedicineDBOperations(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val LOGTAG = "APPOINT_SYS"
        private const val DATABASE_NAME = "medicine.db"
        private const val DATABASE_VERSION = 1

        private val allColumns = arrayOf(
            MedicineDBHandler.COLUMN_MEDICINE_ID,
            MedicineDBHandler.COLUMN_MEDICINE_NAME,
            MedicineDBHandler.COLUMN_MEDICINE_QUANTITY,
            MedicineDBHandler.COLUMN_MEDICINE_UNIT,
            MedicineDBHandler.COLUMN_MEDICINE_TIME,
            MedicineDBHandler.COLUMN_MEDICINE_MEAL,
            MedicineDBHandler.COLUMN_MEDICINE_INTERVAL,
            MedicineDBHandler.COLUMN_MEDICINE_START,
            MedicineDBHandler.COLUMN_MEDICINE_END
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
                ${MedicineDBHandler.COLUMN_MEDICINE_END} TEXT
            )
        """.trimIndent()
        db.execSQL(CREATE_MEDICINE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${MedicineDBHandler.TABLE_MEDICINE}")
        onCreate(db)
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
        val values = ContentValues().apply {
            put(MedicineDBHandler.COLUMN_MEDICINE_NAME, medicine.MedicineName)
            put(MedicineDBHandler.COLUMN_MEDICINE_QUANTITY, medicine.MedicineQuantity)
            put(MedicineDBHandler.COLUMN_MEDICINE_UNIT, medicine.MedicineUnit)
            put(MedicineDBHandler.COLUMN_MEDICINE_TIME, medicine.MedicineTime)
            put(MedicineDBHandler.COLUMN_MEDICINE_MEAL, medicine.MedicineMeal)
            put(MedicineDBHandler.COLUMN_MEDICINE_INTERVAL, medicine.MedicineInterval)
            put(MedicineDBHandler.COLUMN_MEDICINE_START, medicine.MedicineStartDate)
            put(MedicineDBHandler.COLUMN_MEDICINE_END, medicine.MedicineEndDate)
        }

        val insertMedicineID = database?.insert(MedicineDBHandler.TABLE_MEDICINE, null, values) ?: -1
        medicine.MedicineID = insertMedicineID
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
                    MedicineID = it.getLong(0),
                    MedicineName = it.getString(1),
                    MedicineQuantity = it.getString(2),
                    MedicineUnit = it.getString(3),
                    MedicineTime = it.getString(4),
                    MedicineMeal = it.getString(5),
                    MedicineInterval = it.getString(6),
                    MedicineStartDate = it.getString(7),
                    MedicineEndDate = it.getString(8)
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
                    MedicineEndDate = it.getString(it.getColumnIndexOrThrow(MedicineDBHandler.COLUMN_MEDICINE_END))
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
        }

        return database?.update(
            MedicineDBHandler.TABLE_MEDICINE,
            values,
            "${MedicineDBHandler.COLUMN_MEDICINE_ID} = ?",
            arrayOf(medicine.MedicineID.toString())
        ) ?: 0
    }

    fun deleteMedicine(medicineId: Long) {
        database?.delete(
            MedicineDBHandler.TABLE_MEDICINE,
            "${MedicineDBHandler.COLUMN_MEDICINE_ID} = ?",
            arrayOf(medicineId.toString())
        )
    }
}