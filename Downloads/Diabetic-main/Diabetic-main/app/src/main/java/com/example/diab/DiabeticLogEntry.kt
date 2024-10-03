package com.example.diab// Replace with your actual package name

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class DiabeticLogEntry : AppCompatActivity() {

    private lateinit var spinnerTimeOfDay: Spinner
    private lateinit var btnProceed: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diabetic_log_entry)

        // Initialize UI elements
        spinnerTimeOfDay = findViewById(R.id.spinnerTimeOfDay)
        btnProceed = findViewById(R.id.btnProceed)

        // Set up the spinner with options
        val timeOfDayOptions = arrayOf("Morning", "Afternoon", "Evening")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeOfDayOptions)
        spinnerTimeOfDay.adapter = adapter

        // Set listener for Proceed button
        btnProceed.setOnClickListener {
            val selectedTimeOfDay = spinnerTimeOfDay.selectedItem.toString()

            // Navigate to the glucose entry page with the selected time
            val intent = Intent(this, GlucoseEntryActivity::class.java)
            intent.putExtra("TIME_OF_DAY", selectedTimeOfDay)
            startActivity(intent)
        }
    }
}
