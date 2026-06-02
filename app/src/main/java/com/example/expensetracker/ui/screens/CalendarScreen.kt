package com.example.expensetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ui.components.TransactionItem
import com.example.expensetracker.ui.viewmodel.ExpenseViewModel
import com.example.expensetracker.utils.CurrencyManager
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(
    viewModel: ExpenseViewModel
) {
    val transactions by viewModel.transactions.collectAsState()

    // Fixed state definitions: Hold the TimeInMillis or create a fresh Calendar instance on change
    var selectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var currentMonthTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Derive calendar states safely outside layout block to avoid unnecessary recalculations
    val currentMonthCalendar = remember(currentMonthTime) {
        Calendar.getInstance().apply { timeInMillis = currentMonthTime }
    }

    val monthYear = remember(currentMonthCalendar) {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonthCalendar.time)
    }

    // Correctly align calendar grid days based on day-of-week offsets
    val weeksGrid = remember(currentMonthCalendar) {
        val days = mutableListOf<Int?>()
        val calendarCopy = (currentMonthCalendar.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }

        // SUNDAY = 1, MONDAY = 2, etc.
        val firstDayOfWeekOffset = calendarCopy.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = calendarCopy.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Pad start of the month with nulls
        repeat(firstDayOfWeekOffset) { days.add(null) }
        // Add actual days
        for (i in 1..daysInMonth) { days.add(i) }
        // Pad end of the month to make it standard full rows
        while (days.size % 7 != 0) { days.add(null) }

        days.chunked(7)
    }

    val selectedDayTransactions = remember(transactions, selectedDateMillis) {
        transactions.filter { isSameDay(it.timestamp, selectedDateMillis) }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // =====================================================
        // HEADER
        // =====================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    currentMonthTime = (currentMonthCalendar.clone() as Calendar).apply {
                        add(Calendar.MONTH, -1)
                    }.timeInMillis
                }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous Month"
                )
            }

            Text(
                text = monthYear,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = {
                    currentMonthTime = (currentMonthCalendar.clone() as Calendar).apply {
                        add(Calendar.MONTH, 1)
                    }.timeInMillis
                }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next Month"
                )
            }
        }

        // =====================================================
        // CALENDAR GRID & TRANSACTIONS
        // =====================================================
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(weeksGrid) { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { day ->
                        if (day == null) {
                            Spacer(modifier = Modifier.weight(1f))
                        } else {
                            val dayCalendar = (currentMonthCalendar.clone() as Calendar).apply {
                                set(Calendar.DAY_OF_MONTH, day)
                            }

                            // Calculate spending for this specific day
                            val totalSpent = remember(transactions, dayCalendar) {
                                transactions.filter {
                                    it.type == "Expense" && isSameDay(it.timestamp, dayCalendar.timeInMillis)
                                }.sumOf { it.amount }
                            }

                            val isSelected = isSameDay(dayCalendar.timeInMillis, selectedDateMillis)

                            val spendingColor = when {
                                totalSpent >= 1000 -> Color(0xFFE53935)
                                totalSpent >= 500 -> Color(0xFFFF9800)
                                totalSpent > 0 -> Color(0xFF4CAF50)
                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .clickable {
                                        selectedDateMillis = dayCalendar.timeInMillis
                                    }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(spendingColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        color = if (totalSpent > 0 || totalSpent >= 500) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = if (totalSpent > 0) CurrencyManager.format(totalSpent) else "-",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // =====================================================
            // SELECTED DAY TRANSACTIONS SECTION
            // =====================================================
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "Transactions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (selectedDayTransactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No transactions for this day")
                    }
                }
            } else {
                items(selectedDayTransactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onEditClick = {},
                        onDeleteClick = {},
                        onRecurringToggleClick = {
                            if (transaction.recurring) {
                                viewModel.setRecurringEnabled(
                                    transaction = transaction,
                                    enabled = !transaction.recurringEnabled
                                )
                            }
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}

private fun isSameDay(first: Long, second: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = first }
    val cal2 = Calendar.getInstance().apply { timeInMillis = second }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}