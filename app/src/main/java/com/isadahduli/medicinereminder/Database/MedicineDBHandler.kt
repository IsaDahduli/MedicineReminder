package com.isadahduli.medicinereminder.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MedicineDBHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "medicine.db"
        private const val DATABASE_VERSION = 7  // Incremented version number

        const val TABLE_MEDICINE = "medicine"
        const val COLUMN_MEDICINE_ID = "medicine_id"
        const val COLUMN_MEDICINE_NAME = "medicine_name"
        const val COLUMN_MEDICINE_QUANTITY = "medicine_quantity"
        const val COLUMN_MEDICINE_UNIT = "medicine_unit"
        const val COLUMN_MEDICINE_TIME = "medicine_time"
        const val COLUMN_MEDICINE_MEAL = "medicine_meal"
        const val COLUMN_MEDICINE_INTERVAL = "medicine_interval"
        const val COLUMN_MEDICINE_START = "medicine_start"
        const val COLUMN_MEDICINE_END = "medicine_end"
        const val COLUMN_REQUEST_CODE = "request_code"  // New column

        private const val TABLE_CREATE =
            "CREATE TABLE $TABLE_MEDICINE (" +
                    "$COLUMN_MEDICINE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_MEDICINE_NAME TEXT, " +
                    "$COLUMN_MEDICINE_QUANTITY TEXT, " +
                    "$COLUMN_MEDICINE_UNIT TEXT, " +
                    "$COLUMN_MEDICINE_TIME TEXT, " +
                    "$COLUMN_MEDICINE_MEAL TEXT, " +
                    "$COLUMN_MEDICINE_INTERVAL TEXT, " +
                    "$COLUMN_MEDICINE_START TEXT, " +
                    "$COLUMN_MEDICINE_END TEXT, " +
                    "$COLUMN_REQUEST_CODE INTEGER)"  // Include the request code
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 7) {
            db.execSQL("ALTER TABLE $TABLE_MEDICINE ADD COLUMN $COLUMN_REQUEST_CODE INTEGER")
        }
    }
}

