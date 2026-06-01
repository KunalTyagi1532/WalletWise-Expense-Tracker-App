package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.utils.CurrencyManager

@Composable
fun CategoryBudgetSection(

    budget: Budget?,

    transactions: List<Transaction>
) {

    val categoryLimits =
        budget?.categoryLimits ?: emptyMap()

    val expenseTransactions =
        transactions.filter {
            it.type == "Expense"
        }

    val categorySpent =
        expenseTransactions
            .groupBy { it.category }
            .mapValues { entry ->
                entry.value.sumOf { it.amount }
            }

    if (categoryLimits.isEmpty()) {
        return
    }

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        Text(

            text = "Category Budgets",

            style =
                MaterialTheme.typography.titleLarge,

            fontWeight = FontWeight.ExtraBold
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Column(
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            categoryLimits.forEach { (category, limit) ->

                val spent =
                    categorySpent[category] ?: 0.0

                CategoryBudgetCard(

                    category = category,

                    spent = spent,

                    limit = limit
                )
            }
        }
    }
}