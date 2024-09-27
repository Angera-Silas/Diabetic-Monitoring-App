package com.example.diab

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class GlucoseEntryActivity : AppCompatActivity() {

    private lateinit var etBloodSugar: EditText
    private lateinit var etMedication: EditText
    private lateinit var btnAddEntry: Button
    private lateinit var timeOfDay: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glucose_entry)

        // Initialize UI elements
        etBloodSugar = findViewById(R.id.etBloodSugar)
        etMedication = findViewById(R.id.etMedication)
        btnAddEntry = findViewById(R.id.btnAddEntry)

        // Get the selected time of day
        timeOfDay = intent.getStringExtra("TIME_OF_DAY") ?: "Morning"

        // Set the hint for medication input based on time of day
        when (timeOfDay) {
            "Morning" -> etMedication.hint = "Enter Morning Medication"
            "Afternoon" -> etMedication.hint = "Enter Afternoon Medication"
            "Evening" -> etMedication.hint = "Enter Evening Medication"
        }

        btnAddEntry.setOnClickListener {
            // Get the input values
            val bloodSugar = etBloodSugar.text.toString().toIntOrNull() ?: 0
            val medication = etMedication.text.toString()

            // Save the entry in SharedPreferences
            val sharedPref = getSharedPreferences("DiabetesLog", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                when (timeOfDay) {
                    "Morning" -> {
                        putInt("Morning_bloodSugar", bloodSugar)
                        putString("Morning_medication", medication)
                    }
                    "Afternoon" -> {
                        putInt("Afternoon_bloodSugar", bloodSugar)
                        putString("Afternoon_medication", medication)
                    }
                    "Evening" -> {
                        putInt("Evening_bloodSugar", bloodSugar)
                        putString("Evening_medication", medication)
                    }
                }
                apply() // Commit the changes
            }

            // Navigate to ReportsActivity
            val intent = Intent(this, ReportsActivity::class.java)
            startActivity(intent)
        }
    }
}
