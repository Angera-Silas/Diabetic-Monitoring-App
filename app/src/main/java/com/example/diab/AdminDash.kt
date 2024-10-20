package com.example.diab

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AdminDash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dash)

        // Initialize buttons
        val doctorListButton: Button = findViewById(R.id.button_doctor_list)
        val patientListButton: Button = findViewById(R.id.button_patient_list)
        val appointmentListButton: Button = findViewById(R.id.button_appointment_list)

        // Set up button click listeners
        doctorListButton.setOnClickListener {
            startActivity(Intent(this, DoctorListActivity::class.java))
        }

        patientListButton.setOnClickListener {
            startActivity(Intent(this, ViewPatientActivity::class.java))
        }

        appointmentListButton.setOnClickListener {
            startActivity(Intent(this, AppointmentListActivity::class.java))
        }
    }
}
