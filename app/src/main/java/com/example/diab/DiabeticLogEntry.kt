package com.example.diab

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DiabeticLogEntry : AppCompatActivity() {

    private lateinit var etBloodSugar: EditText
    private lateinit var etMorningMedication: EditText
    private lateinit var etAfternoonMedication: EditText
    private lateinit var etEveningMedication: EditText
    private lateinit var btnAddEntry: Button
    private lateinit var rvLogbook: RecyclerView
    private lateinit var btnGoToRecords: Button

    private val logEntries = mutableListOf<LogEntry>()
    private lateinit var logbookAdapter: LogbookAdapter

    // Initialize Firestore instance
    private val db = FirebaseFirestore.getInstance()

    // Get the current user ID
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // Initialize UI elements
        etBloodSugar = findViewById(R.id.etBloodSugar)
        etMorningMedication = findViewById(R.id.etMorningMedication)
        etAfternoonMedication = findViewById(R.id.etAfternoonMedication)
        etEveningMedication = findViewById(R.id.etEveningMedication)
        btnAddEntry = findViewById(R.id.btnAddEntry)
        rvLogbook = findViewById(R.id.rvLogbook)
        btnGoToRecords = findViewById(R.id.btnGoToRecords)

        // Initialize the RecyclerView and Adapter
        logbookAdapter = LogbookAdapter(logEntries)
        rvLogbook.layoutManager = LinearLayoutManager(this)
        rvLogbook.adapter = logbookAdapter

        // Add entry on button click
        btnAddEntry.setOnClickListener {
            val bloodSugar = etBloodSugar.text.toString().toIntOrNull() ?: 0
            val morningMed = etMorningMedication.text.toString().toIntOrNull() ?: 0
            val afternoonMed = etAfternoonMedication.text.toString().toIntOrNull() ?: 0
            val eveningMed = etEveningMedication.text.toString().toIntOrNull() ?: 0

            // Add new log entry with userId
            val newEntry = LogEntry(
                userId = userId, // Include userId here
                bloodSugar = bloodSugar,
                morningMedication = morningMed,
                afternoonMedication = afternoonMed,
                eveningMedication = eveningMed
            )
            logEntries.add(newEntry)
            logbookAdapter.notifyDataSetChanged()  // Update the RecyclerView

            // Send the new entry to Firestore
            saveToFirestore(newEntry)
        }

        // Navigate to Records activity on button click
        btnGoToRecords.setOnClickListener {
            val intent = Intent(this, PRecords::class.java)
            startActivity(intent)
        }
    }

    // Function to save log entry to Firestore
    private fun saveToFirestore(entry: LogEntry) {
        db.collection("logEntries")
            .add(entry)
            .addOnSuccessListener {
                // Success message
                println("Entry successfully added to Firestore!")
            }
            .addOnFailureListener { e ->
                // Error handling
                println("Error adding entry: $e")
            }
    }
}
