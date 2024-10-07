package com.example.diab

data class Patient(
    val id: String? = null, // Nullable type for Firebase deserialization
    val name: String? = null, // Nullable type
    val email: String? = null, // Nullable type
    val phoneNumber: String? = null, // Nullable type
    val userType: String = "Patient" // Default value for userType
)
