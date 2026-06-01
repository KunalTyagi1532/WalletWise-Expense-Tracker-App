package com.example.expensetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.ui.viewmodel.ExpenseViewModel
import androidx.compose.runtime.Composable

@Composable
fun RecurringTransactionsScreen(
    viewModel: ExpenseViewModel
) {

    val transactions by
    viewModel.transactions.collectAsState()

    val recurringTransactions =
        transactions.filter {
            it.recurring
        }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = "Recurring Transactions",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        if (recurringTransactions.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                Text(
                    text = "No recurring transactions"
                )
            }

        } else {

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                items(recurringTransactions) { transaction ->

                    RecurringTransactionCard(
                        transaction = transaction,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun RecurringTransactionCard(
    transaction: Transaction,
    viewModel: ExpenseViewModel
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = transaction.note.ifBlank {
                    transaction.category
                },
                style =
                    MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text =
                    if (
                        transaction.recurringEnabled
                    ) {
                        transaction.recurringInterval
                    } else {
                        "${transaction.recurringInterval} (Paused)"
                    }
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row {

                Button(
                    onClick = {

                        viewModel.setRecurringEnabled(
                            transaction =
                                transaction,
                            enabled =
                                !transaction.recurringEnabled
                        )
                    }
                ) {

                    Text(

                        if (
                            transaction.recurringEnabled
                        ) {
                            "Pause"
                        } else {
                            "Resume"
                        }
                    )
                }

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                OutlinedButton(
                    onClick = {

                        viewModel.deleteTransaction(
                            transaction.id
                        )
                    }
                ) {

                    Text("Delete")
                }
            }
        }
    }
}