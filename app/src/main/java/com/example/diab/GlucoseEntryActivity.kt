package com.example.diab

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class GlucoseEntryActivity : AppCompatActivity() {

    private lateinit var etBloodSugar: EditText
    private lateinit var etMedication: EditText
    private lateinit var etMeal: EditText
    private lateinit var etExercise: EditText
    private lateinit var btnAddEntry: Button
    private lateinit var rgMealTiming: RadioGroup
    private lateinit var rgMedicationTiming: RadioGroup
    private lateinit var rbBeforeEating: RadioButton
    private lateinit var rbAfterEating: RadioButton
    private lateinit var rbBeforeMedication: RadioButton
    private lateinit var rbAfterMedication: RadioButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var timeOfDay: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glucose_entry)

        // Initialize UI elements
        etBloodSugar = findViewById(R.id.etBloodSugar)
        etMedication = findViewById(R.id.etMedication)
        etMeal = findViewById(R.id.etMeal)
        etExercise = findViewById(R.id.etExercise)
        btnAddEntry = findViewById(R.id.btnAddEntry)
        rgMealTiming = findViewById(R.id.rgMealTiming)
        rgMedicationTiming = findViewById(R.id.rgMedicationTiming)
        rbBeforeEating = findViewById(R.id.rbBeforeEating)
        rbAfterEating = findViewById(R.id.rbAfterEating)
        rbBeforeMedication = findViewById(R.id.rbBeforeMedication)
        rbAfterMedication = findViewById(R.id.rbAfterMedication)

        // Initialize Firestore and Firebase Authentication
        firestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        // Determine the time of day
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        timeOfDay = when (currentHour) {
            in 6..11 -> "Morning"
            in 12..17 -> "Afternoon"
            in 18..21 -> "Evening"
            else -> "Night"
        }

        // Set the hint for medication input based on time of day
        etMedication.hint = when (timeOfDay) {
            "Morning" -> "Enter Morning Medication"
            "Afternoon" -> "Enter Afternoon Medication"
            "Evening" -> "Enter Evening Medication"
            else -> "Enter Night Medication"
        }

        // Handle RadioGroup interactions for medication timing
        rgMedicationTiming.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbAfterMedication) {
                etMedication.visibility = View.VISIBLE
            } else {
                etMedication.visibility = View.GONE
            }
        }

        // Handle RadioGroup interactions for meal timing
        rgMealTiming.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbAfterEating) {
                etMeal.visibility = View.VISIBLE
            } else {
                etMeal.visibility = View.GONE
            }
        }

        // Add Entry button logic
        btnAddEntry.setOnClickListener {
            val bloodSugar = etBloodSugar.text.toString().toIntOrNull()
            val medication = if (rbAfterMedication.isChecked) etMedication.text.toString() else ""
            val meal = if (rbAfterEating.isChecked) etMeal.text.toString() else ""
            val exercise = etExercise.text.toString()

            if (bloodSugar == null) {
                Toast.makeText(this, "Please enter your blood sugar level", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (rbAfterMedication.isChecked && medication.isBlank()) {
                Toast.makeText(this, "Please enter the medication", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (rbAfterEating.isChecked && meal.isBlank()) {
                Toast.makeText(this, "Please enter the meal details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveEntry(bloodSugar, medication, meal, exercise)
        }
    }

    private fun saveEntry(bloodSugar: Int, medication: String, meal: String, exercise: String) {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            val entryData = hashMapOf(
                "userId" to userId,
                "bloodSugar" to bloodSugar,
                "medication" to medication,
                "meal" to meal,
                "exercise" to exercise,
                "timeOfDay" to timeOfDay,
                "timestamp" to FieldValue.serverTimestamp()
            )

            firestore
                .collection("glucose_entries")
                .add(entryData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Entry saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving entry: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
