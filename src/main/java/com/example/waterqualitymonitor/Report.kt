package com.example.waterqualitymonitor

data class Report(
    val reportId: String? = null,
    val userId: String? = null,
    val userEmail: String? = null,
    val waterQuality: String? = null,
    val confidence: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double = 0.0, // Yeh line Add ki hai
    val longitude: Double = 0.0, // Yeh line Add ki hai
    val status: String = "Pending"
)