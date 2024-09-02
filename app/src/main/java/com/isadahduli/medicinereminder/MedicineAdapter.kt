package com.isadahduli.medicinereminder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.isadahduli.medicinereminder.Model.Medicine

class MedicineAdapter(context: Context, medicines: List<Medicine>) :
    ArrayAdapter<Medicine>(context, 0, medicines) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get the data item for this position
        val medicine = getItem(position)!!

        // Check if an existing view is being reused, otherwise inflate the view
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.the_scheduled_medicine, parent, false)

        // Lookup view for data population
        val medicineType = view.findViewById<TextView>(R.id.medicine_type)
        val medicineName = view.findViewById<TextView>(R.id.medicine_name)
        val medicineQuantity = view.findViewById<TextView>(R.id.medicine_quantity)
        val medicineSchedule = view.findViewById<TextView>(R.id.medicine_schedule)
        val removeButton = view.findViewById<Button>(R.id.remove_medicine)

        // Populate the data into the template view using the data object
        medicineName.text = medicine.MedicineName
        medicineQuantity.text = "Quantity: ${medicine.MedicineQuantity}"
        medicineType.text = "Type: ${medicine.MedicineUnit}"
        medicineSchedule.text = "On ${medicine.MedicineTime} every ${medicine.MedicineInterval} from ${medicine.MedicineStartDate} to ${medicine.MedicineEndDate}"

        // Handle the remove button click
        removeButton.setOnClickListener {
            (context as? ViewAllMedicinesActivity)?.removeMedicine(medicine)
        }

        // Return the completed view to render on screen
        return view
    }
}
