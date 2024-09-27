package com.example.diab

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp
import com.google.android.material.textfield.TextInputEditText

class PatientLogIn : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_patient_log_in)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        // Handle system window insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the UI elements
        val emailField: TextInputEditText = findViewById(R.id.emailField) // Changed to emailField
        val passwordField: TextInputEditText = findViewById(R.id.passwordField) // Changed to passwordField
        val signInButton: Button = findViewById(R.id.button3) // Button ID is correct

        // Sign In button click listener
        signInButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (validateInput(email, password)) {
                signInUser(email, password)
            } else {
                Toast.makeText(this, "Please enter valid credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to validate the input fields
    private fun validateInput(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }

    // Function to handle sign in (e.g., authentication process)
    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, OnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    // Navigate to the next activity (e.g., DiabeticLogEntry)
                    val intent = Intent(this, DiabeticLogEntry::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
