package com.example.expensetracker.worker

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.expensetracker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
import com.example.expensetracker.utils.CurrencyManager

class DailyFinance(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val db =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()

    private val prefs: SharedPreferences =
        applicationContext.getSharedPreferences(
            "finance_notifications",
            Context.MODE_PRIVATE
        )

    override suspend fun doWork(): Result {

        val userId =
            auth.currentUser?.uid
                ?: return Result.success()

        return try {

            val budgetSnapshot =
                db.collection("budgets")
                    .document(userId)
                    .get()
                    .await()

            val totalLimit =
                budgetSnapshot
                    .getDouble("totalMonthlyLimit")
                    ?: 0.0

            val transactionSnapshot =
                db.collection("transactions")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

            val transactions =
                transactionSnapshot.documents

            val currentMonth =
                Calendar.getInstance()
                    .get(Calendar.MONTH)

            val monthlyExpense =
                transactions.sumOf { doc ->

                    val type =
                        doc.getString("type") ?: ""

                    val amount =
                        doc.getDouble("amount") ?: 0.0

                    val timestamp =
                        doc.getLong("timestamp") ?: 0L

                    val calendar =
                        Calendar.getInstance().apply {
                            timeInMillis = timestamp
                        }

                    val month =
                        calendar.get(Calendar.MONTH)

                    if (
                        type == "Expense" &&
                        month == currentMonth
                    ) {
                        amount
                    } else {
                        0.0
                    }
                }

            val todayTransactions =
                transactions.filter { doc ->

                    val timestamp =
                        doc.getLong("timestamp") ?: 0L

                    isToday(timestamp)
                }

            val progress =
                if (totalLimit > 0) {
                    monthlyExpense / totalLimit
                } else {
                    0.0
                }

            when {

                progress >= 1.0 &&
                        !prefs.getBoolean("sent_exceeded", false) -> {

                    sendNotification(

                        title = "Budget Exceeded",

                        message =
                            "🚨 You've exceeded your monthly budget."
                    )

                    prefs.edit()
                        .putBoolean("sent_exceeded", true)
                        .apply()
                }

                progress >= 0.9 &&
                        !prefs.getBoolean("sent_90", false) -> {

                    sendNotification(

                        title = "Budget Warning",

                        message =
                            "⚠️ You've used 90% of your monthly budget."
                    )

                    prefs.edit()
                        .putBoolean("sent_90", true)
                        .apply()
                }

                progress >= 0.8 &&
                        !prefs.getBoolean("sent_80", false) -> {

                    sendNotification(

                        title = "Budget Warning",

                        message =
                            "⚠️ You've used 80% of your monthly budget."
                    )

                    prefs.edit()
                        .putBoolean("sent_80", true)
                        .apply()
                }

                todayTransactions.isEmpty() -> {

                    sendNotification(

                        title = "No Transactions Logged",

                        message =
                            "💡 You didn’t log any transactions today."
                    )
                }
            }

            Result.success()

        } catch (e: Exception) {

            Result.retry()
        }
    }

    private fun sendNotification(
        title: String,
        message: String
    ) {

        if (
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val builder =
            NotificationCompat.Builder(
                applicationContext,
                "finance_alerts"
            )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

        val manager =
            applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        manager.notify(
            Random().nextInt(),
            builder.build()
        )
    }

    private fun isToday(
        timestamp: Long
    ): Boolean {

        val now =
            Calendar.getInstance()

        val calendar =
            Calendar.getInstance().apply {
                timeInMillis = timestamp
            }

        return now.get(Calendar.YEAR) ==
                calendar.get(Calendar.YEAR) &&

                now.get(Calendar.DAY_OF_YEAR) ==
                calendar.get(Calendar.DAY_OF_YEAR)
    }
}