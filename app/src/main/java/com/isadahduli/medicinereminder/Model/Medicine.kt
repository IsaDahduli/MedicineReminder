package com.isadahduli.medicinereminder.Model

data class Medicine(
    var MedicineID: Long = 0,
    var MedicineName: String = "",
    var MedicineQuantity: String = "",
    var MedicineUnit: String = "",
    var MedicineTime: String = "",
    var MedicineMeal: String = "",
    var MedicineInterval: String = "",
    var MedicineStartDate: String = "",
    var MedicineEndDate: String = ""
) {

    override fun toString(): String {
        return "Medicine ID: $MedicineID\n" +
                "Medicine Name: $MedicineName\n" +
                "Medicine Quantity: $MedicineQuantity $MedicineUnit\n" +
                "Medicine Time: $MedicineTime\n" +
                "Medicine Meal: $MedicineMeal\n" +
                "Medicine Interval: $MedicineInterval\n" +
                "Medicine Start Date: $MedicineStartDate\n" +
                "Medicine End Date: $MedicineEndDate"
    }
}
