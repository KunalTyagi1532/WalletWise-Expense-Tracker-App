package com.example.expensetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.CategoryProvider
import com.example.expensetracker.ui.components.WeeklyBarChart
import com.example.expensetracker.ui.viewmodel.ExpenseViewModel
import com.example.expensetracker.utils.CurrencyManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: ExpenseViewModel
) {

    val transactions by
    viewModel.transactions.collectAsState()

    val income by
    viewModel.totalIncome.collectAsState()

    val expense by
    viewModel.totalExpense.collectAsState()

    val balance by
    viewModel.totalBalance.collectAsState()

    val savingsRate =
        if (income >= expense && income > 0) {

            (((income - expense) / income) * 100).toInt()

        } else {

            -(((expense - income) /
                    expense.coerceAtLeast(1.0)) * 100).toInt()
        }

    val expenseTransactions =
        transactions.filter {
            it.type == "Expense"
        }

    val categoryTotals =
        expenseTransactions
            .groupBy { it.category }
            .mapValues { entry ->
                entry.value.sumOf { it.amount }
            }
            .toList()
            .sortedByDescending { it.second }

    val highestCategory =
        categoryTotals.firstOrNull()?.first ?: "None"

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Analytics",

                        style =
                            MaterialTheme.typography.headlineSmall,

                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

    ) { paddingValues ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),

            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 16.dp
            ),

            verticalArrangement =
                Arrangement.spacedBy(18.dp)
        ) {

            // =====================================================
            // OVERVIEW
            // =====================================================

            item {

                Text(
                    text = "Monthly Overview",

                    style =
                        MaterialTheme.typography.titleLarge,

                    fontWeight = FontWeight.ExtraBold
                )
            }

            item {

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    AnalyticsStatCard(
                        modifier = Modifier.weight(1f),

                        title = "Income",

                        amount = income,

                        color = Color(0xFF4CAF50),

                        icon =
                            Icons.Default.AccountBalanceWallet
                    )

                    AnalyticsStatCard(
                        modifier = Modifier.weight(1f),

                        title = "Expense",

                        amount = expense,

                        color = Color(0xFFF44336),

                        icon = Icons.Default.BarChart
                    )
                }
            }

            item {

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    AnalyticsStatCard(
                        modifier = Modifier.weight(1f),

                        title = "Savings",

                        amount = balance,

                        color =
                            MaterialTheme.colorScheme.primary,

                        icon = Icons.Default.Savings
                    )

                    AnalyticsStatCard(
                        modifier = Modifier.weight(1f),

                        title = "Rate",

                        amount = savingsRate.toDouble(),

                        suffix = "%",

                        color = Color(0xFFFFB300),

                        icon = Icons.Default.PieChart
                    )
                }
            }

            // =====================================================
            // WEEKLY CHART
            // =====================================================

            item {

                WeeklyBarChart(
                    transactions = transactions
                )
            }

            // =====================================================
            // CATEGORY BREAKDOWN
            // =====================================================

            item {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Spending Breakdown",

                    style =
                        MaterialTheme.typography.titleLarge,

                    fontWeight = FontWeight.ExtraBold
                )
            }

            items(categoryTotals.size) { index ->

                val category =
                    categoryTotals[index].first

                val total =
                    categoryTotals[index].second

                val categoryColor =
                    CategoryProvider.getColorForCategory(
                        category
                    )

                val progress =
                    if (expense > 0) {

                        (total / expense).toFloat()

                    } else {

                        0f
                    }

                Card(

                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(24.dp),

                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.surface
                    ),

                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {

                        Row(

                            modifier = Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.SpaceBetween,

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(categoryColor)
                                )

                                Spacer(
                                    modifier = Modifier.width(10.dp)
                                )

                                Text(
                                    text = category,

                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleMedium,

                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }

                            Text(

                                text =
                                    CurrencyManager.format(
                                        total
                                    ),

                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium,

                                fontWeight =
                                    FontWeight.ExtraBold,

                                color = categoryColor
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        LinearProgressIndicator(

                            progress = { progress },

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(CircleShape),

                            color = categoryColor,

                            trackColor =
                                MaterialTheme
                                    .colorScheme
                                    .surfaceVariant
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Text(

                            text =
                                "${(progress * 100).toInt()}% of expenses",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }
                }
            }

            // =====================================================
            // SMART INSIGHTS
            // =====================================================

            item {

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "Smart Insights",

                    style =
                        MaterialTheme.typography.titleLarge,

                    fontWeight = FontWeight.ExtraBold
                )
            }

            item {

                Card(

                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(28.dp),

                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .primaryContainer
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(22.dp)
                    ) {

                        Text(

                            text =
                                "Highest Spending Category",

                            style =
                                MaterialTheme
                                    .typography
                                    .labelLarge,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onPrimaryContainer
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(

                            text = highestCategory,

                            style =
                                MaterialTheme
                                    .typography
                                    .headlineSmall,

                            fontWeight =
                                FontWeight.ExtraBold,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onPrimaryContainer
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(

                            text =
                                "Keep an eye on this category to improve your monthly savings.",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onPrimaryContainer
                        )
                    }
                }
            }

            item {

                Spacer(
                    modifier = Modifier.height(100.dp)
                )
            }
        }
    }
}

@Composable
private fun AnalyticsStatCard(

    modifier: Modifier = Modifier,

    title: String,

    amount: Double,

    color: Color,

    icon:
    androidx.compose.ui.graphics.vector.ImageVector,

    suffix: String = ""
) {

    Card(

        modifier = modifier,

        shape = RoundedCornerShape(26.dp),

        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            Box(

                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(

                        brush = Brush.radialGradient(

                            colors = listOf(

                                color.copy(alpha = 0.30f),

                                color.copy(alpha = 0.08f)
                            )
                        )
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(

                    imageVector = icon,

                    contentDescription = null,

                    tint = color
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(

                text = title,

                style =
                    MaterialTheme.typography.labelLarge,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(

                text =

                    if (suffix.isNotEmpty()) {

                        "${amount.toInt()}$suffix"

                    } else {

                        CurrencyManager.format(
                            amount
                        )
                    },

                style =
                    MaterialTheme.typography.headlineSmall,

                fontWeight =
                    FontWeight.ExtraBold,

                color = color
            )
        }
    }
}