package com.example.expensetracker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.utils.CurrencyManager

@Composable
fun SummaryCard(
    balance: Double,
    income: Double,
    expense: Double
) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),

        horizontalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        // =====================================================
        // INCOME
        // =====================================================

        MiniStatCard(

            modifier = Modifier.weight(1f),

            title = "Income",

            amount = income,

            color = Color(0xFF4CAF50),

            icon = Icons.Default.ArrowUpward
        )

        // =====================================================
        // EXPENSE
        // =====================================================

        MiniStatCard(

            modifier = Modifier.weight(1f),

            title = "Expense",

            amount = expense,

            color = Color(0xFFF44336),

            icon = Icons.Default.ArrowDownward
        )

        // =====================================================
        // BALANCE
        // =====================================================

        MiniStatCard(

            modifier = Modifier.weight(1f),

            title = "Balance",

            amount = balance,

            color =
                MaterialTheme.colorScheme.primary,

            icon =
                Icons.Default.AccountBalanceWallet
        )
    }
}

@Composable
private fun MiniStatCard(

    modifier: Modifier = Modifier,

    title: String,

    amount: Double,

    color: Color,

    icon:
    androidx.compose.ui.graphics.vector.ImageVector
) {

    val animatedColor by animateColorAsState(

        targetValue = color,

        label = "stat_color"
    )

    val borderColor =

        if (
            MaterialTheme.colorScheme
                .background
                .luminance() < 0.5f
        ) {

            Color.White.copy(alpha = 0.22f)

        } else {

            Color.Black.copy(alpha = 0.14f)
        }

    Card(

        modifier = modifier,

        shape = RoundedCornerShape(28.dp),

        colors = CardDefaults.cardColors(

            containerColor =
                MaterialTheme.colorScheme.surface
        ),

        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 18.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            // =====================================================
            // ICON
            // =====================================================

            Box(

                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(

                        brush = Brush.radialGradient(

                            colors = listOf(

                                animatedColor.copy(alpha = 0.20f),

                                animatedColor.copy(alpha = 0.06f)
                            )
                        )
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(

                    imageVector = icon,

                    contentDescription = null,

                    tint = animatedColor,

                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =====================================================
            // TITLE
            // =====================================================

            Text(

                text = title,

                style =
                    MaterialTheme.typography.labelMedium,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            // =====================================================
            // AMOUNT
            // =====================================================

            Text(

                text =
                    CurrencyManager.format(
                        amount
                    ),

                style =
                    MaterialTheme.typography.titleLarge,

                fontWeight =
                    FontWeight.ExtraBold,

                color = animatedColor,

                maxLines = 1
            )
        }
    }
}