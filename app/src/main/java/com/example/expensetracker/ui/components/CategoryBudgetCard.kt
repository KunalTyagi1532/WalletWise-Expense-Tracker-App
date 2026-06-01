package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.CategoryProvider
import com.example.expensetracker.utils.CurrencyManager

@Composable
fun CategoryBudgetCard(

    category: String,

    spent: Double,

    limit: Double
) {

    val progress =
        if (limit > 0) {
            (spent / limit).toFloat()
        } else {
            0f
        }

    val categoryColor =
        when {

            progress >= 1f -> {
                MaterialTheme.colorScheme.error
            }

            progress >= 0.8f -> {
                Color(0xFFFFB300)
            }

            else -> {
                CategoryProvider
                    .getColorForCategory(category)
            }
        }

    val remaining =
        limit - spent

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
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(categoryColor)
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Text(

                        text = category,

                        style =
                            MaterialTheme.typography
                                .titleMedium,

                        fontWeight = FontWeight.Bold
                    )
                }

                Text(

                    text =
                        "${CurrencyManager.symbol()}${spent.toInt()} / ${CurrencyManager.symbol()}${limit.toInt()}",

                    style =
                        MaterialTheme.typography.labelLarge,

                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            LinearProgressIndicator(

                progress = {
                    progress.coerceAtMost(1f)
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(CircleShape),

                color = categoryColor,

                trackColor =
                    MaterialTheme.colorScheme
                        .surfaceVariant
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(

                text =
                    if (remaining >= 0) {

                        "${CurrencyManager.symbol()}${remaining.toInt()} remaining"

                    } else {

                        "${CurrencyManager.symbol()}${(-remaining).toInt()} over budget"
                    },

                style =
                    MaterialTheme.typography.bodySmall,

                fontWeight = FontWeight.SemiBold,

                color = categoryColor
            )
        }
    }
}