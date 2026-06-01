package com.example.expensetracker.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.example.expensetracker.utils.CurrencyManager

fun openNotificationAccessSettings(
    context: Context
) {

    val intent = Intent(
        Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
    )

    context.startActivity(intent)
}