package com.example.diab

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast // Import Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class Appointment : AppCompatActivity() {

    private lateinit var textViewDateTime: TextView
    private lateinit var buttonSetDateTime: ImageButton
    private lateinit var editTextHistory: EditText
    private lateinit var spinnerSpeciality: Spinner
    private lateinit var spinnerReason: Spinner
    private lateinit var buttonSubmit: Button
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AppointmentActivity", "onCreate called")
        setContentView(R.layout.activity_appointment)

        textViewDateTime = findViewById(R.id.textViewDateTime)
        buttonSetDateTime = findViewById(R.id.buttonSetDateTime)
        editTextHistory = findViewById(R.id.editTextHistory)
        spinnerSpeciality = findViewById(R.id.spinnerSpeciality)
        spinnerReason = findViewById(R.id.spinnerReason)
        buttonSubmit = findViewById(R.id.buttonSubmit)

        // Setting up Spinner for Doctor's Speciality
        val specialties = listOf("Nutritionist", "Diabetologist", "Endocrinologist", "General Practitioner")
        val specialtyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, specialties)
        specialtyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSpeciality.adapter = specialtyAdapter

        // Setting up Spinner for Appointment Reasons
        val reasons = listOf("Regular Check-up", "Follow-up", "Consultation", "Emergency")
        val reasonAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, reasons)
        reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerReason.adapter = reasonAdapter

        // Open Date Picker when the user clicks the button or TextView
        buttonSetDateTime.setOnClickListener { showDatePickerDialog() }
        textViewDateTime.setOnClickListener { showDatePickerDialog() }

        // Submit button click listener
        buttonSubmit.setOnClickListener {
            val selectedSpecialty = spinnerSpeciality.selectedItem.toString()
            val selectedReason = spinnerReason.selectedItem.toString()
            val patientHistory = editTextHistory.text.toString()
            val selectedDateTime = textViewDateTime.text.toString()

            // Handle the appointment booking logic here
            Log.d("AppointmentActivity", "Specialty: $selectedSpecialty, Reason: $selectedReason, History: $patientHistory, DateTime: $selectedDateTime")

            // Show a Toast message indicating the appointment has been booked
            Toast.makeText(this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    // Method to show the DatePickerDialog
    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                showTimePickerDialog() // After selecting the date, show the time picker
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    // Method to show the TimePickerDialog
    private fun showTimePickerDialog() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                updateDateTimeTextView() // Update the TextView with the selected date and time
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    // Method to update the TextView with the selected date and time
    private fun updateDateTimeTextView() {
        val dateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault())
        val dateTime = dateFormat.format(calendar.time)
        textViewDateTime.text = dateTime // Display the selected date and time
    }

}
