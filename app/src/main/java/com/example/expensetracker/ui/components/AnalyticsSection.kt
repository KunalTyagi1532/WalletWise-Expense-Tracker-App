package com.example.expensetracker.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.CategoryProvider
import com.example.expensetracker.data.model.Transaction
import kotlinx.coroutines.delay
import com.example.expensetracker.utils.CurrencyManager

@Composable
fun AnalyticsSection(
    transactions: List<Transaction>
) {

    val expenses = transactions.filter {
        it.type == "Expense" && it.amount > 0
    }

    val totalExpense = expenses.sumOf {
        it.amount
    }

    if (expenses.isEmpty() || totalExpense <= 0.0) return

    val categoryTotals = expenses
        .groupBy { it.category }
        .mapValues { entry ->
            entry.value.sumOf { it.amount }
        }
        .toList()
        .sortedByDescending { it.second }

    var isVisible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(categoryTotals) {

        isVisible = false
        delay(120)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,

        enter =
            fadeIn(animationSpec = tween(500)) +
                    slideInVertically(
                        initialOffsetY = { 80 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),

        exit =
            fadeOut(animationSpec = tween(300)) +
                    slideOutVertically(
                        targetOffsetY = { -60 }
                    )
    ) {

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),

            shape = MaterialTheme.shapes.extraLarge,

            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                // HEADER

                Text(
                    text = "Spending Breakdown",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Track where your money goes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(22.dp))

                // MAIN BAR

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                        .graphicsLayer {
                            shadowElevation = 4.dp.toPx()
                            shape = CircleShape
                            clip = true
                        }
                ) {

                    categoryTotals.forEach { (category, amount) ->

                        val targetWeight =
                            (amount / totalExpense)
                                .toFloat()
                                .coerceAtLeast(0.001f)

                        val animatedWeight by animateFloatAsState(
                            targetValue = targetWeight,
                            animationSpec = tween(900),
                            label = "weight_animation"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(animatedWeight)
                                .background(
                                    CategoryProvider
                                        .getColorForCategory(category)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // LEGEND

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 12.dp)
                ) {

                    items(categoryTotals) { (category, amount) ->

                        val percentage =
                            ((amount / totalExpense) * 100).toInt()

                        ElevatedCard(
                            colors = CardDefaults.elevatedCardColors(
                                containerColor =
                                    CategoryProvider
                                        .getColorForCategory(category)
                                        .copy(alpha = 0.12f)
                            ),
                            shape = CircleShape
                        ) {

                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 8.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(
                                            CategoryProvider
                                                .getColorForCategory(category)
                                        )
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = "$percentage%",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}