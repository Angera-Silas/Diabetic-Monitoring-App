package com.example.diab

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ReportsActivity : AppCompatActivity() {

    private lateinit var reportContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        // Initialize the report container
        reportContainer = findViewById(R.id.reportContainer)

        // Retrieve data from SharedPreferences
        val sharedPref = getSharedPreferences("DiabetesLog", Context.MODE_PRIVATE)
        val morningBloodSugar = sharedPref.getInt("Morning_bloodSugar", -1)
        val morningMedication = sharedPref.getString("Morning_medication", "")
        val afternoonBloodSugar = sharedPref.getInt("Afternoon_bloodSugar", -1)
        val afternoonMedication = sharedPref.getString("Afternoon_medication", "")
        val eveningBloodSugar = sharedPref.getInt("Evening_bloodSugar", -1)
        val eveningMedication = sharedPref.getString("Evening_medication", "")

        // Add log entries to the report
        if (morningBloodSugar != -1) {
            addLogToReport(morningBloodSugar, morningMedication ?: "", R.drawable.cup_of_tea) // Replace with your morning icon
        }
        if (afternoonBloodSugar != -1) {
            addLogToReport(afternoonBloodSugar, afternoonMedication ?: "", R.drawable.plate) // Replace with your afternoon icon
        }
        if (eveningBloodSugar != -1) {
            addLogToReport(eveningBloodSugar, eveningMedication ?: "", R.drawable.supper) // Replace with your evening icon
        }
    }

    private fun addLogToReport(bloodSugar: Int, medication: String, iconResId: Int) {
        val view = layoutInflater.inflate(R.layout.item_report, reportContainer, false)

        val tvBloodSugar: TextView = view.findViewById(R.id.tvBloodSugar)
        val tvMedication: TextView = view.findViewById(R.id.tvMedication)
        val ivTimeIcon: ImageView = view.findViewById(R.id.ivTimeIcon)

        tvBloodSugar.text = "Blood Sugar: $bloodSugar mg/dL"
        tvMedication.text = "Medication: $medication"
        ivTimeIcon.setImageResource(iconResId)

        reportContainer.addView(view)
    }
}
