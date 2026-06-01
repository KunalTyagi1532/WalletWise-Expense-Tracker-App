package com.example.expensetracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ui.viewmodel.AuthViewModel
import com.example.expensetracker.ui.viewmodel.ThemeViewModel
import com.example.expensetracker.utils.CurrencyManager
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(

    authViewModel: AuthViewModel,

    themeViewModel: ThemeViewModel
) {

    val context =
        LocalContext.current

    // =====================================================
    // LOAD SAVED CURRENCY
    // =====================================================

    LaunchedEffect(Unit) {

        CurrencyManager.loadCurrency(
            context
        )
    }

    val selectedCurrency =
        CurrencyManager.selectedCurrency

    val currentUser =
        FirebaseAuth.getInstance().currentUser

    var notificationsEnabled by remember {
        mutableStateOf(true)
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(

                        text = "Settings",

                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

    ) { paddingValues ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            // =====================================================
            // ACCOUNT
            // =====================================================

            Card(

                shape = RoundedCornerShape(24.dp),

                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surface
                )
            ) {

                Column {

                    ListItem(

                        headlineContent = {
                            Text("Account")
                        },

                        supportingContent = {
                            Text(
                                currentUser?.email
                                    ?: "Unknown User"
                            )
                        },

                        leadingContent = {

                            Icon(
                                imageVector =
                                    Icons.Default.Person,

                                contentDescription = null
                            )
                        }
                    )

                    HorizontalDivider()

                    ListItem(

                        headlineContent = {
                            Text("Logout")
                        },

                        supportingContent = {
                            Text("Sign out of account")
                        },

                        leadingContent = {

                            Icon(
                                imageVector =
                                    Icons.Default.Logout,

                                contentDescription = null
                            )
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                                authViewModel.logout()
                            }
                    )
                }
            }

            // =====================================================
            // APPEARANCE
            // =====================================================

            Card(

                shape = RoundedCornerShape(24.dp),

                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surface
                )
            ) {

                ListItem(

                    headlineContent = {
                        Text("Dark Mode")
                    },

                    supportingContent = {
                        Text("Toggle app theme")
                    },

                    leadingContent = {

                        Icon(
                            imageVector =
                                Icons.Default.DarkMode,

                            contentDescription = null
                        )
                    },

                    trailingContent = {

                        Switch(

                            checked =
                                themeViewModel
                                    .isDarkMode.value,

                            onCheckedChange = {

                                themeViewModel
                                    .toggleTheme()
                            }
                        )
                    }
                )
            }

            // =====================================================
            // CURRENCY
            // =====================================================

            Card(

                shape = RoundedCornerShape(24.dp),

                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surface
                )
            ) {

                Column {

                    ListItem(

                        headlineContent = {
                            Text("Preferred Currency")
                        },

                        supportingContent = {
                            Text(

                                when (
                                    selectedCurrency.value
                                ) {

                                    "USD" ->
                                        "US Dollar ($)"

                                    "EUR" ->
                                        "Euro (€)"

                                    "GBP" ->
                                        "British Pound (£)"

                                    "JPY" ->
                                        "Japanese Yen (¥)"

                                    else ->
                                        "Indian Rupee (₹)"
                                }
                            )
                        }
                    )

                    HorizontalDivider()

                    Row(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),

                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        listOf(
                            "INR",
                            "USD",
                            "EUR",
                            "GBP",
                            "JPY"
                        ).forEach { currency ->

                            FilterChip(

                                selected =
                                    selectedCurrency.value ==
                                            currency,

                                onClick = {

                                    CurrencyManager
                                        .saveCurrency(

                                            context,
                                            currency
                                        )
                                },

                                label = {
                                    Text(currency)
                                }
                            )
                        }
                    }
                }
            }

            // =====================================================
            // NOTIFICATIONS
            // =====================================================

            Card(

                shape = RoundedCornerShape(24.dp),

                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surface
                )
            ) {

                ListItem(

                    headlineContent = {
                        Text("Notifications")
                    },

                    supportingContent = {
                        Text("Daily finance reminders")
                    },

                    leadingContent = {

                        Icon(
                            imageVector =
                                Icons.Default.Notifications,

                            contentDescription = null
                        )
                    },

                    trailingContent = {

                        Switch(

                            checked =
                                notificationsEnabled,

                            onCheckedChange = {

                                notificationsEnabled =
                                    it
                            }
                        )
                    }
                )
            }

            // =====================================================
            // DATA
            // =====================================================

            Card(

                shape = RoundedCornerShape(24.dp),

                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surface
                )
            ) {

                ListItem(

                    headlineContent = {
                        Text("Clear Transactions")
                    },

                    supportingContent = {
                        Text("Delete all saved transactions")
                    },

                    leadingContent = {

                        Icon(
                            imageVector =
                                Icons.Default.Delete,

                            contentDescription = null
                        )
                    }
                )
            }

            // =====================================================
            // ABOUT
            // =====================================================

            Card(

                shape = RoundedCornerShape(24.dp),

                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surface
                )
            ) {

                ListItem(

                    headlineContent = {
                        Text("About App")
                    },

                    supportingContent = {
                        Text(
                            "ExpenseTracker v1.0\nBuilt with Jetpack Compose + Firebase"
                        )
                    },

                    leadingContent = {

                        Icon(
                            imageVector =
                                Icons.Default.Info,

                            contentDescription = null
                        )
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(40.dp)
            )
        }
    }
}