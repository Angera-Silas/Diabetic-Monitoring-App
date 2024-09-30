
package com.example.diab

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class PRecords : AppCompatActivity() {

    private lateinit var rvUserRecords: RecyclerView
    private val userRecords = mutableListOf<LogEntry>()
    private lateinit var userRecordsAdapter: LogbookAdapter

    // Firestore instance
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_precords)

        // Setup edge-to-edge UI handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize RecyclerView
        rvUserRecords = findViewById(R.id.rvUserRecords)
        userRecordsAdapter = LogbookAdapter(userRecords)
        rvUserRecords.layoutManager = LinearLayoutManager(this)
        rvUserRecords.adapter = userRecordsAdapter

        // Fetch user records from Firestore
        fetchUserRecords()
    }

    // Function to retrieve user records from Firestore
    private fun fetchUserRecords() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid // Get the logged-in user's ID
        if (userId != null) {
            db.collection("logEntries")
                .whereEqualTo("userId", userId) // Query to get records for the logged-in user
                .get()
                .addOnSuccessListener { result: QuerySnapshot ->
                    for (document in result) {
                        // Map Firestore document to LogEntry object
                        val entry = document.toObject(LogEntry::class.java)
                        userRecords.add(entry)
                    }
                    userRecordsAdapter.notifyDataSetChanged() // Update RecyclerView with data
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error getting user records: ", exception)
                }
        }
    }
}
