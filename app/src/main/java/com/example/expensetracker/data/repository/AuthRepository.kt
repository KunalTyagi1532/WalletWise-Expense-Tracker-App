package com.example.expensetracker.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import com.example.expensetracker.utils.CurrencyManager

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    // CURRENT USER

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // LOGIN

    suspend fun login(
        email: String,
        password: String
    ): Result<FirebaseUser?> {

        return try {

            val result = auth
                .signInWithEmailAndPassword(
                    email.trim(),
                    password
                )
                .await()

            Result.success(result.user)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // SIGN UP

    suspend fun signUp(
        email: String,
        password: String
    ): Result<FirebaseUser?> {

        return try {

            val result = auth
                .createUserWithEmailAndPassword(
                    email.trim(),
                    password
                )
                .await()

            Result.success(result.user)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // LOGOUT

    fun logout() {
        auth.signOut()
    }

    // EMAIL VERIFICATION

    suspend fun sendEmailVerification(): Result<Unit> {

        return try {

            auth.currentUser
                ?.sendEmailVerification()
                ?.await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // PASSWORD RESET

    suspend fun resetPassword(
        email: String
    ): Result<Unit> {

        return try {

            auth.sendPasswordResetEmail(
                email.trim()
            ).await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}