package com.example.diab

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        Handler().postDelayed({
            val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val savedEmail = sharedPreferences.getString("email", null)
            val savedUserType = sharedPreferences.getString("userType", null)

            if (savedEmail != null && savedUserType != null) {
                // Verify user details from the database
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    firestore.collection("users").document(currentUser.uid).get()
                        .addOnSuccessListener { document ->
                            val userType = document.getString("userType")
                            if (userType == savedUserType) {
                                // Proceed to respective dashboard
                                navigateToDashboard(userType)
                            } else {
                                // Redirect to login
                                redirectToLogin()
                            }
                        }
                        .addOnFailureListener {
                            redirectToLogin()
                        }
                } else {
                    redirectToLogin()
                }
            } else {
                redirectToLogin()
            }
        }, 2000) // 2-second delay
    }

    private fun navigateToDashboard(userType: String?) {
        when (userType) {
            "Doctor" -> startActivity(Intent(this, PatientListActivity::class.java))
            "Patient" -> startActivity(Intent(this, GlucoseChartActivity::class.java))
            "Admin" -> startActivity(Intent(this, AdminDash::class.java))
            else -> redirectToLogin()
        }
        finish()
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
