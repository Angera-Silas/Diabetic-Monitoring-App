package com.example.diab

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.diab.AdminDash
import com.example.diab.GlucoseChartActivity
import com.example.diab.PatientListActivity
import com.example.diab.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val emailField: EditText = findViewById(R.id.email)
        val passwordField: EditText = findViewById(R.id.password)
        val loginButton: Button = findViewById(R.id.login_button)
        val goToSignupButton: TextView = findViewById(R.id.go_to_signup_button)
        progressBar = findViewById(R.id.progressBar)

        // Move to the next input when "Enter" is pressed
        emailField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                if (isValidEmail(emailField.text.toString().toLowerCase(Locale.ROOT).trim())) {
                    passwordField.requestFocus()
                    true
                } else {
                    emailField.error = "Enter a valid email address"
                    false
                }
            } else {
                false
            }
        }

        passwordField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginButton.performClick() // Submit the form
                true
            } else {
                false
            }
        }

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim().toLowerCase(Locale.ROOT)
            val password = passwordField.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                progressBar.visibility = ProgressBar.VISIBLE
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        progressBar.visibility = ProgressBar.GONE
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            if (userId != null) {
                                firestore.collection("users").document(userId).get()
                                    .addOnSuccessListener { document ->
                                        val userType = document.getString("userType")
                                        if (userType != null) {
                                            // Save user details locally
                                            saveUserDetails(email, userType)
                                            navigateToDashboard(userType)
                                        } else {
                                            Toast.makeText(this, "Unknown user type", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(this, "Failed to fetch user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        goToSignupButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun saveUserDetails(email: String, userType: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("userType", userType)
        editor.apply()
    }

    private fun navigateToDashboard(userType: String) {
        when (userType) {
            "Doctor" -> startActivity(Intent(this, PatientListActivity::class.java))
            "Patient" -> startActivity(Intent(this, GlucoseChartActivity::class.java))
            "Admin" -> startActivity(Intent(this, AdminDash::class.java))
            else -> Toast.makeText(this, "Unknown user type", Toast.LENGTH_SHORT).show()
        }
        finish()
    }
}
