package com.example.expensetracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(

    val route: String,

    val title: String,

    val icon: ImageVector
) {

    // =====================================================
    // DASHBOARD
    // =====================================================

    data object Dashboard : BottomNavItem(

        route = "dashboard",

        title = "Dashboard",

        icon = Icons.Default.Home
    )

    // =====================================================
    // ANALYTICS
    // =====================================================

    data object Analytics : BottomNavItem(

        route = "analytics",

        title = "Analytics",

        icon = Icons.Default.Analytics
    )

    // =====================================================
    // CALENDAR
    // =====================================================

    data object Calendar : BottomNavItem(

        route = "calendar",

        title = "Calendar",

        icon = Icons.Default.CalendarMonth
    )

    // =====================================================
    // GOALS
    // =====================================================

    data object Goals : BottomNavItem(

        route = "goals",

        title = "Goals",

        icon = Icons.Default.Flag
    )

    // =====================================================
    // RECURRING
    // =====================================================

    data object Recurring : BottomNavItem(

        route = "recurring",

        title = "Recurring",

        icon = Icons.Default.Repeat
    )
}