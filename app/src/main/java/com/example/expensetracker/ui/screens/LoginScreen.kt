package com.example.expensetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Center
        ) {

            // =====================================================
            // TOP ICON
            // =====================================================

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                            )
                        )
                    ),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,

                    contentDescription = null,

                    tint = MaterialTheme.colorScheme.primary,

                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // =====================================================
            // TITLE
            // =====================================================

            Text(
                text = "Expense Tracker",

                style = MaterialTheme.typography.headlineLarge,

                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Manage your finances smarter.",

                style = MaterialTheme.typography.bodyLarge,

                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(42.dp))

            // =====================================================
            // LOGIN CARD
            // =====================================================

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(32.dp),

                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 4.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(24.dp)
                ) {

                    Text(
                        text = "Welcome Back",

                        style = MaterialTheme.typography.headlineSmall,

                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Login to continue",

                        style = MaterialTheme.typography.bodyMedium,

                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // =====================================================
                    // EMAIL FIELD
                    // =====================================================

                    OutlinedTextField(
                        value = viewModel.email,

                        onValueChange = {
                            viewModel.updateEmail(it)
                        },

                        label = {
                            Text("Email")
                        },

                        modifier = Modifier.fillMaxWidth(),

                        singleLine = true,

                        shape = RoundedCornerShape(18.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // =====================================================
                    // PASSWORD FIELD
                    // =====================================================

                    OutlinedTextField(
                        value = viewModel.password,

                        onValueChange = {
                            viewModel.updatePassword(it)
                        },

                        label = {
                            Text("Password")
                        },

                        modifier = Modifier.fillMaxWidth(),

                        visualTransformation =
                            PasswordVisualTransformation(),

                        singleLine = true,

                        shape = RoundedCornerShape(18.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // =====================================================
                    // LOADING
                    // =====================================================

                    if (viewModel.isLoading) {

                        Box(
                            modifier = Modifier.fillMaxWidth(),

                            contentAlignment = Alignment.Center
                        ) {

                            CircularProgressIndicator()
                        }

                    } else {

                        // =====================================================
                        // LOGIN BUTTON
                        // =====================================================

                        Button(
                            onClick = {
                                viewModel.onLoginClick()
                            },

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),

                            shape = RoundedCornerShape(20.dp)
                        ) {

                            Text(
                                text = "Login",

                                style = MaterialTheme.typography.titleMedium,

                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // =====================================================
                        // FORGOT PASSWORD
                        // =====================================================

                        TextButton(

                            onClick = {

                                viewModel.resetPassword()
                            },

                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                text = "Forgot Password?"
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // =====================================================
                        // SIGN UP
                        // =====================================================

                        TextButton(
                            onClick = {
                                viewModel.onSignUpClick()
                            },

                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                text = "Create New Account"
                            )
                        }
                    }

                    // =====================================================
                    // ERROR / STATUS MESSAGE
                    // =====================================================

                    viewModel.errorMessage?.let {

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = it,

                            color = MaterialTheme.colorScheme.error,

                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}