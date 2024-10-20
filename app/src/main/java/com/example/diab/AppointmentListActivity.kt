package com.example.diab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentListActivity : AppCompatActivity() {

    private lateinit var appointmentsRecyclerView: RecyclerView
    private lateinit var appointmentsAdapter: AppointmentsAdapter
    private val appointmentsList = mutableListOf<Appointment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_list) // Ensure this layout exists

        // Initialize RecyclerView
        appointmentsRecyclerView = findViewById(R.id.recyclerViewAppointments) // Make sure this ID matches your layout
        appointmentsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch appointments
        fetchAppointments()
    }

    private fun fetchAppointments() {
        val db = FirebaseFirestore.getInstance()
        db.collection("appointments") // Replace "appointments" with your Firestore collection name
            .get()
            .addOnSuccessListener { documents ->
                appointmentsList.clear()
                for (document in documents) {
                    val appointment = document.toObject(Appointment::class.java)
                    appointmentsList.add(appointment)
                }
                // Update the RecyclerView
                appointmentsAdapter = AppointmentsAdapter(appointmentsList)
                appointmentsRecyclerView.adapter = appointmentsAdapter
            }
            .addOnFailureListener { exception ->
                // Handle the error here
                exception.printStackTrace()
            }
    }
}
