package com.example.expensetracker.utils

import android.content.Context
import androidx.compose.runtime.mutableStateOf

object CurrencyManager {

    // =====================================================
    // GLOBAL CURRENCY STATE
    // =====================================================

    val selectedCurrency =
        mutableStateOf("INR")

    // =====================================================
    // SHARED PREFS
    // =====================================================

    private const val PREF_NAME =
        "currency_prefs"

    private const val KEY_CURRENCY =
        "selected_currency"

    // =====================================================
    // LOAD SAVED CURRENCY
    // =====================================================

    fun loadCurrency(
        context: Context
    ) {

        val prefs =

            context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )

        selectedCurrency.value =

            prefs.getString(
                KEY_CURRENCY,
                "INR"
            ) ?: "INR"
    }

    // =====================================================
    // SAVE CURRENCY
    // =====================================================

    fun saveCurrency(

        context: Context,

        currency: String
    ) {

        selectedCurrency.value =
            currency

        val prefs =

            context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )

        prefs.edit()

            .putString(
                KEY_CURRENCY,
                currency
            )

            .apply()
    }

    // =====================================================
    // SYMBOL
    // =====================================================

    fun symbol(): String {

        return when (
            selectedCurrency.value
        ) {

            "USD" -> "$"

            "EUR" -> "€"

            "GBP" -> "£"

            "JPY" -> "¥"

            else -> "₹"
        }
    }

    // =====================================================
    // INR -> SELECTED CURRENCY CONVERSION
    // =====================================================

    fun convertFromINR(
        amount: Double
    ): Double {

        return when (
            selectedCurrency.value
        ) {

            // Approximate live-ish rates

            "USD" -> amount * 0.012

            "EUR" -> amount * 0.011

            "GBP" -> amount * 0.0093

            "JPY" -> amount * 1.73

            else -> amount
        }
    }

    // =====================================================
    // FORMATTED DISPLAY
    // =====================================================

    fun format(
        amount: Double
    ): String {

        val converted =
            convertFromINR(amount)

        return "${symbol()}${"%.2f".format(converted)}"
    }
}