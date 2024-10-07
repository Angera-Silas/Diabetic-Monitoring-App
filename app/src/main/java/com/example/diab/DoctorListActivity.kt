package com.example.diab

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DoctorListActivity : AppCompatActivity(), DoctorAdapter.OnDoctorClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DoctorAdapter
    private lateinit var doctorList: MutableList<Doctor>
    private lateinit var firestore: FirebaseFirestore
    private var currentPatientId: String? = null // Store current patient's ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_list)

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize Firebase Auth
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Use current user's UID as the patient ID (assuming patient is the logged-in user)
            currentPatientId = currentUser.uid
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewDoctors)
        recyclerView.layoutManager = LinearLayoutManager(this)
        doctorList = mutableListOf()
        adapter = DoctorAdapter(doctorList, this) // Pass this as OnDoctorClickListener
        recyclerView.adapter = adapter

        // Fetch doctors from Firestore
        fetchDoctors()
    }

    private fun fetchDoctors() {
        firestore.collection("users")
            .whereEqualTo("userType", "Doctor")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val doctor = document.toObject(Doctor::class.java)
                    doctorList.add(doctor)
                }
                adapter.notifyDataSetChanged() // Notify the adapter to update the UI
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error loading doctors: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Implement the OnDoctorClickListener interface
    override fun onDoctorClick(doctor: Doctor) {
        // Make sure patient ID is available
        currentPatientId?.let { patientId ->
            // Send patientId (sender) and doctor.id (receiver) to ChatActivity
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("senderId", patientId)   // Pass patient's ID as senderId
                putExtra("receiverId", doctor.id) // Pass doctor's ID as receiverId
            }
            startActivity(intent)
        } ?: run {
            Toast.makeText(this, "Patient ID not initialized", Toast.LENGTH_SHORT).show()
        }
    }
}
