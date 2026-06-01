package com.example.expensetracker.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.data.model.Transaction
import java.util.Calendar
import kotlin.math.absoluteValue
import com.example.expensetracker.utils.CurrencyManager

@Composable
fun InsightsSection(

    transactions: List<Transaction>,

    budget: Budget?,

    goals: List<SavingsGoal>
) {

    val expenseTransactions =
        transactions.filter {
            it.type == "Expense"
        }

    val totalExpense =
        expenseTransactions.sumOf {
            it.amount
        }

    // =====================================================
    // TOP CATEGORY
    // =====================================================

    val topCategory =
        expenseTransactions
            .groupBy { it.category }
            .maxByOrNull {
                it.value.sumOf { tx -> tx.amount }
            }
            ?.key ?: "None"

    // =====================================================
    // MONTH COMPARISON
    // =====================================================

    val currentMonth =
        Calendar.getInstance()
            .get(Calendar.MONTH)

    val lastMonth =
        if (currentMonth == 0) 11 else currentMonth - 1

    val currentMonthExpense =
        expenseTransactions.sumOf { tx ->

            val calendar =
                Calendar.getInstance().apply {
                    timeInMillis = tx.timestamp
                }

            if (
                calendar.get(Calendar.MONTH)
                == currentMonth
            ) {

                tx.amount

            } else {
                0.0
            }
        }

    val lastMonthExpense =
        expenseTransactions.sumOf { tx ->

            val calendar =
                Calendar.getInstance().apply {
                    timeInMillis = tx.timestamp
                }

            if (
                calendar.get(Calendar.MONTH)
                == lastMonth
            ) {

                tx.amount

            } else {
                0.0
            }
        }

    val percentageChange =
        if (lastMonthExpense > 0) {

            (
                    (currentMonthExpense - lastMonthExpense)
                            / lastMonthExpense
                    ) * 100

        } else {
            0.0
        }

    // =====================================================
    // BUDGET STATUS
    // =====================================================

    val budgetLimit =
        budget?.totalMonthlyLimit ?: 0.0

    val budgetProgress =
        if (budgetLimit > 0) {
            totalExpense / budgetLimit
        } else {
            0.0
        }

    // =====================================================
    // TODAY EXPENSE CHECK
    // =====================================================

    val today =
        Calendar.getInstance()

    val spentToday =
        expenseTransactions.any { tx ->

            val calendar =
                Calendar.getInstance().apply {
                    timeInMillis = tx.timestamp
                }

            calendar.get(Calendar.DAY_OF_YEAR) ==
                    today.get(Calendar.DAY_OF_YEAR)
        }

    // =====================================================
    // GOAL PROGRESS
    // =====================================================

    val closestGoal =
        goals.maxByOrNull {

            if (it.target > 0)
                it.saved / it.target
            else
                0.0
        }

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {

        Text(

            text = "Smart Insights",

            modifier = Modifier.padding(horizontal = 16.dp),

            style =
                MaterialTheme.typography.titleLarge,

            fontWeight = FontWeight.ExtraBold
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Row(

            modifier = Modifier
                .horizontalScroll(
                    rememberScrollState()
                )
                .padding(horizontal = 16.dp),

            horizontalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            // =====================================================
            // TOP CATEGORY
            // =====================================================

            InsightCard(

                title = "Top Category",

                description =
                    "$topCategory was your highest expense category.",

                icon = Icons.Default.LocalFireDepartment,

                gradient =
                    listOf(
                        Color(0xFFFF9800),
                        Color(0xFFFFB74D)
                    )
            )

            // =====================================================
            // TREND
            // =====================================================

            InsightCard(

                title = "Monthly Trend",

                description =

                    if (percentageChange >= 0) {

                        "You spent ${
                            percentageChange.toInt()
                        }% more than last month."

                    } else {

                        "You reduced spending by ${
                            percentageChange.absoluteValue.toInt()
                        }% this month."
                    },

                icon =
                    if (percentageChange >= 0)
                        Icons.Default.TrendingUp
                    else
                        Icons.Default.TrendingDown,

                gradient =
                    if (percentageChange >= 0) {

                        listOf(
                            Color(0xFFE53935),
                            Color(0xFFEF5350)
                        )

                    } else {

                        listOf(
                            Color(0xFF43A047),
                            Color(0xFF66BB6A)
                        )
                    }
            )

            // =====================================================
            // BUDGET HEALTH
            // =====================================================

            InsightCard(

                title = "Budget Health",

                description =

                    when {

                        budgetProgress >= 1f -> {

                            "You exceeded your monthly budget."
                        }

                        budgetProgress >= 0.8f -> {

                            "You're close to exceeding your budget."
                        }

                        else -> {

                            "You're within your monthly budget."
                        }
                    },

                icon = Icons.Default.Insights,

                gradient =

                    when {

                        budgetProgress >= 1f -> {

                            listOf(
                                Color(0xFFD32F2F),
                                Color(0xFFE57373)
                            )
                        }

                        budgetProgress >= 0.8f -> {

                            listOf(
                                Color(0xFFFFA000),
                                Color(0xFFFFCA28)
                            )
                        }

                        else -> {

                            listOf(
                                Color(0xFF1E88E5),
                                Color(0xFF64B5F6)
                            )
                        }
                    }
            )

            // =====================================================
            // GOAL PROGRESS
            // =====================================================

            closestGoal?.let { goal ->

                val progress =
                    ((goal.saved / goal.target) * 100)
                        .toInt()

                if (progress >= 25) {

                    InsightCard(

                        title = "Savings Goal",

                        description =
                            "You're $progress% towards ${goal.title}.",

                        icon = Icons.Default.Savings,

                        gradient =
                            listOf(
                                Color(0xFF43A047),
                                Color(0xFF66BB6A)
                            )
                    )
                }
            }

            // =====================================================
            // NO SPENDING TODAY
            // =====================================================

            if (!spentToday) {

                InsightCard(

                    title = "Discipline",

                    description =
                        "No expenses recorded today. Great control.",

                    icon = Icons.Default.Savings,

                    gradient =
                        listOf(
                            Color(0xFF00897B),
                            Color(0xFF4DB6AC)
                        )
                )
            }
        }
    }
}