package com.example.diab

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ViewPatientActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ViewPatientAdapter // Use ViewPatientAdapter
    private val patientList = mutableListOf<Patient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_patient)



        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPatients) // Ensure you have this RecyclerView in your layout
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ViewPatientAdapter(patientList) // Use ViewPatientAdapter
        recyclerView.adapter = adapter

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Load patients
        loadPatients()
    }

    private fun loadPatients() {
        db.collection("users")
            .whereEqualTo("userType", "Patient")
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                patientList.clear() // Clear existing patients
                for (document in queryDocumentSnapshots.documents) {
                    val patient = document.toObject(Patient::class.java)
                    patient?.let { patientList.add(it) } // Add patient to the list
                }
                adapter.notifyDataSetChanged() // Notify adapter of data changes
                if (patientList.isEmpty()) {
                    Toast.makeText(this, "No patients found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load patients: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
