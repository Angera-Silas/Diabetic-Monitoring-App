package com.example.diab

data class LogEntry(
    val userId: String = "",  // User ID associated with the log entry
    val bloodSugar: Int = 0,  // Blood sugar level
    val morningMedication: Int = 0,  // Morning medication amount
    val afternoonMedication: Int = 0,  // Afternoon medication amount
    val eveningMedication: Int = 0     // Evening medication amount
)
