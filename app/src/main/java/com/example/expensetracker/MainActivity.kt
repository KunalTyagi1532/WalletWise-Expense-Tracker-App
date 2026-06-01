package com.example.expensetracker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.*
import com.example.expensetracker.ui.navigation.BottomNavItem
import com.example.expensetracker.ui.screens.AnalyticsScreen
import com.example.expensetracker.ui.screens.CalendarScreen
import com.example.expensetracker.ui.screens.DashboardScreen
import com.example.expensetracker.ui.screens.GoalsScreen
import com.example.expensetracker.ui.screens.LoginScreen
import com.example.expensetracker.ui.screens.SettingsScreen
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.example.expensetracker.ui.viewmodel.AuthViewModel
import com.example.expensetracker.ui.viewmodel.ExpenseViewModel
import com.example.expensetracker.ui.viewmodel.ThemeViewModel
import com.example.expensetracker.utils.CurrencyManager
import com.example.expensetracker.worker.DailyFinance
import com.example.expensetracker.worker.RecurringTransaction
import java.util.Calendar
import java.util.concurrent.TimeUnit
import com.example.expensetracker.ui.screens.RecurringTransactionsScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // =====================================================
        // SPLASH THEME
        // =====================================================

        setTheme(
            R.style.Theme_ExpenseTracker_Splash
        )

        super.onCreate(savedInstanceState)

        // =====================================================
        // SPLASH API
        // =====================================================

        installSplashScreen()

        // =====================================================
        // NOTIFICATIONS
        // =====================================================

        createNotificationChannel()

        requestNotificationPermission()

        // =====================================================
        // WORKERS
        // =====================================================

        setupRecurringWork()

        setupDailyFinanceWorker()

        setContent {

            // =====================================================
            // LOAD SAVED CURRENCY
            // =====================================================

            val context = LocalContext.current

            LaunchedEffect(Unit) {

                CurrencyManager.loadCurrency(
                    context
                )
            }

            // =====================================================
            // VIEWMODELS
            // =====================================================

            val authViewModel: AuthViewModel =
                viewModel()

            val expenseViewModel: ExpenseViewModel =
                viewModel()

            val themeViewModel: ThemeViewModel =
                viewModel()

            // =====================================================
            // UPI DETECTED DATA
            // =====================================================

            val openAddTransaction =

                intent.getBooleanExtra(
                    "open_add_transaction",
                    false
                )

            val detectedAmount =

                intent.getDoubleExtra(
                    "detected_amount",
                    0.0
                )

            val detectedMerchant =

                intent.getStringExtra(
                    "detected_merchant"
                ) ?: ""

            val detectedCategory =

                intent.getStringExtra(
                    "detected_category"
                ) ?: "Other"

            val detectedType =

                intent.getStringExtra(
                    "detected_type"
                ) ?: "Expense"

            ExpenseTrackerTheme(

                darkTheme =
                    themeViewModel.isDarkMode.value
            ) {

                Surface(

                    modifier =
                        Modifier.fillMaxSize(),

                    color =
                        MaterialTheme.colorScheme.background
                ) {

                    if (authViewModel.isUserLoggedIn) {

                        val currentScreen =
                            remember {
                                mutableStateOf("dashboard")
                            }

                        Scaffold(

                            bottomBar = {

                                NavigationBar(

                                    tonalElevation = 12.dp,

                                    containerColor =
                                        MaterialTheme.colorScheme.surface
                                ) {

                                    NavigationBarItem(

                                        selected =
                                            currentScreen.value ==
                                                    "dashboard",

                                        onClick = {

                                            currentScreen.value =
                                                "dashboard"
                                        },

                                        icon = {

                                            Icon(

                                                imageVector =
                                                    BottomNavItem
                                                        .Dashboard
                                                        .icon,

                                                contentDescription =
                                                    null
                                            )
                                        },

                                        label = {
                                            Text("Home")
                                        }
                                    )

                                    NavigationBarItem(

                                        selected =
                                            currentScreen.value ==
                                                    "analytics",

                                        onClick = {

                                            currentScreen.value =
                                                "analytics"
                                        },

                                        icon = {

                                            Icon(

                                                imageVector =
                                                    BottomNavItem
                                                        .Analytics
                                                        .icon,

                                                contentDescription =
                                                    null
                                            )
                                        },

                                        label = {
                                            Text("Analytics")
                                        }
                                    )

                                    NavigationBarItem(

                                        selected =
                                            currentScreen.value ==
                                                    "calendar",

                                        onClick = {

                                            currentScreen.value =
                                                "calendar"
                                        },

                                        icon = {

                                            Icon(

                                                imageVector =
                                                    BottomNavItem
                                                        .Calendar
                                                        .icon,

                                                contentDescription =
                                                    null
                                            )
                                        },

                                        label = {
                                            Text("Calendar")
                                        }
                                    )

                                    NavigationBarItem(

                                        selected =
                                            currentScreen.value ==
                                                    "goals",

                                        onClick = {

                                            currentScreen.value =
                                                "goals"
                                        },

                                        icon = {

                                            Icon(

                                                imageVector =
                                                    BottomNavItem
                                                        .Goals
                                                        .icon,

                                                contentDescription =
                                                    null
                                            )
                                        },

                                        label = {
                                            Text("Goals")
                                        }
                                    )

                                    NavigationBarItem(

                                        selected =
                                            currentScreen.value ==
                                                    "recurring",

                                        onClick = {

                                            currentScreen.value =
                                                "recurring"
                                        },

                                        icon = {

                                            Icon(

                                                imageVector =
                                                    BottomNavItem
                                                        .Recurring
                                                        .icon,

                                                contentDescription =
                                                    null
                                            )
                                        },

                                        label = {
                                            Text("Recurring")
                                        }
                                    )
                                }
                            }

                        ) { innerPadding ->

                            Box(

                                modifier =
                                    Modifier.padding(
                                        innerPadding
                                    )
                            ) {

                                when (
                                    currentScreen.value
                                ) {

                                    "dashboard" -> {

                                        DashboardScreen(

                                            viewModel =
                                                expenseViewModel,

                                            themeViewModel =
                                                themeViewModel,

                                            onOpenSettings = {

                                                currentScreen.value =
                                                    "settings"
                                            },

                                        )
                                    }

                                    "analytics" -> {

                                        AnalyticsScreen(

                                            viewModel =
                                                expenseViewModel
                                        )
                                    }

                                    "calendar" -> {

                                        CalendarScreen(

                                            viewModel =
                                                expenseViewModel
                                        )
                                    }

                                    "goals" -> {

                                        GoalsScreen(

                                            viewModel =
                                                expenseViewModel
                                        )
                                    }

                                    "settings" -> {

                                        SettingsScreen(

                                            authViewModel =
                                                authViewModel,

                                            themeViewModel =
                                                themeViewModel
                                        )
                                    }

                                    "recurring" -> {

                                        RecurringTransactionsScreen(

                                            viewModel =
                                                expenseViewModel
                                        )
                                    }
                                }
                            }
                        }

                    } else {

                        LoginScreen(
                            viewModel = authViewModel
                        )
                    }
                }
            }
        }
    }

    // =====================================================
    // NOTIFICATION CHANNEL
    // =====================================================

    private fun createNotificationChannel() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(

                    "finance_alerts",

                    "Finance Alerts",

                    NotificationManager
                        .IMPORTANCE_HIGH
                ).apply {

                    description =
                        "UPI transaction alerts"
                }

            val manager =
                getSystemService(
                    NotificationManager::class.java
                )

            manager.createNotificationChannel(
                channel
            )
        }
    }

    // =====================================================
    // NOTIFICATION PERMISSION
    // =====================================================

    private fun requestNotificationPermission() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            if (

                ContextCompat.checkSelfPermission(

                    this,

                    Manifest.permission.POST_NOTIFICATIONS

                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(

                    this,

                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ),

                    1001
                )
            }
        }
    }

    // =====================================================
    // DAILY FINANCE WORKER
    // =====================================================

    private fun setupDailyFinanceWorker() {

        val now =
            Calendar.getInstance()

        val target =
            Calendar.getInstance().apply {

                set(Calendar.HOUR_OF_DAY, 23)

                set(Calendar.MINUTE, 0)

                set(Calendar.SECOND, 0)

                if (before(now)) {

                    add(
                        Calendar.DAY_OF_MONTH,
                        1
                    )
                }
            }

        val initialDelay =

            target.timeInMillis -
                    now.timeInMillis

        val workRequest =

            PeriodicWorkRequestBuilder<DailyFinance>(
                24,
                TimeUnit.HOURS
            )
                .setInitialDelay(
                    initialDelay,
                    TimeUnit.MILLISECONDS
                )
                .build()

        WorkManager.getInstance(this)

            .enqueueUniquePeriodicWork(

                "daily_finance_worker",

                ExistingPeriodicWorkPolicy.KEEP,

                workRequest
            )
    }

    // =====================================================
    // RECURRING WORKER
    // =====================================================

    private fun setupRecurringWork() {

        val constraints =

            Constraints.Builder()

                .setRequiredNetworkType(
                    NetworkType.CONNECTED
                )

                .build()

        val recurringWorkRequest =

            PeriodicWorkRequestBuilder<RecurringTransaction>(
                12,
                TimeUnit.HOURS
            )
                .setConstraints(
                    constraints
                )
                .build()

        WorkManager.getInstance(this)

            .enqueueUniquePeriodicWork(

                "RecurringTransactions",

                ExistingPeriodicWorkPolicy.KEEP,

                recurringWorkRequest
            )
    }
}