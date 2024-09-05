package com.isadahduli.medicinereminder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.isadahduli.medicinereminder.Model.Medicine
import com.isadahduli.medicinereminder.Database.MedicineDBOperations

class MedicineAdapter(context: Context, private val medicines: MutableList<Medicine>) :
    ArrayAdapter<Medicine>(context, 0, medicines) {

    private val medicineDBOperations = MedicineDBOperations(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get the data item for this position
        val medicine = getItem(position)

        // Check if an existing view is being reused, otherwise inflate the view
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.the_scheduled_medicine, parent, false)

        // Ensure medicine is not null before proceeding
        medicine?.let {
            // Lookup view for data population
            val medicineType = view.findViewById<TextView>(R.id.medicine_type)
            val medicineName = view.findViewById<TextView>(R.id.medicine_name)
            val medicineQuantity = view.findViewById<TextView>(R.id.medicine_quantity)
            val medicineSchedule = view.findViewById<TextView>(R.id.medicine_schedule)
            val removeButton = view.findViewById<Button>(R.id.remove_medicine)

            // Populate the data into the template view using the data object
            medicineName.text = it.MedicineName
            medicineQuantity.text = "Quantity: ${it.MedicineQuantity}"
            medicineType.text = "Type: ${it.MedicineUnit}"
            medicineSchedule.text = "On ${it.MedicineTime} every ${it.MedicineInterval} from ${it.MedicineStartDate} to ${it.MedicineEndDate}"

            // Handle the remove button click
            removeButton.setOnClickListener {
                // Ensure the medicine is removed from the database and associated notifications are canceled
                medicineDBOperations.open()
                medicineDBOperations.deleteMedicine(medicine.MedicineID, context)
                medicineDBOperations.close()


                // Remove the item from the list and notify the adapter
                medicines.removeAt(position)
                notifyDataSetChanged()
            }
        }

        // Return the completed view to render on screen
        return view
    }
}
