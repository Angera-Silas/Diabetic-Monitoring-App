package com.example.diab

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ReportsActivity : AppCompatActivity() {

    private lateinit var reportContainer: LinearLayout
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        // Initialize the report container
        reportContainer = findViewById(R.id.reportContainer)

        // Initialize Firestore and FirebaseAuth
        firestore = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        // Fetch data from Firestore
        fetchBloodSugarData()
    }

    private fun fetchBloodSugarData() {
        // Retrieve the user ID from the intent or the current user
        val userId = intent.getStringExtra("patientId") ?: auth.currentUser?.uid // Use the patient ID if available, otherwise use current user ID

        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch blood sugar data specific to the logged-in user
        firestore.collection("glucose_entries")
            .whereEqualTo("userId", userId) // Filter by userId
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val bloodSugar = document.getLong("bloodSugar")?.toInt() ?: -1
                        val medication = document.getString("medication") ?: ""
                        val timeOfDay = document.getString("timeOfDay") ?: ""

                        // Decide the icon based on time of day
                        val iconResId = when (timeOfDay) {
                            "Morning" -> R.drawable.cup_of_tea
                            "Afternoon" -> R.drawable.plate
                            "Evening" -> R.drawable.supper
                            else -> R.drawable.cup_of_tea
                        }

                        // Add log entries to the report
                        addLogToReport(bloodSugar, medication, iconResId)
                    }
                } else {
                    Toast.makeText(this, "No glucose entries found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addLogToReport(bloodSugar: Int, medication: String, iconResId: Int) {
        val view = layoutInflater.inflate(R.layout.item_report, reportContainer, false)

        val tvBloodSugar: TextView = view.findViewById(R.id.tvBloodSugar)
        val tvMedication: TextView = view.findViewById(R.id.tvMedication)
        val ivTimeIcon: ImageView = view.findViewById(R.id.ivTimeIcon)

        tvBloodSugar.text = "Blood Sugar: $bloodSugar mg/dL"
        tvMedication.text = "Medication: $medication"
        ivTimeIcon.setImageResource(iconResId)

        reportContainer.addView(view)
    }
}
