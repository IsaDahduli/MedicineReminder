package com.isadahduli.medicinereminder

import android.app.ListActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.isadahduli.medicinereminder.Database.MedicineDBOperations
import com.isadahduli.medicinereminder.Model.Medicine

class ViewAllMedicinesActivity : ListActivity() {

    private lateinit var medicineOps: MedicineDBOperations  // Non-nullable property initialized later
    private var medicines: MutableList<Medicine> = mutableListOf()  // Initialize with an empty mutable list
    private lateinit var adapter: MedicineAdapter  // Non-nullable property initialized later

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_medicines)

        // Open the database
        medicineOps = MedicineDBOperations(this)
        medicineOps.open()

        // Fetch all medicines from the database
        medicines = medicineOps.getAllMedicines().toMutableList()

        // Log the number of medicines retrieved
        if (medicines.isEmpty()) {
            Log.d("ViewAllMedicinesActivity", "No medicines found in the database.")
        } else {
            Log.d("ViewAllMedicinesActivity", "Medicines fetched: ${medicines.size}")
        }

        // Initialize the adapter and set it to the list
        adapter = MedicineAdapter(this, medicines)
        listAdapter = adapter
    }

    fun removeMedicine(medicine: Medicine) {
        // Remove the selected medicine from the database and cancel its notifications
        medicineOps.deleteMedicine(medicine.MedicineID, this)

        // Remove the item from the list and notify the adapter
        medicines.remove(medicine)
        adapter.notifyDataSetChanged()

        Toast.makeText(this, "Medicine removed successfully!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Close the database connection
        medicineOps.close()
    }
}
