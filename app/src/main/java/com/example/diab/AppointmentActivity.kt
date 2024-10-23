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

    private lateinit var spinnerDoctor: Spinner
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
        spinnerDoctor = findViewById(R.id.spinnerSpeciality)
        spinnerReason = findViewById(R.id.spinnerReason)
        editTextHistory = findViewById(R.id.editTextHistory)
        textViewDateTime = findViewById(R.id.textViewDateTime)
        buttonSetDateTime = findViewById(R.id.buttonSetDateTime)
        buttonSubmit = findViewById(R.id.buttonSubmit)

        // Populate the doctor spinner and reason spinner
        populateDoctorSpinner()

        // Set click listener for the date/time button
        buttonSetDateTime.setOnClickListener {
            showDateTimePicker()
        }

        // Set click listener for the submit button
        buttonSubmit.setOnClickListener {
            bookAppointment()
        }
    }

    private fun populateDoctorSpinner() {
        // Fetch doctors from Firestore based on userType being "Doctor"
        firestore.collection("users")
            .whereEqualTo("userType", "Doctor")  // Query only documents where userType is "Doctor"
            .get()
            .addOnSuccessListener { documents ->
                val doctorNames = mutableListOf<String>()

                // Iterate through documents and collect doctor names
                for (document in documents) {
                    val doctorName = document.getString("name") ?: "Unknown Doctor"
                    val doctorEmail = document.getString("email") ?: "Unknown Email"

                    doctorNames.add("$doctorName ($doctorEmail)")  // Format name and email
                }

                // If no doctors are found, add a default message
                if (doctorNames.isEmpty()) {
                    doctorNames.add("No doctors available")
                }

                // Populate spinner with doctor names
                val doctorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, doctorNames)
                doctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerDoctor.adapter = doctorAdapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching doctors: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        // Populate reason for appointment as static data
        val reasons = arrayOf("Routine Checkup", "Follow-up", "New Consultation") // Example data
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

        val doctorInfo = spinnerDoctor.selectedItem.toString()
        val reason = spinnerReason.selectedItem.toString()
        val history = editTextHistory.text.toString()
        val dateTime = textViewDateTime.text.toString()

        // Extract doctor's name and email from the selected item
        val doctorName = doctorInfo.substringBefore(" (")
        val doctorEmail = doctorInfo.substringAfter("(").substringBefore(")")

        val appointmentData = hashMapOf(
            "userId" to userId,
            "doctorName" to doctorName,
            "doctorEmail" to doctorEmail,
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
