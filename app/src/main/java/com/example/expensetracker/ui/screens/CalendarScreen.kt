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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    val transactions by
    viewModel.transactions.collectAsState()

    var selectedDate by remember {

        mutableStateOf(
            Calendar.getInstance()
        )
    }

    var currentMonth by remember {

        mutableStateOf(
            Calendar.getInstance()
        )
    }

    val daysInMonth =
        currentMonth.getActualMaximum(
            Calendar.DAY_OF_MONTH
        )

    val monthYear =

        SimpleDateFormat(
            "MMMM yyyy",
            Locale.getDefault()
        ).format(currentMonth.time)

    val selectedDayTransactions =

        transactions.filter {

            isSameDay(
                it.timestamp,
                selectedDate.timeInMillis
            )
        }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // =====================================================
        // HEADER
        // =====================================================

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            IconButton(

                onClick = {

                    currentMonth.add(
                        Calendar.MONTH,
                        -1
                    )
                }
            ) {

                Icon(
                    imageVector =
                        Icons.Default.KeyboardArrowLeft,

                    contentDescription = null
                )
            }

            Text(

                text = monthYear,

                style =
                    MaterialTheme.typography.headlineSmall,

                fontWeight = FontWeight.Bold
            )

            IconButton(

                onClick = {

                    currentMonth.add(
                        Calendar.MONTH,
                        1
                    )
                }
            ) {

                Icon(
                    imageVector =
                        Icons.Default.KeyboardArrowRight,

                    contentDescription = null
                )
            }
        }

        // =====================================================
        // CALENDAR GRID
        // =====================================================

        LazyColumn(

            modifier = Modifier.weight(1f),

            contentPadding = PaddingValues(
                horizontal = 8.dp
            )
        ) {

            items((1..daysInMonth).chunked(7)) { week ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    week.forEach { day ->

                        val dayCalendar =
                            Calendar.getInstance().apply {

                                time = currentMonth.time

                                set(
                                    Calendar.DAY_OF_MONTH,
                                    day
                                )
                            }

                        val totalSpent =

                            transactions.filter {

                                it.type == "Expense" &&

                                        isSameDay(
                                            it.timestamp,
                                            dayCalendar.timeInMillis
                                        )

                            }.sumOf {
                                it.amount
                            }

                        val spendingColor =

                            when {

                                totalSpent >= 1000 ->
                                    Color.Red

                                totalSpent >= 500 ->
                                    Color(0xFFFF9800)

                                totalSpent > 0 ->
                                    Color(0xFF4CAF50)

                                else ->
                                    MaterialTheme
                                        .colorScheme
                                        .surfaceVariant
                            }

                        Column(

                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                                .clip(
                                    RoundedCornerShape(18.dp)
                                )
                                .background(
                                    MaterialTheme
                                        .colorScheme
                                        .surface
                                )
                                .clickable {

                                    selectedDate =
                                        dayCalendar
                                }
                                .padding(
                                    vertical = 10.dp,
                                    horizontal = 4.dp
                                ),

                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Box(

                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(spendingColor),

                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Text(

                                    text = day.toString(),

                                    color = Color.White,

                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(6.dp)
                            )

                            Text(

                                text =

                                    if (totalSpent > 0) {

                                        CurrencyManager.format(
                                            totalSpent
                                        )

                                    } else {

                                        "-"
                                    },

                                style =
                                    MaterialTheme
                                        .typography
                                        .labelSmall,

                                fontWeight =
                                    FontWeight.SemiBold,

                                textAlign =
                                    TextAlign.Center,

                                maxLines = 1
                            )
                        }
                    }

                    // =====================================================
                    // FILL EMPTY CELLS
                    // =====================================================

                    repeat(7 - week.size) {

                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                }
            }

            // =====================================================
            // SELECTED DAY TRANSACTIONS
            // =====================================================

            item {

                HorizontalDivider()

                Text(

                    text = "Transactions",

                    style =
                        MaterialTheme.typography.titleLarge,

                    fontWeight = FontWeight.Bold,

                    modifier =
                        Modifier.padding(16.dp)
                )
            }

            if (selectedDayTransactions.isEmpty()) {

                item {

                    Box(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                "No transactions for this day"
                        )
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

                                    enabled =
                                        !transaction.recurringEnabled
                                )
                            }
                        }
                    )
                }
            }

            item {

                Spacer(
                    modifier = Modifier.height(120.dp)
                )
            }
        }
    }
}

// =====================================================
// HELPERS
// =====================================================

private fun isSameDay(
    first: Long,
    second: Long
): Boolean {

    val cal1 =
        Calendar.getInstance().apply {

            timeInMillis = first
        }

    val cal2 =
        Calendar.getInstance().apply {

            timeInMillis = second
        }

    return cal1.get(Calendar.YEAR) ==
            cal2.get(Calendar.YEAR)

            &&

            cal1.get(Calendar.DAY_OF_YEAR) ==
            cal2.get(Calendar.DAY_OF_YEAR)
}