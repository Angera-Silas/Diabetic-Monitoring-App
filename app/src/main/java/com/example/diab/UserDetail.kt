package com.example.diab

data class UserDetail(
    val bloodSugar: Long,
    val exercise: String,
    val meal: String,
    val medication: String,
    val timeOfDay: String,
    val timestamp: String // Consider using a Date type if you want to manipulate dates
)
