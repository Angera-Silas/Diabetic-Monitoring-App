package com.example.diab

<<<<<<< HEAD
=======
import android.content.Context
>>>>>>> f62f972d3243ae3a06a84796ac99140333b80c9e
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
<<<<<<< HEAD
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
=======
import androidx.appcompat.app.AppCompatActivity
>>>>>>> f62f972d3243ae3a06a84796ac99140333b80c9e

class ReportsActivity : AppCompatActivity() {

    private lateinit var reportContainer: LinearLayout
<<<<<<< HEAD
    private lateinit var firestore: FirebaseFirestore
=======
>>>>>>> f62f972d3243ae3a06a84796ac99140333b80c9e

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        // Initialize the report container
        reportContainer = findViewById(R.id.reportContainer)

<<<<<<< HEAD
        // Initialize Firestore
        firestore = Firebase.firestore

        // Fetch data from Firestore
        fetchBloodSugarData()
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
                        "Morning" -> R.drawable.cup_of_tea // Replace with your morning icon
                        "Afternoon" -> R.drawable.plate // Replace with your afternoon icon
                        "Evening" -> R.drawable.supper // Replace with your evening icon
                        else -> R.drawable.cup_of_tea // Default icon for other times
                    }

                    // Add log entries to the report
                    addLogToReport(bloodSugar, medication, iconResId)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addLogToReport(bloodSugar: Int, medication: String, iconResId: Int) {
        // Inflate the log entry layout
=======
        // Retrieve data from SharedPreferences
        val sharedPref = getSharedPreferences("DiabetesLog", Context.MODE_PRIVATE)
        val morningBloodSugar = sharedPref.getInt("Morning_bloodSugar", -1)
        val morningMedication = sharedPref.getString("Morning_medication", "")
        val afternoonBloodSugar = sharedPref.getInt("Afternoon_bloodSugar", -1)
        val afternoonMedication = sharedPref.getString("Afternoon_medication", "")
        val eveningBloodSugar = sharedPref.getInt("Evening_bloodSugar", -1)
        val eveningMedication = sharedPref.getString("Evening_medication", "")

        // Add log entries to the report
        if (morningBloodSugar != -1) {
            addLogToReport(morningBloodSugar, morningMedication ?: "", R.drawable.cup_of_tea) // Replace with your morning icon
        }
        if (afternoonBloodSugar != -1) {
            addLogToReport(afternoonBloodSugar, afternoonMedication ?: "", R.drawable.plate) // Replace with your afternoon icon
        }
        if (eveningBloodSugar != -1) {
            addLogToReport(eveningBloodSugar, eveningMedication ?: "", R.drawable.supper) // Replace with your evening icon
        }
    }

    private fun addLogToReport(bloodSugar: Int, medication: String, iconResId: Int) {
>>>>>>> f62f972d3243ae3a06a84796ac99140333b80c9e
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
