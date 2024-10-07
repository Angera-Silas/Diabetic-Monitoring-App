package com.example.diab

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ReportsActivity : AppCompatActivity() {

    private lateinit var reportContainer: LinearLayout
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        // Initialize the report container
        reportContainer = findViewById(R.id.reportContainer)

        // Initialize Firestore
        firestore = Firebase.firestore

        // Fetch data from Firestore
        fetchBloodSugarData()

        // Retrieve data from SharedPreferences
        retrieveSharedPreferencesData()
    }

    private fun fetchBloodSugarData() {
        firestore.collection("glucose_entries")
            .get()
            .addOnSuccessListener { documents ->
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
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun retrieveSharedPreferencesData() {
        val sharedPref = getSharedPreferences("DiabetesLog", Context.MODE_PRIVATE)
        val morningBloodSugar = sharedPref.getInt("Morning_bloodSugar", -1)
        val morningMedication = sharedPref.getString("Morning_medication", "")
        val afternoonBloodSugar = sharedPref.getInt("Afternoon_bloodSugar", -1)
        val afternoonMedication = sharedPref.getString("Afternoon_medication", "")
        val eveningBloodSugar = sharedPref.getInt("Evening_bloodSugar", -1)
        val eveningMedication = sharedPref.getString("Evening_medication", "")

        // Add log entries to the report from SharedPreferences
        if (morningBloodSugar != -1) {
            addLogToReport(morningBloodSugar, morningMedication ?: "", R.drawable.cup_of_tea)
        }
        if (afternoonBloodSugar != -1) {
            addLogToReport(afternoonBloodSugar, afternoonMedication ?: "", R.drawable.plate)
        }
        if (eveningBloodSugar != -1) {
            addLogToReport(eveningBloodSugar, eveningMedication ?: "", R.drawable.supper)
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
