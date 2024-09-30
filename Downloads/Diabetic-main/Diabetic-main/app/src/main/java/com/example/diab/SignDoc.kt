package com.example.diab

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignDoc : AppCompatActivity() {

    private lateinit var editTextPhoneNumber: EditText
    private lateinit var editTextDateOfBirth: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_doc)

        // Initialize the views
        editTextPhoneNumber = findViewById(R.id.editTextPhone)
        editTextDateOfBirth = findViewById(R.id.editTextDOB)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        progressBar = findViewById(R.id.progressBar)

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Set up button listener
        findViewById<Button>(R.id.btnSignUp).setOnClickListener { createAccount() }
    }

    private fun createAccount() {
        val phoneNumber = editTextPhoneNumber.text.toString().trim()
        val dateOfBirth = editTextDateOfBirth.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString()

        if (validateData(phoneNumber, dateOfBirth, email, password)) {
            createAccountInFirebase(phoneNumber, dateOfBirth, email, password)
        }
    }

    private fun validateData(phoneNumber: String, dateOfBirth: String, email: String, password: String): Boolean {
        var isValid = true

        if (phoneNumber.isEmpty()) {
            editTextPhoneNumber.error = "Phone number is required"
            isValid = false
        }
        if (dateOfBirth.isEmpty()) {
            editTextDateOfBirth.error = "Date of birth is required"
            isValid = true
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.error = "Email is invalid"
            isValid = false
        }
        if (password.length < 6) {
            editTextPassword.error = "Password too short"
            isValid = false
        }
        return isValid
    }

    private fun createAccountInFirebase(phoneNumber: String, dateOfBirth: String, email: String, password: String) {
        changeInProgress(true)
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                changeInProgress(false)
                if (task.isSuccessful) {
                    // Account creation successful
                    val user = mAuth.currentUser
                    user?.let {
                        val userId = it.uid
                        val userData = hashMapOf(
                            "id" to userId,
                            "phoneNumber" to phoneNumber,
                            "dob" to dateOfBirth,
                            "email" to email,
                            "userType" to "doctor"
                        )

                        // Save user data in Firestore
                        db.collection("users").document(userId).set(userData)
                            .addOnSuccessListener {
                                // Notify user about successful registration
                                Toast.makeText(this, "Successfully created account. Check your email for verification.", Toast.LENGTH_LONG).show()

                                // Send email verification
                                user.sendEmailVerification().addOnCompleteListener { verifyTask ->
                                    if (verifyTask.isSuccessful) {
                                        // Logout after sending email verification
                                        FirebaseAuth.getInstance().signOut()

                                        // Navigate to login screen after showing success message
                                        onLoginClick()
                                    } else {
                                        // Handle email verification sending failure
                                        Toast.makeText(this, "Failed to send verification email. Try again.", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                            .addOnFailureListener {
                                // Handle failure in saving user data
                                Toast.makeText(this, "Failed to save user data. Please try again.", Toast.LENGTH_LONG).show()
                            }
                    }
                } else {
                    // Handle account creation failure
                    Toast.makeText(this, task.exception?.localizedMessage ?: "Failed to create account. Try again.", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun onLoginClick() {
        // Handle navigation to login screen
        val intent = Intent(this, LoginDoc::class.java)
        startActivity(intent)
        finish()
    }

    private fun changeInProgress(inProgress: Boolean) {
        progressBar.visibility = if (inProgress) View.VISIBLE else View.GONE
        findViewById<Button>(R.id.btnSignUp).isEnabled = !inProgress
    }


}
