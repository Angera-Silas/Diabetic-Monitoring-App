package com.example.diab

import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReportActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_reports)

        // Initialize Firebase instances
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Reference to the date TextView
        val reportDate = findViewById<TextView>(R.id.reportDate)

        // Reference to the TableLayout
        val reportTable = findViewById<TableLayout>(R.id.reportTable)

        // Get current user's ID
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Fetch data from Firestore for the logged-in user
            firestore.collection("users")
                .document(userId)
                .collection("logs")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val name = document.getString("name")
                        val email = document.getString("email")
                        val bloodGlucose = document.getString("bloodGlucose")
                        val medication = document.getString("medication")
                        val date = document.getString("date")

                        // Set the date at the top (only once if needed)
                        reportDate.text = "Date: $date"

                        // Create a new TableRow for each log entry
                        val tableRow = TableRow(this)

                        // Name
                        val nameTextView = TextView(this)
                        nameTextView.text = name
                        nameTextView.setPadding(8, 8, 8, 8)
                        nameTextView.setBackgroundResource(R.drawable.table_cell_border)

                        // Email
                        val emailTextView = TextView(this)
                        emailTextView.text = email
                        emailTextView.setPadding(8, 8, 8, 8)
                        emailTextView.setBackgroundResource(R.drawable.table_cell_border)

                        // Blood Glucose
                        val glucoseTextView = TextView(this)
                        glucoseTextView.text = bloodGlucose
                        glucoseTextView.setPadding(8, 8, 8, 8)
                        glucoseTextView.setBackgroundResource(R.drawable.table_cell_border)

                        // Medication
                        val medicationTextView = TextView(this)
                        medicationTextView.text = medication
                        medicationTextView.setPadding(8, 8, 8, 8)
                        medicationTextView.setBackgroundResource(R.drawable.table_cell_border)

                        // Add the TextViews to the TableRow
                        tableRow.addView(nameTextView)
                        tableRow.addView(emailTextView)
                        tableRow.addView(glucoseTextView)
                        tableRow.addView(medicationTextView)

                        // Add the row to the TableLayout
                        reportTable.addView(tableRow)
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                    exception.printStackTrace()
                }
        }
    }
}
