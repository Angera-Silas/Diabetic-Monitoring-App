package com.example.diab

<<<<<<< HEAD
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignPatient : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextPhoneNumber: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var mAuth: FirebaseAuth

=======
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import android.util.Patterns

class SignPatient : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
>>>>>>> f62f972d3243ae3a06a84796ac99140333b80c9e
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_patient)

<<<<<<< HEAD
        // Initialize views
        editTextName = findViewById(R.id.editTextName)
        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        editTextPhoneNumber = findViewById(R.id.phone_number)
        progressBar = findViewById(R.id.progressBar)

        mAuth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.login_button).setOnClickListener { registerUser() }
    }

    private fun registerUser() {
        val name = editTextName.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val phoneNumber = editTextPhoneNumber.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        // Create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    // Optionally, navigate to another activity or store additional user information (e.g., name, phone number)
                } else {
                    Toast.makeText(this, task.exception?.localizedMessage ?: "Registration failed", Toast.LENGTH_SHORT).show()
=======
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

    // Function to handle user sign-up with Firebase and navigate to login
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
>>>>>>> f62f972d3243ae3a06a84796ac99140333b80c9e
                }
            }
    }
}
