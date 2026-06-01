package com.example.expensetracker.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.expensetracker.utils.CurrencyManager

data class CategoryInfo(
    val name: String,
    val icon: ImageVector,
    val color: Color,

    // NEW:
    // Helps future analytics/filter systems
    val isIncomeCategory: Boolean = false
)

object CategoryProvider {

    val categories = listOf(

        // EXPENSE CATEGORIES

        CategoryInfo(
            name = "Food",
            icon = Icons.Default.Restaurant,
            color = Color(0xFFFF9800)
        ),

        CategoryInfo(
            name = "Rent",
            icon = Icons.Default.Home,
            color = Color(0xFF2196F3)
        ),

        CategoryInfo(
            name = "Shopping",
            icon = Icons.Default.ShoppingBag,
            color = Color(0xFFE91E63)
        ),

        CategoryInfo(
            name = "Transport",
            icon = Icons.Default.DirectionsBus,
            color = Color(0xFF4CAF50)
        ),

        CategoryInfo(
            name = "Health",
            icon = Icons.Default.MedicalServices,
            color = Color(0xFFF44336)
        ),

        CategoryInfo(
            name = "Entertainment",
            icon = Icons.Default.Movie,
            color = Color(0xFF9C27B0)
        ),

        // INCOME CATEGORY

        CategoryInfo(
            name = "Salary",
            icon = Icons.Default.Payments,
            color = Color(0xFF00C853),

            isIncomeCategory = true
        ),

        // FALLBACK

        CategoryInfo(
            name = "Other",
            icon = Icons.Default.Category,
            color = Color(0xFF607D8B)
        )
    )

    fun getIconForCategory(name: String): ImageVector {

        return categories.find {
            it.name.equals(name, ignoreCase = true)
        }?.icon ?: Icons.Default.Category
    }

    fun getColorForCategory(name: String): Color {

        return categories.find {
            it.name.equals(name, ignoreCase = true)
        }?.color ?: Color(0xFF607D8B)
    }

    // NEW:
    // Useful for analytics and future filters

    fun isIncomeCategory(name: String): Boolean {

        return categories.find {
            it.name.equals(name, ignoreCase = true)
        }?.isIncomeCategory ?: false
    }
}