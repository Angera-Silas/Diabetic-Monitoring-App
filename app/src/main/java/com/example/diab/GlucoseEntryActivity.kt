package com.example.diab

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
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
    private lateinit var rbBeforeEating: RadioButton
    private lateinit var rbAfterEating: RadioButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var timeOfDay: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glucose_entry)

        // Initialize DrawerLayout and ActionBarDrawerToggle
        drawerLayout = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Show the hamburger icon
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle navigation item selection
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> startActivity(Intent(this, GlucoseChartActivity::class.java))
                R.id.nav_medication -> startActivity(Intent(this, ReportsActivity::class.java))
                // Add more cases for additional navigation items
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Initialize UI elements
        etBloodSugar = findViewById(R.id.etBloodSugar)
        etMedication = findViewById(R.id.etMedication)
        etMeal = findViewById(R.id.etMeal)
        etExercise = findViewById(R.id.etExercise)
        btnAddEntry = findViewById(R.id.btnAddEntry)
        rgMealTiming = findViewById(R.id.rgMealTiming)
        rbBeforeEating = findViewById(R.id.rbBeforeEating)
        rbAfterEating = findViewById(R.id.rbAfterEating)

        // Initialize Firestore and Firebase Authentication
        firestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        // Determine the current time of day
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

        // RadioGroup listener
        rgMealTiming.setOnCheckedChangeListener { _, checkedId ->
            etMeal.visibility = if (checkedId == R.id.rbAfterEating) View.VISIBLE else View.GONE
        }

        // Save button listener
        btnAddEntry.setOnClickListener {
            val bloodSugar = etBloodSugar.text.toString().toIntOrNull()
            val medication = etMedication.text.toString()
            val meal = etMeal.text.toString()
            val exercise = etExercise.text.toString()

            if (rbAfterEating.isChecked && meal.isBlank()) {
                Toast.makeText(this, "Please enter the meal details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (bloodSugar != null && medication.isNotBlank() && exercise.isNotBlank()) {
                saveEntry(bloodSugar, medication, meal, exercise)
                navigateToReports()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
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

            firestore.collection("users").document(userId)
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

    private fun navigateToReports() {
        startActivity(Intent(this, ReportsActivity::class.java))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
