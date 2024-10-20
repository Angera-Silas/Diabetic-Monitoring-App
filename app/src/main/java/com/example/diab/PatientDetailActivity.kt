package com.example.diab

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue

class PatientDetailActivity : AppCompatActivity() {

    private lateinit var patientId: String
    private lateinit var editTextComment: EditText
    private lateinit var buttonSaveComment: Button
    private lateinit var spinnerRiskLevel: Spinner
    private val db = FirebaseFirestore.getInstance() // Firestore instance
    private val mAuth = FirebaseAuth.getInstance() // Firebase Auth instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_detail)

        // Get the patient ID from the intent
        patientId = intent.getStringExtra("patientId") ?: ""

        // Initialize UI components
        val foodChartButton: Button = findViewById(R.id.buttonFoodChart)
        val medicationChartButton: Button = findViewById(R.id.buttonMedicationChart)
        editTextComment = findViewById(R.id.editTextComment)
        buttonSaveComment = findViewById(R.id.buttonSaveComment)
        spinnerRiskLevel = findViewById(R.id.spinnerRiskLevel)

        // Set up the Spinner with risk levels
        val riskLevels = arrayOf("Normal", "Risky", "Dangerous")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, riskLevels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRiskLevel.adapter = adapter

        // Set up button to open GlucoseChartFoodActivity
        foodChartButton.setOnClickListener {
            val intent = Intent(this, GlucoseChartFoodActivity::class.java).apply {
                putExtra("patientId", patientId)
            }
            startActivity(intent)
        }

        // Set up button to open GlucoseChartMedicationActivity
        medicationChartButton.setOnClickListener {
            val intent = Intent(this, GlucoseChartMedicationActivity::class.java).apply {
                putExtra("patientId", patientId)
            }
            startActivity(intent)
        }

        // Set up button to save comment
        buttonSaveComment.setOnClickListener {
            val commentText = editTextComment.text.toString().trim()
            val selectedLevel = spinnerRiskLevel.selectedItem.toString() // Get selected risk level

            if (commentText.isNotEmpty()) {
                saveCommentToFirestore(commentText, selectedLevel) // Pass the selected level
            } else {
                Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveCommentToFirestore(comment: String, riskLevel: String) {
        val userId = mAuth.currentUser?.uid // Get the current user ID
        if (userId != null) {
            val commentData = hashMapOf(
                "userId" to userId, // Add the userId to the comment data
                "patientId" to patientId,
                "comment" to comment,
                "riskLevel" to riskLevel, // Add risk level to the comment data
                "timestamp" to FieldValue.serverTimestamp() // Use server timestamp
            )

            db.collection("doctor_comments")
                .add(commentData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Comment saved successfully", Toast.LENGTH_SHORT).show()
                    editTextComment.text.clear() // Clear the input field after saving
                    spinnerRiskLevel.setSelection(0) // Reset spinner to default
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving comment: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
