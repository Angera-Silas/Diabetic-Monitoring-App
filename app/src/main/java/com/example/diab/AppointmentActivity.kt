package com.example.diab


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AppointmentActivity : AppCompatActivity() {

    private lateinit var spinnerSpeciality: Spinner
    private lateinit var spinnerReason: Spinner
    private lateinit var editTextHistory: EditText
    private lateinit var textViewDateTime: TextView
    private lateinit var buttonSetDateTime: ImageButton
    private lateinit var buttonSubmit: Button

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment) // Change to your layout name

        // Initialize UI components
        spinnerSpeciality = findViewById(R.id.spinnerSpeciality)
        spinnerReason = findViewById(R.id.spinnerReason)
        editTextHistory = findViewById(R.id.editTextHistory)
        textViewDateTime = findViewById(R.id.textViewDateTime)
        buttonSetDateTime = findViewById(R.id.buttonSetDateTime)
        buttonSubmit = findViewById(R.id.buttonSubmit)

        // Populate spinnerSpeciality and spinnerReason with data here
        populateSpinners()

        // Set click listener for the date/time button
        buttonSetDateTime.setOnClickListener {
            showDateTimePicker()
        }

        // Set click listener for the submit button
        buttonSubmit.setOnClickListener {
            bookAppointment()
        }
    }

    private fun populateSpinners() {
        // Populate your spinner data
        val specialities = arrayOf("Cardiology", "Dermatology", "Pediatrics") // Example data
        val reasons = arrayOf("Routine Checkup", "Follow-up", "New Consultation") // Example data

        val specialityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, specialities)
        specialityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSpeciality.adapter = specialityAdapter

        val reasonAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, reasons)
        reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerReason.adapter = reasonAdapter
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()

        // Show Date Picker
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Show Time Picker after selecting the date
                val timePickerDialog = TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        // Update the TextView with selected date and time
                        textViewDateTime.text = String.format("%02d/%02d/%d %02d:%02d", dayOfMonth, month + 1, year, hourOfDay, minute)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun bookAppointment() {
        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val speciality = spinnerSpeciality.selectedItem.toString()
        val reason = spinnerReason.selectedItem.toString()
        val history = editTextHistory.text.toString()
        val dateTime = textViewDateTime.text.toString()

        val appointmentData = hashMapOf(
            "userId" to userId,
            "speciality" to speciality,
            "reason" to reason,
            "history" to history,
            "dateTime" to dateTime
        )

        // Send appointment data to Firestore
        firestore.collection("appointments")
            .add(appointmentData)
            .addOnSuccessListener {
                Toast.makeText(this, "Appointment booked successfully", Toast.LENGTH_SHORT).show()
                finish() // Optionally close the activity
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error booking appointment: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
