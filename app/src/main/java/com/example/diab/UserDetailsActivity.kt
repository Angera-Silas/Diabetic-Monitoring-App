package com.example.diab

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var tvBloodSugar: TextView
    private lateinit var tvExercise: TextView
    private lateinit var tvMeal: TextView
    private lateinit var tvMedication: TextView
    private lateinit var tvTimeOfDay: TextView
    private lateinit var tvTimestamp: TextView

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Get the current user's userId
        val userId = auth.currentUser?.uid

        // Initialize TextViews
        tvBloodSugar = findViewById(R.id.tvBloodSugar)
        tvExercise = findViewById(R.id.tvExercise)
        tvMeal = findViewById(R.id.tvMeal)
        tvMedication = findViewById(R.id.tvMedication)
        tvTimeOfDay = findViewById(R.id.tvTimeOfDay)
        tvTimestamp = findViewById(R.id.tvTimestamp)

        // Fetch data from Firestore
        if (userId != null) {
            fetchUserData(userId)
        }
    }

    private fun fetchUserData(userId: String) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Get the user data
                    val bloodSugar = document.getLong("bloodSugar") ?: 0
                    val exercise = document.getString("exercise") ?: ""
                    val meal = document.getString("meal") ?: ""
                    val medication = document.getString("medication") ?: ""
                    val timeOfDay = document.getString("timeOfDay") ?: ""
                    val timestamp = document.getTimestamp("timestamp")

                    // Update UI with fetched data
                    tvBloodSugar.text = "Blood Sugar: $bloodSugar"
                    tvExercise.text = "Exercise: $exercise"
                    tvMeal.text = "Meal: $meal"
                    tvMedication.text = "Medication: $medication"
                    tvTimeOfDay.text = "Time of Day: $timeOfDay"
                    tvTimestamp.text = "Timestamp: ${timestamp?.toDate()}"
                } else {
                    // Handle the case where the document does not exist
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }
}
