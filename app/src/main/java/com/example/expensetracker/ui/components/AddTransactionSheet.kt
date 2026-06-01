package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.CategoryProvider
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.utils.CurrencyManager
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    transactionToEdit: Transaction? = null,
    onSave: (Transaction) -> Unit,
    onDismiss: () -> Unit
) {

    var amount by remember {
        mutableStateOf(
            transactionToEdit?.amount?.toString() ?: ""
        )
    }

    var note by remember {
        mutableStateOf(
            transactionToEdit?.note ?: ""
        )
    }

    var selectedCategory by remember {
        mutableStateOf(
            transactionToEdit?.category ?: "Food"
        )
    }

    var selectedType by remember {
        mutableStateOf(
            transactionToEdit?.type ?: "Expense"
        )
    }

    // =====================================================
    // RECURRING STATES
    // =====================================================

    var recurring by remember {
        mutableStateOf(
            transactionToEdit?.recurring ?: false
        )
    }

    var recurringInterval by remember {
        mutableStateOf(
            transactionToEdit?.recurringInterval ?: "Monthly"
        )
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    val intervals = listOf(
        "Daily",
        "Weekly",
        "Monthly",
        "Yearly"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding()
    ) {

        // =====================================================
        // HEADER
        // =====================================================

        Text(

            text =
                if (transactionToEdit == null) {
                    "New Transaction"
                } else {
                    "Edit Transaction"
                },

            style =
                MaterialTheme.typography.headlineSmall,

            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // =====================================================
        // AMOUNT FIELD
        // =====================================================

        OutlinedTextField(

            value = amount,

            onValueChange = {
                amount = it
            },

            label = {
                Text("Amount")
            },

            leadingIcon = {

                Text(

                    text =
                        CurrencyManager.symbol(),

                    style =
                        MaterialTheme.typography.titleMedium,

                    fontWeight = FontWeight.Bold
                )
            },

            modifier = Modifier.fillMaxWidth(),

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),

            shape = MaterialTheme.shapes.medium,

            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // =====================================================
        // LIVE CONVERSION PREVIEW
        // =====================================================

        if (amount.toDoubleOrNull() != null) {

            Text(

                text =
                    "Preview: ${
                        CurrencyManager.format(
                            amount.toDouble()
                        )
                    }",

                style =
                    MaterialTheme.typography.bodyMedium,

                color =
                    MaterialTheme.colorScheme.primary,

                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        // =====================================================
        // NOTE FIELD
        // =====================================================

        OutlinedTextField(

            value = note,

            onValueChange = {
                note = it
            },

            label = {
                Text("Note (Optional)")
            },

            placeholder = {
                Text("Coffee, Gym, Netflix...")
            },

            modifier = Modifier.fillMaxWidth(),

            shape = MaterialTheme.shapes.medium
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // =====================================================
        // TYPE TOGGLE
        // =====================================================

        Text(

            text = "Transaction Type",

            style =
                MaterialTheme.typography.titleSmall,

            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {

            SegmentedButton(

                selected =
                    selectedType == "Expense",

                onClick = {

                    selectedType = "Expense"

                    if (
                        CategoryProvider
                            .isIncomeCategory(
                                selectedCategory
                            )
                    ) {

                        selectedCategory = "Food"
                    }
                },

                shape =
                    SegmentedButtonDefaults.itemShape(
                        index = 0,
                        count = 2
                    ),

                colors =
                    SegmentedButtonDefaults.colors(

                        activeContainerColor =
                            Color(0xFFFFEBEE),

                        activeContentColor =
                            Color(0xFFD32F2F)
                    )
            ) {

                Text("Expense")
            }

            SegmentedButton(

                selected =
                    selectedType == "Income",

                onClick = {

                    selectedType = "Income"

                    if (
                        !CategoryProvider
                            .isIncomeCategory(
                                selectedCategory
                            )
                    ) {

                        selectedCategory = "Salary"
                    }
                },

                shape =
                    SegmentedButtonDefaults.itemShape(
                        index = 1,
                        count = 2
                    ),

                colors =
                    SegmentedButtonDefaults.colors(

                        activeContainerColor =
                            Color(0xFFE8F5E9),

                        activeContentColor =
                            Color(0xFF2E7D32)
                    )
            ) {

                Text("Income")
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // =====================================================
        // CATEGORY SECTION
        // =====================================================

        Text(

            text = "Category",

            style =
                MaterialTheme.typography.titleSmall,

            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        LazyRow(

            horizontalArrangement =
                Arrangement.spacedBy(8.dp),

            contentPadding =
                PaddingValues(horizontal = 4.dp)
        ) {

            items(

                CategoryProvider.categories.filter {

                    if (selectedType == "Income") {

                        it.isIncomeCategory

                    } else {

                        !it.isIncomeCategory
                    }
                }

            ) { category ->

                val isSelected =
                    selectedCategory == category.name

                FilterChip(

                    selected = isSelected,

                    onClick = {
                        selectedCategory = category.name
                    },

                    label = {
                        Text(category.name)
                    },

                    leadingIcon = {

                        Icon(

                            imageVector =
                                category.icon,

                            contentDescription = null,

                            modifier =
                                Modifier.size(18.dp)
                        )
                    },

                    colors =
                        FilterChipDefaults
                            .filterChipColors(

                                selectedContainerColor =
                                    category.color.copy(alpha = 0.2f),

                                selectedLabelColor =
                                    category.color,

                                selectedLeadingIconColor =
                                    category.color
                            )
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // =====================================================
        // RECURRING SECTION
        // =====================================================

        Card(

            modifier = Modifier.fillMaxWidth(),

            colors = CardDefaults.cardColors(

                containerColor =
                    MaterialTheme.colorScheme
                        .surfaceVariant
                        .copy(alpha = 0.4f)
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Row(

                    modifier = Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically,

                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Repeat,

                            contentDescription = null,

                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .primary,

                            modifier =
                                Modifier.size(20.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Column {

                            Text(

                                text =
                                    "Recurring Transaction",

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyLarge,

                                fontWeight =
                                    FontWeight.SemiBold
                            )

                            Text(

                                text =
                                    "Automatically repeats",

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

                    Switch(

                        checked = recurring,

                        onCheckedChange = {
                            recurring = it
                        }
                    )
                }

                if (recurring) {

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    ExposedDropdownMenuBox(

                        expanded = expanded,

                        onExpandedChange = {
                            expanded = !expanded
                        }
                    ) {

                        OutlinedTextField(

                            value = recurringInterval,

                            onValueChange = {},

                            readOnly = true,

                            label = {
                                Text("Repeat Frequency")
                            },

                            trailingIcon = {

                                ExposedDropdownMenuDefaults
                                    .TrailingIcon(
                                        expanded = expanded
                                    )
                            },

                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),

                            shape =
                                MaterialTheme.shapes.medium
                        )

                        ExposedDropdownMenu(

                            expanded = expanded,

                            onDismissRequest = {
                                expanded = false
                            }
                        ) {

                            intervals.forEach { interval ->

                                DropdownMenuItem(

                                    text = {
                                        Text(interval)
                                    },

                                    onClick = {

                                        recurringInterval =
                                            interval

                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        // =====================================================
        // SAVE BUTTON
        // =====================================================

        Button(

            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),

            shape = MaterialTheme.shapes.medium,

            onClick = {

                val parsedAmount =
                    amount.toDoubleOrNull() ?: 0.0

                if (parsedAmount > 0) {

                    onSave(

                        Transaction(

                            id =
                                transactionToEdit?.id
                                    ?: UUID.randomUUID()
                                        .toString(),

                            userId =
                                transactionToEdit?.userId
                                    ?: "",

                            amount = parsedAmount,

                            category =
                                selectedCategory,

                            type =
                                selectedType,

                            note =
                                note.trim(),

                            timestamp =
                                transactionToEdit?.timestamp
                                    ?: System.currentTimeMillis(),

                            recurring = recurring,

                            recurringInterval =

                                if (recurring) {

                                    recurringInterval

                                } else {

                                    "None"
                                },

                            recurringEnabled =

                                transactionToEdit?.recurringEnabled

                                    ?: true,

                            lastRecurringGenerated =

                                transactionToEdit?.lastRecurringGenerated

                                    ?: System.currentTimeMillis(),

                            createdAt =
                                transactionToEdit?.createdAt
                                    ?: System.currentTimeMillis(),

                            updatedAt =
                                System.currentTimeMillis()
                        )
                    )
                }
            }
        ) {

            Text(

                text = "Save Transaction",

                style =
                    MaterialTheme.typography.titleMedium,

                fontWeight = FontWeight.Bold
            )
        }
    }
}