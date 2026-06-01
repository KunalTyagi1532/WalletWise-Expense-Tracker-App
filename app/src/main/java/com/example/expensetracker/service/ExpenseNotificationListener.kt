package com.example.expensetracker.service

import android.app.PendingIntent
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.expensetracker.MainActivity
import com.example.expensetracker.R
import android.Manifest

class ExpenseNotificationListener :
    NotificationListenerService() {

    // =====================================================
    // DUPLICATE PROTECTION
    // =====================================================

    private var lastNotificationText = ""

    private var lastTimestamp = 0L

    override fun onNotificationPosted(
        sbn: StatusBarNotification?
    ) {

        if (sbn == null) return

        val packageName =
            sbn.packageName ?: return

        // =====================================================
        // SUPPORTED UPI APPS
        // =====================================================

        val supportedApps = listOf(

            // Google Pay
            "com.google.android.apps.nbu.paisa.user",

            // PhonePe
            "com.phonepe.app",

            // Paytm
            "net.one97.paytm",

            // BHIM
            "in.org.npci.upiapp",

            // Amazon Pay
            "in.amazon.mShop.android.shopping"
        )

        if (!supportedApps.contains(packageName)) {
            return
        }

        val extras =
            sbn.notification.extras

        val title =
            extras.getString("android.title")
                ?: ""

        val text =
            extras.getCharSequence("android.text")
                ?.toString()
                ?: ""

        val subText =
            extras.getCharSequence("android.subText")
                ?.toString()
                ?: ""

        val fullText =
            "$title $text $subText"

        Log.d(
            "UPI_DEBUG",
            "FULL = $fullText"
        )

        // =====================================================
        // DUPLICATE PROTECTION
        // =====================================================

        val now =
            System.currentTimeMillis()

        if (
            fullText == lastNotificationText &&
            now - lastTimestamp < 5000
        ) {

            Log.d(
                "UPI_AUTO_TRACK",
                "Duplicate skipped"
            )

            return
        }

        lastNotificationText = fullText

        lastTimestamp = now

        // =====================================================
        // NORMALIZE TEXT
        // =====================================================

        val lowerText =
            fullText.lowercase()

        // =====================================================
        // PAYMENT KEYWORDS
        // =====================================================

        val expenseKeywords = listOf(

            "paid",
            "sent",
            "debited",
            "upi",
            "transfer",
            "transaction",
            "spent",
            "payment successful",
            "money sent"
        )

        val incomeKeywords = listOf(

            "received",
            "credited",
            "money received",
            "cashback",
            "refund"
        )

        val isExpense =
            expenseKeywords.any {

                lowerText.contains(it)
            }

        val isIncome =
            incomeKeywords.any {

                lowerText.contains(it)
            }

        if (!isExpense && !isIncome) {
            return
        }

        // =====================================================
        // AMOUNT DETECTION
        // =====================================================

        val amountRegex =

            Regex(
                """(?:₹|rs\.?|inr)\s?([0-9,]+(?:\.[0-9]{1,2})?)""",
                RegexOption.IGNORE_CASE
            )

        val amountMatch =
            amountRegex.find(fullText)

        val amount =

            amountMatch
                ?.groupValues
                ?.getOrNull(1)
                ?.replace(",", "")
                ?.toDoubleOrNull()
                ?: 0.0

        if (amount <= 0) {

            Log.d(
                "UPI_AUTO_TRACK",
                "Amount not detected"
            )

            return
        }

        // =====================================================
        // TRANSACTION TYPE
        // =====================================================

        val transactionType =

            if (isIncome)
                "Income"
            else
                "Expense"

        // =====================================================
        // MERCHANT / PERSON EXTRACTION
        // =====================================================

        var merchant = "UPI Payment"

        val merchantPatterns = listOf(

            Regex("""paid to\s([A-Za-z0-9\s]+)"""),

            Regex("""to\s([A-Za-z0-9\s]+)"""),

            Regex("""from\s([A-Za-z0-9\s]+)"""),

            Regex("""sent to\s([A-Za-z0-9\s]+)"""),

            Regex("""received from\s([A-Za-z0-9\s]+)""")
        )

        for (pattern in merchantPatterns) {

            val match =
                pattern.find(lowerText)

            if (match != null) {

                merchant =
                    match.groupValues[1]
                        .trim()
                        .replaceFirstChar {
                            it.uppercase()
                        }

                break
            }
        }

        // =====================================================
        // CATEGORY DETECTION
        // =====================================================

        val category =

            when {

                merchant.contains(
                    "swiggy",
                    ignoreCase = true
                ) -> "Food"

                merchant.contains(
                    "zomato",
                    ignoreCase = true
                ) -> "Food"

                merchant.contains(
                    "uber",
                    ignoreCase = true
                ) -> "Transport"

                merchant.contains(
                    "ola",
                    ignoreCase = true
                ) -> "Transport"

                merchant.contains(
                    "amazon",
                    ignoreCase = true
                ) -> "Shopping"

                merchant.contains(
                    "flipkart",
                    ignoreCase = true
                ) -> "Shopping"

                merchant.contains(
                    "netflix",
                    ignoreCase = true
                ) -> "Entertainment"

                merchant.contains(
                    "spotify",
                    ignoreCase = true
                ) -> "Entertainment"

                isIncome -> "Income"

                else -> "Other"
            }

        // =====================================================
        // OPEN APP INTENT
        // =====================================================

        val intent = Intent(

            this,
            MainActivity::class.java
        ).apply {

            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            putExtra(
                "detected_amount",
                amount
            )

            putExtra(
                "detected_merchant",
                merchant
            )

            putExtra(
                "detected_category",
                category
            )

            putExtra(
                "detected_type",
                transactionType
            )

            putExtra(
                "open_add_transaction",
                true
            )
        }

        val pendingIntent =

            PendingIntent.getActivity(

                this,

                amount.toInt(),

                intent,

                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        // =====================================================
        // SHOW NOTIFICATION
        // =====================================================

        val notification =

            NotificationCompat.Builder(
                this,
                "finance_alerts"
            )

                .setSmallIcon(
                    R.mipmap.ic_launcher
                )

                .setContentTitle(
                    "UPI Transaction Detected"
                )

                .setContentText(
                    "Detected ₹$amount paid to $merchant"
                )

                .setStyle(

                    NotificationCompat
                        .BigTextStyle()

                        .bigText(
                            "Detected ₹$amount paid to $merchant.\nTap to review and add transaction."
                        )
                )

                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )

                .setAutoCancel(true)

                .setContentIntent(
                    pendingIntent
                )

                .build()

        try {

            NotificationManagerCompat
                .from(this)
                .notify(
                    amount.toInt(),
                    notification
                )

        } catch (e: SecurityException) {

            Log.e(
                "UPI_NOTIFY",
                "Notification permission denied"
            )
        }
    }
}