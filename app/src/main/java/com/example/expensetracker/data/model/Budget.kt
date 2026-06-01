package com.example.expensetracker.data.model
import com.example.expensetracker.utils.CurrencyManager
data class Budget(
    val id: String = "",
    val userId: String = "",
    val totalMonthlyLimit: Double = 0.0,
    val categoryLimits: Map<String, Double> = emptyMap()
)