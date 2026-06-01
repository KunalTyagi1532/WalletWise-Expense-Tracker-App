package com.example.expensetracker.ui.components

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.utils.CurrencyManager
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBudgetCard(
    remaining: Double,
    totalLimit: Double,
    progress: Float,
    isExceeded: Boolean,
    onSetBudgetClick: () -> Unit
) {

    // =====================================================
    // CONTEXT
    // =====================================================

    val context = LocalContext.current

    // =====================================================
    // SAVE BUDGET STATUS
    // ONLY RUNS WHEN STATUS CHANGES
    // =====================================================

    LaunchedEffect(isExceeded) {

        val prefs = context.getSharedPreferences(
            "budget_prefs",
            Context.MODE_PRIVATE
        )

        prefs.edit()

            .putBoolean(
                "budget_exceeded",
                isExceeded
            )

            .apply()
    }

    // =====================================================
    // ANIMATIONS
    // =====================================================

    val animatedContainerColor by animateColorAsState(

        targetValue =

            if (isExceeded) {

                MaterialTheme.colorScheme.errorContainer

            } else {

                MaterialTheme.colorScheme.surface
            },

        animationSpec = tween(500),

        label = "container_color"
    )

    val animatedContentColor by animateColorAsState(

        targetValue =

            if (isExceeded) {

                MaterialTheme.colorScheme.error

            } else {

                MaterialTheme.colorScheme.primary
            },

        animationSpec = tween(500),

        label = "content_color"
    )

    val animatedProgress by animateFloatAsState(

        targetValue = progress.coerceIn(0f, 1f),

        animationSpec = tween(900),

        label = "budget_progress"
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

    // =====================================================
    // CARD
    // =====================================================

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),

        shape = MaterialTheme.shapes.extraLarge,

        colors = CardDefaults.cardColors(
            containerColor = animatedContainerColor
        ),

        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),

        onClick = onSetBudgetClick
    ) {

        Column(
            modifier = Modifier.padding(24.dp)
        ) {

            // =====================================================
            // HEADER
            // =====================================================

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(

                    shape = CircleShape,

                    color =
                        animatedContentColor
                            .copy(alpha = 0.12f)
                ) {

                    Icon(

                        imageVector =

                            if (isExceeded) {

                                Icons.Default.Warning

                            } else {

                                Icons.Default.AccountBalanceWallet
                            },

                        contentDescription = null,

                        tint = animatedContentColor,

                        modifier = Modifier
                            .padding(10.dp)
                            .size(22.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column {

                    Text(

                        text =

                            if (isExceeded) {

                                "Budget Exceeded"

                            } else {

                                "Monthly Budget"
                            },

                        style =
                            MaterialTheme.typography.titleMedium,

                        fontWeight = FontWeight.Bold
                    )

                    Text(

                        text =

                            if (totalLimit > 0) {

                                "Tap to edit your limit"

                            } else {

                                "Set your monthly spending cap"
                            },

                        style =
                            MaterialTheme.typography.bodySmall,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            // =====================================================
            // AMOUNTS
            // =====================================================

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.Bottom
            ) {

                Column {

                    Text(

                        text =

                            if (isExceeded) {

                                "Overspent"

                            } else {

                                "Remaining"
                            },

                        style =
                            MaterialTheme.typography.labelLarge,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(

                        text =
                            CurrencyManager.format(
                                abs(remaining)
                            ),

                        style =
                            MaterialTheme.typography.headlineLarge,

                        fontWeight =
                            FontWeight.ExtraBold,

                        color =

                            if (isExceeded) {

                                animatedContentColor

                            } else {

                                MaterialTheme
                                    .colorScheme
                                    .onSurface
                            }
                    )
                }

                Column(
                    horizontalAlignment =
                        Alignment.End
                ) {

                    Text(

                        text = "Budget",

                        style =
                            MaterialTheme.typography.labelMedium,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(

                        text =
                            CurrencyManager.format(
                                totalLimit
                            ),

                        style =
                            MaterialTheme.typography.titleLarge,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // =====================================================
            // PROGRESS BAR
            // =====================================================

            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme
                            .colorScheme
                            .surfaceVariant
                    )
            ) {

                Box(

                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(CircleShape)
                        .background(

                            brush =
                                Brush.horizontalGradient(

                                    colors =

                                        if (isExceeded) {

                                            listOf(

                                                MaterialTheme
                                                    .colorScheme
                                                    .error,

                                                MaterialTheme
                                                    .colorScheme
                                                    .error
                                                    .copy(alpha = 0.7f)
                                            )

                                        } else {

                                            listOf(

                                                MaterialTheme
                                                    .colorScheme
                                                    .primary,

                                                MaterialTheme
                                                    .colorScheme
                                                    .primary
                                                    .copy(alpha = 0.7f)
                                            )
                                        }
                                )
                        )
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // =====================================================
            // FOOTER
            // =====================================================

            if (isExceeded) {

                Text(

                    text =
                        "⚠️ You're ${
                            CurrencyManager.format(
                                abs(remaining)
                            )
                        } over budget.",

                    style =
                        MaterialTheme.typography.bodyMedium,

                    fontWeight =
                        FontWeight.SemiBold,

                    color = animatedContentColor
                )

            } else {

                val percentUsed =
                    (animatedProgress * 100).toInt()

                Text(

                    text =
                        "$percentUsed% of monthly budget used",

                    style =
                        MaterialTheme.typography.bodyMedium,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }
        }
    }
}