package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Withdrawal(
    val id: String,
    val userId: String = "user_default",
    val method: String, // "Mobile Recharge", "bKash", "Nagad", "Rocket"
    val operator: String? = null, // "Grameenphone (GP)", "Robi", "Banglalink", "Airtel", "Skitto", "Teletalk"
    val accountNumber: String,
    val amountBdt: Double,
    val pointsDeducted: Int,
    val status: String = "Pending", // "Pending", "Approved", "Rejected"
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
}
