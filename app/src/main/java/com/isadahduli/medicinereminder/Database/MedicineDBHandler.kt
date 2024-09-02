package com.isadahduli.medicinereminder.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MedicineDBHandler(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "medicine.db"
        private const val DB_VERSION = 1

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
                    "$COLUMN_MEDICINE_END TEXT)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEDICINE")
        db.execSQL(TABLE_CREATE)
    }
}