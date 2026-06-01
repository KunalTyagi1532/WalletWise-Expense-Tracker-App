package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.CategoryProvider
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.utils.CurrencyManager
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionItem(
    transaction: Transaction,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onRecurringToggleClick: () -> Unit
) {

    val categoryColor =
        CategoryProvider.getColorForCategory(
            transaction.category
        )

    val isIncome =
        transaction.type == "Income"

    val transactionBorder =
        if (isIncome) {

            Color(0xFF00E676).copy(alpha = 0.18f)

        } else {

            MaterialTheme.colorScheme.error.copy(alpha = 0.18f)
        }

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),

        shape = RoundedCornerShape(28.dp),

        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        ),

        border = BorderStroke(
            width = 1.2.dp,
            color = transactionBorder
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 16.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(

                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(

                        brush = Brush.radialGradient(

                            colors = listOf(

                                categoryColor.copy(alpha = 0.35f),

                                categoryColor.copy(alpha = 0.10f)
                            )
                        )
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(

                    imageVector =
                        CategoryProvider.getIconForCategory(
                            transaction.category
                        ),

                    contentDescription = null,

                    tint = categoryColor,

                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(

                    text = when {

                        transaction.note.isNotBlank() -> {

                            transaction.note
                        }

                        transaction.category.isNotBlank()
                                && transaction.category != "Transaction" -> {

                            transaction.category
                        }

                        else -> {

                            "Miscellaneous"
                        }
                    },

                    style =
                        MaterialTheme.typography.titleMedium,

                    fontWeight = FontWeight.Bold,

                    color =
                        MaterialTheme.colorScheme.onSurface,

                    maxLines = 1,

                    overflow =
                        TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(

                        text = transaction.category,

                        style =
                            MaterialTheme.typography.labelMedium,

                        color = categoryColor,

                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(

                        text = "•",

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(

                        text =
                            SimpleDateFormat(
                                "MMM dd, hh:mm a",
                                Locale.getDefault()
                            ).format(
                                Date(transaction.timestamp)
                            ),

                        style =
                            MaterialTheme.typography.labelMedium,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }

                if (
                    transaction.recurringInterval != "None"
                ) {

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Surface(

                        shape = CircleShape,

                        color =
                            MaterialTheme
                                .colorScheme
                                .primary
                                .copy(alpha = 0.12f)
                    ) {

                        Row(

                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),

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
                                    Modifier.size(14.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text(

                                text =

                                    if (
                                        transaction.recurring &&
                                        !transaction.recurringEnabled
                                    ) {

                                        "${transaction.recurringInterval} (Paused)"

                                    } else {

                                        transaction.recurringInterval
                                    },

                                style =
                                    MaterialTheme
                                        .typography
                                        .labelSmall,

                                fontWeight =
                                    FontWeight.SemiBold,

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .primary
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                horizontalAlignment =
                    Alignment.End
            ) {

                Text(

                    text =
                        if (isIncome) {

                            "+${CurrencyManager.format(
                                transaction.amount
                            )}"

                        } else {

                            "-${CurrencyManager.format(
                                transaction.amount
                            )}"
                        },

                    style =
                        MaterialTheme.typography.titleLarge,

                    fontWeight = FontWeight.ExtraBold,

                    color =
                        if (isIncome) {

                            Color(0xFF4CAF50)

                        } else {

                            Color(0xFFF44336)
                        }
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Row {

                    FilledTonalIconButton(

                        onClick = onEditClick,

                        modifier =
                            Modifier.size(34.dp)
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Edit,

                            contentDescription =
                                "Edit",

                            modifier =
                                Modifier.size(18.dp)
                        )
                    }

                    if (transaction.recurring) {

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        FilledTonalIconButton(

                            onClick =
                                onRecurringToggleClick,

                            modifier =
                                Modifier.size(34.dp)
                        ) {

                            Icon(

                                imageVector =
                                    Icons.Default.Repeat,

                                contentDescription =

                                    if (
                                        transaction.recurringEnabled
                                    ) {
                                        "Pause Recurring"
                                    } else {
                                        "Resume Recurring"
                                    },

                                modifier =
                                    Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    FilledTonalIconButton(

                        onClick = onDeleteClick,

                        modifier =
                            Modifier.size(34.dp),

                        colors =
                            IconButtonDefaults
                                .filledTonalIconButtonColors(

                                    containerColor =
                                        MaterialTheme
                                            .colorScheme
                                            .errorContainer
                                )
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Delete,

                            contentDescription =
                                "Delete",

                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .error,

                            modifier =
                                Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}