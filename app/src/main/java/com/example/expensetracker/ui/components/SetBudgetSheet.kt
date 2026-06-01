package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.expensetracker.utils.CurrencyManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetBudgetSheet(
    currentBudget: Double,
    onSave: (Double) -> Unit,
    onDismiss: () -> Unit
) {

    var budget by remember {
        mutableStateOf(
            if (currentBudget > 0) {
                currentBudget.toInt().toString()
            } else {
                ""
            }
        )
    }

    val parsedBudget =
        budget.toDoubleOrNull() ?: 0.0

    val isValid =
        parsedBudget > 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding()
    ) {

        // HEADER ICON

        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        )
                    )
                ),

            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = null,

                tint = MaterialTheme.colorScheme.primary,

                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // TITLE

        Text(
            text = "Set Monthly Budget",

            style = MaterialTheme.typography.headlineSmall,

            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Control your monthly spending goals.",

            style = MaterialTheme.typography.bodyMedium,

            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        // INPUT FIELD

        OutlinedTextField(
            value = budget,

            onValueChange = {
                budget = it
            },

            label = {
                Text("Monthly Limit")
            },

            placeholder = {
                Text("e.g. 25000")
            },

            leadingIcon = {
                Text(
                    "${CurrencyManager.symbol()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),

            supportingText = {

                if (!isValid && budget.isNotBlank()) {

                    Text(
                        text = "Enter a valid amount"
                    )

                } else {

                    Text(
                        text = "Your spending progress will track against this amount."
                    )
                }
            },

            singleLine = true,

            modifier = Modifier.fillMaxWidth(),

            shape = MaterialTheme.shapes.large
        )

        Spacer(modifier = Modifier.height(32.dp))

        // SAVE BUTTON

        Button(
            onClick = {

                if (isValid) {
                    onSave(parsedBudget)
                }
            },

            enabled = isValid,

            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),

            shape = MaterialTheme.shapes.large
        ) {

            Text(
                text = "Save Budget",

                style = MaterialTheme.typography.titleMedium,

                fontWeight = FontWeight.Bold
            )
        }
    }
}