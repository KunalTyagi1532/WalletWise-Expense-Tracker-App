package com.example.expensetracker.data.model
import com.example.expensetracker.utils.CurrencyManager
data class SavingsGoal(

    val id: String = "",

    val title: String = "",

    val saved: Double = 0.0,

    val target: Double = 0.0,

    val userId: String = ""
)