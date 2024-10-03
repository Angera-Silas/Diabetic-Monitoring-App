package com.example.diab

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class Records : AppCompatActivity() {

    private lateinit var rvLogbook: RecyclerView
    private val logEntries = mutableListOf<LogEntry>()
    private lateinit var logbookAdapter: LogbookAdapter

    // Firestore instance
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_records)

        // Setup edge-to-edge UI handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize RecyclerView
        rvLogbook = findViewById(R.id.rvLogbook)
        logbookAdapter = LogbookAdapter(logEntries)
        rvLogbook.layoutManager = LinearLayoutManager(this)
        rvLogbook.adapter = logbookAdapter

        // Fetch data from Firestore
        fetchLogEntries()
    }

    // Function to retrieve log entries from Firestore
    private fun fetchLogEntries() {
        db.collection("logEntries")
            .get()
            .addOnSuccessListener { result: QuerySnapshot ->
                for (document in result) {
                    // Map Firestore document to LogEntry object
                    val entry = document.toObject(LogEntry::class.java)
                    logEntries.add(entry)
                }
                logbookAdapter.notifyDataSetChanged() // Update RecyclerView with data
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting documents: ", exception)
            }
    }
}
