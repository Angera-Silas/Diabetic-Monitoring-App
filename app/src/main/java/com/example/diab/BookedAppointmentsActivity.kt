package com.example.diab // Change to your package name

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BookedAppointmentsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var appointmentsAdapter: AppointmentsAdapter
    private lateinit var appointmentsList: MutableList<Appointment>

    private val firestore: FirebaseFirestore by lazy { Firebase.firestore }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booked_appointments) // Change to your layout name

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewAppointments)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize appointments list and adapter
        appointmentsList = mutableListOf()
        appointmentsAdapter = AppointmentsAdapter(appointmentsList)
        recyclerView.adapter = appointmentsAdapter

        // Fetch appointments
        fetchAppointments()
    }

    private fun fetchAppointments() {
        // Get the patientId from the intent
        val patientId = intent.getStringExtra("patientId")

        // Use the patient ID if available, otherwise use current user ID
        val userId = patientId ?: auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch appointments based on userId
        firestore.collection("appointments")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    appointmentsList.clear()
                    for (document in documents) {
                        val appointment = document.toObject(Appointment::class.java)
                        appointmentsList.add(appointment)
                    }
                    appointmentsAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "No appointments found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching appointments: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
