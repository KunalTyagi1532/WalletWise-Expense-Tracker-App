package com.example.expensetracker.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.auth.AuthRepository
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    // =====================================================
    // INPUT STATES
    // =====================================================

    var email by mutableStateOf("")

    var password by mutableStateOf("")

    // =====================================================
    // UI STATES
    // =====================================================

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // =====================================================
    // AUTH STATE
    // =====================================================

    var isUserLoggedIn by mutableStateOf(
        repository.currentUser != null
    )
        private set

    // =====================================================
    // INPUT HANDLERS
    // =====================================================

    fun updateEmail(value: String) {
        email = value
    }

    fun updatePassword(value: String) {
        password = value
    }

    // =====================================================
    // LOGIN
    // =====================================================

    fun onLoginClick() {

        if (!validateInputs()) return

        viewModelScope.launch {

            isLoading = true
            errorMessage = null

            val result = repository.login(
                email = email,
                password = password
            )

            if (result.isSuccess) {

                isUserLoggedIn = true

            } else {

                errorMessage =
                    result.exceptionOrNull()?.message
                        ?: "Login failed"
            }

            isLoading = false
        }
    }

    // =====================================================
    // SIGN UP
    // =====================================================

    fun onSignUpClick() {

        if (!validateInputs()) return

        viewModelScope.launch {

            isLoading = true
            errorMessage = null

            val result = repository.signUp(
                email = email,
                password = password
            )

            if (result.isSuccess) {

                isUserLoggedIn = true

            } else {

                errorMessage =
                    result.exceptionOrNull()?.message
                        ?: "Sign up failed"
            }

            isLoading = false
        }
    }

    // =====================================================
    // RESET PASSWORD
    // =====================================================

    fun resetPassword() {

        if (email.isBlank()) {

            errorMessage =
                "Enter your email first"

            return
        }

        if (
            !android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()
        ) {

            errorMessage =
                "Enter a valid email"

            return
        }

        FirebaseAuth
            .getInstance()

            .sendPasswordResetEmail(email)

            .addOnCompleteListener { task ->

                errorMessage =

                    if (task.isSuccessful) {

                        "Password reset email sent"

                    } else {

                        task.exception?.message
                            ?: "Failed to send reset email"
                    }
            }
    }

    // =====================================================
    // LOGOUT
    // =====================================================

    fun logout() {

        FirebaseAuth
            .getInstance()
            .signOut()

        isUserLoggedIn = false
    }

    // =====================================================
    // VALIDATION
    // =====================================================

    private fun validateInputs(): Boolean {

        return when {

            email.isBlank() -> {

                errorMessage =
                    "Email cannot be empty"

                false
            }

            !android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches() -> {

                errorMessage =
                    "Enter a valid email"

                false
            }

            password.length < 6 -> {

                errorMessage =
                    "Password must be at least 6 characters"

                false
            }

            else -> true
        }
    }
}