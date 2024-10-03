package com.example.diab

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginDoc : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_doc)

        // Handle system window insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the UI elements
        val emailField: TextInputEditText = findViewById(R.id.textView)
        val passwordField: TextInputEditText = findViewById(R.id.textView5)
        val signInButton: Button = findViewById(R.id.button3)
        val forgotPasswordText: TextView = findViewById(R.id.textView10)
        val signUpText: TextView = findViewById(R.id.textView17)

        // Sign In button click listener
        signInButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (validateInput(email, password)) {
                // Add logic for authentication (e.g., API call, Firebase authentication)
                signInUser(email, password)
            } else {
                // Show error message if validation fails
                Toast.makeText(this, "Please enter valid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        // Forgot Password click listener
        forgotPasswordText.setOnClickListener {
            // Navigate to ForgotPasswordActivity
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        // Sign Up click listener
        signUpText.setOnClickListener {
            // Navigate to SignUpActivity
            val intent = Intent(this, SignDoc::class.java)
            startActivity(intent)
        }
    }

    class ForgotPasswordActivity {

    }

    // Function to validate the input fields
    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            return false
        }
        if (password.isEmpty()) {
            return false
        }
        // Add any additional validations as needed (e.g., email format check)
        return true
    }

    // Function to handle sign in (e.g., authentication process)
    private fun signInUser(email: String, password: String) {
        // Here you can add logic to authenticate the user with a server or local database
        // For example, you can use Firebase authentication or your own API call.
FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(
    OnCompleteListener {
        if(it.isSuccessful)
        {
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

            // After successful login, navigate to the DashboardActivity
            val intent = Intent(this, DiabeticLogEntry::class.java)
            startActivity(intent)
            finish()
        }
        else{
            Toast.makeText(this,"Wrong password or username",Toast.LENGTH_SHORT).show()
        }
    })
        // Simulating successful login
        // Close the login activity so it cannot be navigated back to
    }
}




