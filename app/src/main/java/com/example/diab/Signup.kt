package com.example.diab

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
import java.util.UUID

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize UI elements
        val nameField: EditText = findViewById(R.id.name)
        val emailField: EditText = findViewById(R.id.email)
        val passwordField: EditText = findViewById(R.id.password)
        val phoneNumberField: EditText = findViewById(R.id.phone_number)
        val signupButton: Button = findViewById(R.id.signup_button)
        val userRoleGroup: RadioGroup = findViewById(R.id.user_role)
        progressBar = findViewById(R.id.progressBar)

        // Set focus-based navigation with validation
        setupFieldNavigation(nameField, emailField, ::validateName)
        setupFieldNavigation(emailField, phoneNumberField, ::validateEmail)
        setupFieldNavigation(phoneNumberField, passwordField, ::validatePhoneNumber)

        // On final field, trigger submission
        passwordField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && validatePassword(passwordField.text.toString())) {
                signupButton.performClick() // Trigger the sign-up process
                true
            } else {
                false
            }
        }

        signupButton.setOnClickListener {
            val name = nameField.text.toString().trim()
            val email = emailField.text.toString().trim().toLowerCase(Locale.ROOT)
            val password = passwordField.text.toString().trim()
            val phoneNumber = phoneNumberField.text.toString().trim()

            if (validateInput(name, email, password, phoneNumber)) {
                val selectedRoleId = userRoleGroup.checkedRadioButtonId
                val selectedRole: RadioButton = findViewById(selectedRoleId)
                val userType = selectedRole.text.toString()

                progressBar.visibility = ProgressBar.VISIBLE
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        progressBar.visibility = ProgressBar.GONE
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            if (userId != null) {
                                val user = hashMapOf(
                                    "id" to userId,
                                    "name" to name,
                                    "email" to email,
                                    "phoneNumber" to phoneNumber,
                                    "userType" to userType
                                )
                                firestore.collection("users").document(userId).set(user)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_SHORT).show()
                                        when (userType) {
                                            "Doctor" -> startActivity(Intent(this, DoctorDash::class.java))
                                            "Patient" -> startActivity(Intent(this, GlucoseChartActivity::class.java))
                                            "Admin" -> startActivity(Intent(this, AdminDashboard::class.java))
                                        }
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Sign Up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun setupFieldNavigation(
        currentField: EditText,
        nextField: EditText,
        validationFunction: (String) -> Boolean
    ) {
        currentField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT && validationFunction(currentField.text.toString())) {
                nextField.requestFocus()
                true
            } else {
                false
            }
        }
    }

    private fun validateName(name: String): Boolean {
        return if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun validateEmail(email: String): Boolean {
        return if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun validatePhoneNumber(phoneNumber: String): Boolean {
        return if (phoneNumber.isEmpty() || phoneNumber.length < 10) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun validatePassword(password: String): Boolean {
        return if (password.isEmpty() || password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun validateInput(name: String, email: String, password: String, phoneNumber: String): Boolean {
        return validateName(name) && validateEmail(email) && validatePhoneNumber(phoneNumber) && validatePassword(password)
    }
}