package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Transaction
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.text.SimpleDateFormat
import java.util.*
import com.example.expensetracker.utils.CurrencyManager

@Composable
fun WeeklyBarChart(
    transactions: List<Transaction>
) {

    val expenseTransactions =
        transactions.filter {
            it.type == "Expense"
        }

    val totalSpending =
        expenseTransactions.sumOf {
            it.amount
        }

    // =====================================================
    // DON'T SHOW USELESS CHARTS
    // =====================================================

    if (totalSpending < 200) {

        Card(

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(28.dp),

            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface
            ),

            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {

            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                Text(

                    text = "Weekly Spending",

                    style =
                        MaterialTheme.typography.titleLarge,

                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(

                    text =
                        "Not enough spending data yet to generate meaningful analytics.",

                    style =
                        MaterialTheme.typography.bodyMedium,

                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        return
    }

    val calendar = Calendar.getInstance()

    val weekData =
        mutableMapOf(
            "Mon" to 0f,
            "Tue" to 0f,
            "Wed" to 0f,
            "Thu" to 0f,
            "Fri" to 0f,
            "Sat" to 0f,
            "Sun" to 0f
        )

    expenseTransactions.forEach { transaction ->

        calendar.timeInMillis =
            transaction.timestamp

        val day =
            SimpleDateFormat(
                "EEE",
                Locale.getDefault()
            ).format(calendar.time)

        if (weekData.containsKey(day)) {

            weekData[day] =
                weekData[day]!! +
                        transaction.amount.toFloat()
        }
    }

    val model =
        entryModelOf(

            weekData["Mon"] ?: 0f,

            weekData["Tue"] ?: 0f,

            weekData["Wed"] ?: 0f,

            weekData["Thu"] ?: 0f,

            weekData["Fri"] ?: 0f,

            weekData["Sat"] ?: 0f,

            weekData["Sun"] ?: 0f
        )

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(28.dp),

        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(

                text = "Weekly Spending",

                style =
                    MaterialTheme.typography.titleLarge,

                fontWeight = FontWeight.ExtraBold
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Chart(

                chart = columnChart(),

                model = model,

                startAxis = null,

                bottomAxis = rememberBottomAxis(

                    guideline = null,

                    valueFormatter = { value, _ ->

                        when (value.toInt()) {

                            0 -> "Mon"
                            1 -> "Tue"
                            2 -> "Wed"
                            3 -> "Thu"
                            4 -> "Fri"
                            5 -> "Sat"
                            6 -> "Sun"

                            else -> ""
                        }
                    }
                ),

                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(
                        horizontal = 8.dp,
                        vertical = 12.dp
                    )
            )
        }
    }
}