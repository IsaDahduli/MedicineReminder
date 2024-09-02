package com.isadahduli.medicinereminder

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.isadahduli.medicinereminder.Database.MedicineDBOperations
import com.isadahduli.medicinereminder.Model.Medicine
import java.text.SimpleDateFormat
import java.util.*

class AddMedicineActivity : AppCompatActivity() {

    private lateinit var medicineNameEditText: EditText
    private lateinit var medicineQuantityEditText: EditText
    private lateinit var medicineUnitSpinner: Spinner
    private lateinit var medicineTimeEditText: EditText
    private lateinit var medicineMealSpinner: Spinner
    private lateinit var medicineIntervalSpinner: Spinner
    private lateinit var medicineStartDateEditText: EditText
    private lateinit var medicineEndDateEditText: EditText
    private lateinit var addMedicineButton: Button

    private lateinit var medicineDBOperations: MedicineDBOperations
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine)

        // Initialize the UI components
        medicineNameEditText = findViewById(R.id.edit_text_medicine_name)
        medicineQuantityEditText = findViewById(R.id.edit_text_medicine_quantity)
        medicineUnitSpinner = findViewById(R.id.spinner_units)
        medicineTimeEditText = findViewById(R.id.edit_text_medicine_time)
        medicineMealSpinner = findViewById(R.id.spinner_meal)
        medicineIntervalSpinner = findViewById(R.id.spinner_interval)
        medicineStartDateEditText = findViewById(R.id.edit_text_medicine_from_date)
        medicineEndDateEditText = findViewById(R.id.edit_text_medicine_to_date)
        addMedicineButton = findViewById(R.id.button_add_medicine)

        // Initialize the database operations
        medicineDBOperations = MedicineDBOperations(this)
        medicineDBOperations.open()

        // Setup spinners with data
        setupSpinners()

        // Setup date pickers
        setupDatePickers()

        // Setup time picker
        setupTimePicker()

        // Set the add medicine button click listener
        addMedicineButton.setOnClickListener {
            addMedicineToDatabase()
        }
    }

    private fun setupSpinners() {
        // Populate the Unit Spinner
        val unitAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.units,
            android.R.layout.simple_spinner_item
        )
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        medicineUnitSpinner.adapter = unitAdapter

        // Populate the Meal Spinner
        val mealAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.meal,
            android.R.layout.simple_spinner_item
        )
        mealAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        medicineMealSpinner.adapter = mealAdapter

        // Populate the Interval Spinner
        val intervalAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.interval,
            android.R.layout.simple_spinner_item
        )
        intervalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        medicineIntervalSpinner.adapter = intervalAdapter
    }

    private fun setupDatePickers() {
        val today = Calendar.getInstance()

        // Setup Start Date picker
        medicineStartDateEditText.setOnClickListener {
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateLabel(medicineStartDateEditText)
            }

            val datePickerDialog = DatePickerDialog(
                this, dateSetListener,
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = today.timeInMillis  // Set minimum date to today
            datePickerDialog.show()
        }

        // Setup End Date picker
        medicineEndDateEditText.setOnClickListener {
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateLabel(medicineEndDateEditText)
            }

            val datePickerDialog = DatePickerDialog(
                this, dateSetListener,
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
            )

            // Set the minimum date to the selected start date or today, whichever is later
            val startDate = getStartDate()
            if (startDate != null) {
                datePickerDialog.datePicker.minDate = maxOf(today.timeInMillis, startDate.timeInMillis)
            } else {
                datePickerDialog.datePicker.minDate = today.timeInMillis
            }

            datePickerDialog.show()
        }
    }

    private fun getStartDate(): Calendar? {
        val dateStr = medicineStartDateEditText.text.toString().trim()
        if (dateStr.isEmpty()) return null

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = sdf.parse(dateStr) ?: return null

        return Calendar.getInstance().apply { time = date }
    }

    private fun updateDateLabel(editText: EditText) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        editText.setText(sdf.format(calendar.time))
    }

    private fun setupTimePicker() {
        medicineTimeEditText.setOnClickListener {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                medicineTimeEditText.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
            }, hour, minute, true).show()
        }
    }

    private fun addMedicineToDatabase() {
        // Retrieve the input data
        val name = medicineNameEditText.text.toString().trim()
        val quantity = medicineQuantityEditText.text.toString().trim()
        val unit = medicineUnitSpinner.selectedItem.toString()
        val time = medicineTimeEditText.text.toString().trim()
        val meal = medicineMealSpinner.selectedItem.toString()
        val interval = medicineIntervalSpinner.selectedItem.toString()
        val startDateStr = medicineStartDateEditText.text.toString().trim()
        val endDateStr = medicineEndDateEditText.text.toString().trim()

        if (name.isEmpty() || quantity.isEmpty() || time.isEmpty() || startDateStr.isEmpty() || endDateStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val startDate = sdf.parse(startDateStr)
        val endDate = sdf.parse(endDateStr)

        if (startDate == null || endDate == null) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
            return
        }

        if (endDate.before(startDate)) {
            Toast.makeText(this, "End Date cannot be before Start Date", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a new Medicine object
        val medicine = Medicine(
            MedicineName = name,
            MedicineQuantity = quantity,
            MedicineUnit = unit,
            MedicineTime = time,
            MedicineMeal = meal,
            MedicineInterval = interval,
            MedicineStartDate = startDateStr,
            MedicineEndDate = endDateStr
        )

        // Insert the medicine into the database
        medicineDBOperations.addMedicine(medicine)
        scheduleNotification(medicine)
        Toast.makeText(this, "Medicine added successfully", Toast.LENGTH_SHORT).show()

        // Clear the fields after adding
        clearFields()
    }

    private fun clearFields() {
        medicineNameEditText.text.clear()
        medicineQuantityEditText.text.clear()
        medicineTimeEditText.text.clear()
        medicineStartDateEditText.text.clear()
        medicineEndDateEditText.text.clear()
        medicineUnitSpinner.setSelection(0)
        medicineMealSpinner.setSelection(0)
        medicineIntervalSpinner.setSelection(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        medicineDBOperations.close() // Close the database connection when the activity is destroyed
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(medicine: Medicine) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MedicineReminderReceiver::class.java).apply {
            putExtra("medicine_name", medicine.MedicineName)
            putExtra("medicine_id", medicine.MedicineID)
            putExtra("medicine_unit", medicine.MedicineUnit)
            putExtra("medicine_quantity", medicine.MedicineQuantity)
            putExtra("medicine_end_date", medicine.MedicineEndDate)
            putExtra("medicine_time", medicine.MedicineTime)
            putExtra("medicine_interval", getIntervalMillis(medicine.MedicineInterval))
        }

        // Create a unique request code by combining medicineName and the time
        val requestCode = (medicine.MedicineName + medicine.MedicineTime.replace(":", "")).hashCode()

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Convert the medicine time to a calendar instance
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, getHourFromTimeString(medicine.MedicineTime))
            set(Calendar.MINUTE, getMinuteFromTimeString(medicine.MedicineTime))
            set(Calendar.SECOND, 0)
        }

        // Schedule the first alarm to trigger the notification
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    private fun getIntervalMillis(interval: String): Long {
        return when (interval) {
            "15min" -> 15 * 60 * 1000L
            "30min" -> 30 * 60 * 1000L
            "1hr" -> 60 * 60 * 1000L
            "1.5hr" -> 90 * 60 * 1000L
            "2hr" -> 2 * 60 * 60 * 1000L
            "3hr" -> 3 * 60 * 60 * 1000L
            "4hr" -> 4 * 60 * 60 * 1000L
            "6hr" -> 6 * 60 * 60 * 1000L
            "8hr" -> 8 * 60 * 60 * 1000L
            "12hr" -> 12 * 60 * 60 * 1000L
            "24hr" -> 24 * 60 * 60 * 1000L
            else -> 24 * 60 * 60 * 1000L // Default to 24 hours if the interval is unrecognized
        }
    }

    private fun getHourFromTimeString(time: String): Int {
        val parts = time.split(":")
        return parts[0].toInt()
    }

    private fun getMinuteFromTimeString(time: String): Int {
        val parts = time.split(":")
        return parts[1].toInt()
    }
}