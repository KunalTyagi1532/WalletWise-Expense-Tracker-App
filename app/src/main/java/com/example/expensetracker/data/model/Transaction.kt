package com.example.expensetracker.data.model

data class Transaction(

    val id: String = "",

    val userId: String = "",

    val amount: Double = 0.0,

    val category: String = "Other",

    val type: String = "Expense",

    val note: String = "",

    val timestamp: Long = System.currentTimeMillis(),

    // =====================================================
    // RECURRING SYSTEM
    // =====================================================

    // Main recurring flag used everywhere

    val recurring: Boolean = false,

    // Daily / Weekly / Monthly etc.

    val recurringInterval: String = "None",

    // Prevents duplicate recurring generation

    val lastRecurringGenerated: Long = 0L,

    // =====================================================
    // ANALYTICS / HISTORY
    // =====================================================

    // Original creation time

    val createdAt: Long = System.currentTimeMillis(),

    // Updated whenever edited

    val updatedAt: Long = System.currentTimeMillis(),

    val recurringEnabled: Boolean = true,
)