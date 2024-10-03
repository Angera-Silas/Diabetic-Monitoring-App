package com.example.diab

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class PatientListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var patientAdapter: PatientAdapter
    private val patientList = mutableListOf<Patient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_list)

        recyclerView = findViewById(R.id.recyclerViewPatients)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchPatients()
    }

    private fun fetchPatients() {
        val db = FirebaseFirestore.getInstance()

        db.collection("patients")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val patient = document.toObject(Patient::class.java)
                    patientList.add(patient)
                }
                patientAdapter = PatientAdapter(patientList)
                recyclerView.adapter = patientAdapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching patients: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
