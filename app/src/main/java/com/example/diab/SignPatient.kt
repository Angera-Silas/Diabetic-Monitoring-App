package com.example.diab

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class SignPatient : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_patient)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Handle system window insets for edge-to-edge layout (optional)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.email)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI elements
        val nameField: EditText = findViewById(R.id.name)
        val emailField: EditText = findViewById(R.id.email)
        val passwordField: EditText = findViewById(R.id.password)
        val phoneField: EditText = findViewById(R.id.phone_number)
        val signUpButton: Button = findViewById(R.id.login_button)

        // Set the sign-up button click listener
        signUpButton.setOnClickListener {
            val name = nameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val phoneNumber = phoneField.text.toString().trim()

            // Validate the input before proceeding
            if (validateInput(name, email, password, phoneNumber)) {
                signUpUser(email, password, phoneNumber)
            }
        }
    }

    // Function to validate the input fields
    private fun validateInput(name: String, email: String, password: String, phoneNumber: String): Boolean {
        return when {
            name.isEmpty() -> {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                false
            }
            email.isEmpty() -> {
                Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show()
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                false
            }
            password.isEmpty() -> {
                Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                false
            }
            password.length < 6 -> {
                Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                false
            }
            phoneNumber.isEmpty() -> {
                Toast.makeText(this, "Phone number cannot be empty", Toast.LENGTH_SHORT).show()
                false
            }
            !Patterns.PHONE.matcher(phoneNumber).matches() -> {
                Toast.makeText(this, "Invalid phone number format", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    // Function to handle user sign-up with Firebase
    private fun signUpUser(email: String, password: String, phoneNumber: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration successful
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    // Navigate to LoginActivity
                    val intent = Intent(this, PatientLogIn::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish() // Finish the current activity so it can't be returned to
                } else {
                    // Registration failed
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
