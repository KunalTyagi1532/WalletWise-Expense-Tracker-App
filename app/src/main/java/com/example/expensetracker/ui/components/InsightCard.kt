package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.utils.CurrencyManager

@Composable
fun InsightCard(

    title: String,

    description: String,

    icon: ImageVector,

    gradient: List<Color>
) {

    Card(

        modifier = Modifier
            .fillMaxWidth(),

        shape = RoundedCornerShape(28.dp),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {

        Box(

            modifier = Modifier
                .fillMaxWidth()
                .background(

                    brush = Brush.linearGradient(
                        colors = gradient
                    )
                )
                .padding(20.dp)
        ) {

            Row(

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(

                    modifier = Modifier
                        .size(54.dp)
                        .background(
                            Color.White.copy(alpha = 0.18f),
                            RoundedCornerShape(18.dp)
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    Icon(

                        imageVector = icon,

                        contentDescription = null,

                        tint = Color.White
                    )
                }

                Spacer(
                    modifier = Modifier.width(16.dp)
                )

                Column {

                    Text(

                        text = title,

                        style =
                            MaterialTheme.typography.titleMedium,

                        fontWeight = FontWeight.ExtraBold,

                        color = Color.White
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(

                        text = description,

                        style =
                            MaterialTheme.typography.bodyMedium,

                        color =
                            Color.White.copy(alpha = 0.92f)
                    )
                }
            }
        }
    }
}