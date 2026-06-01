package com.example.expensetracker.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result
import com.example.expensetracker.data.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

class RecurringTransaction(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        Log.d(
            "RECURRING",
            "Worker started"
        )

        val db =
            FirebaseFirestore.getInstance()

        val auth =
            FirebaseAuth.getInstance()

        val userId =
            auth.currentUser?.uid

        Log.d(
            "RECURRING",
            "User = $userId"
        )

        if (userId == null) {

            Log.d(
                "RECURRING",
                "User is null. Retrying later."
            )

            return Result.retry()
        }

        return try {

            val snapshot =
                db.collection("transactions")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("recurring", true)
                    .whereEqualTo("recurringEnabled",true)
                    .get()
                    .await()

            Log.d(
                "RECURRING",
                "Found ${snapshot.documents.size} recurring transactions"
            )

            val now =
                System.currentTimeMillis()

            for (doc in snapshot.documents) {

                val original =
                    doc.toObject(Transaction::class.java)
                        ?: continue

                Log.d(
                    "RECURRING",
                    "Checking ${original.note} | ${original.recurringInterval}"
                )

                val shouldGenerate =
                    shouldGenerateRecurring(
                        transaction = original,
                        now = now
                    )

                Log.d(
                    "RECURRING",
                    "Should Generate = $shouldGenerate"
                )

                if (!shouldGenerate) continue

                val alreadyGenerated =
                    hasTransactionAlreadyGeneratedToday(
                        db = db,
                        original = original,
                        userId = userId
                    )

                Log.d(
                    "RECURRING",
                    "Already Generated = $alreadyGenerated"
                )

                if (alreadyGenerated) continue

                val newTransaction =
                    original.copy(

                        id =
                            UUID.randomUUID()
                                .toString(),

                        timestamp =
                            now,

                        // Generated transaction should not
                        // generate more recurring transactions

                        recurring =
                            false,

                        // Keep Daily / Weekly / Monthly
                        // for display in transaction history

                        recurringInterval =
                            original.recurringInterval,

                        lastRecurringGenerated =
                            now
                    )

                db.collection("transactions")
                    .document(newTransaction.id)
                    .set(newTransaction)
                    .await()

                db.collection("transactions")
                    .document(original.id)
                    .update(
                        "lastRecurringGenerated",
                        now
                    )
                    .await()

                Log.d(
                    "RECURRING",
                    "Generated transaction ${newTransaction.id}"
                )
            }

            Result.success()

        } catch (e: Exception) {

            Log.e(
                "RECURRING",
                "Worker crashed",
                e
            )

            Result.retry()
        }
    }

    private fun shouldGenerateRecurring(
        transaction: Transaction,
        now: Long
    ): Boolean {

        val lastGenerated =
            transaction.lastRecurringGenerated

        val diff =
            now - lastGenerated

        val oneDay =
            24 * 60 * 60 * 1000L

        return when (
            transaction.recurringInterval
        ) {

            "Daily" -> {
                diff >= oneDay
            }

            "Weekly" -> {
                diff >= oneDay * 7
            }

            "Monthly" -> {
                diff >= oneDay * 30
            }

            "Yearly" -> {
                diff >= oneDay * 365
            }

            else -> false
        }
    }

    private suspend fun hasTransactionAlreadyGeneratedToday(
        db: FirebaseFirestore,
        original: Transaction,
        userId: String
    ): Boolean {

        val startOfDay =
            getStartOfToday()

        val endOfDay =
            getEndOfToday()

        val snapshot =
            db.collection("transactions")
                .whereEqualTo(
                    "userId",
                    userId
                )
                .whereEqualTo(
                    "note",
                    original.note
                )
                .whereEqualTo(
                    "amount",
                    original.amount
                )
                .whereEqualTo(
                    "category",
                    original.category
                )
                .whereGreaterThanOrEqualTo(
                    "timestamp",
                    startOfDay
                )
                .whereLessThanOrEqualTo(
                    "timestamp",
                    endOfDay
                )
                .get()
                .await()

        return snapshot.documents.isNotEmpty()
    }

    private fun getStartOfToday(): Long {

        return Calendar.getInstance().apply {

            set(
                Calendar.HOUR_OF_DAY,
                0
            )

            set(
                Calendar.MINUTE,
                0
            )

            set(
                Calendar.SECOND,
                0
            )

            set(
                Calendar.MILLISECOND,
                0
            )

        }.timeInMillis
    }

    private fun getEndOfToday(): Long {

        return Calendar.getInstance().apply {

            set(
                Calendar.HOUR_OF_DAY,
                23
            )

            set(
                Calendar.MINUTE,
                59
            )

            set(
                Calendar.SECOND,
                59
            )

            set(
                Calendar.MILLISECOND,
                999
            )

        }.timeInMillis
    }
}