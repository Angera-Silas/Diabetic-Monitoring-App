package com.example.diab

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginPatient : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_patient)

        // Initialize views
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        progressBar = findViewById(R.id.progressBar)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Set up button listener
        findViewById<Button>(R.id.btnLogin).setOnClickListener { loginUser() }
        findViewById<Button>(R.id.btnSignUp).setOnClickListener {    val intent = Intent(this, SignPatient::class.java)
            startActivity(intent)
            finish()}
    }

    private fun loginUser() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and Password are required", Toast.LENGTH_SHORT).show()
            return
        }

        changeInProgress(true)

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                changeInProgress(false)
                if (task.isSuccessful) {
                    // Successful login
                    val intent = Intent(this, GlucoseChartActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Handle login failure
                    Toast.makeText(this, task.exception?.localizedMessage ?: "Login failed. Try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }



    private fun changeInProgress(inProgress: Boolean) {
        progressBar.visibility = if (inProgress) View.VISIBLE else View.GONE
        findViewById<Button>(R.id.btnLogin).isEnabled = !inProgress
        findViewById<Button>(R.id.btnSignUp).isEnabled = !inProgress
    }
}
