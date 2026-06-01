package com.example.expensetracker.data.repository

import com.example.expensetracker.data.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.example.expensetracker.utils.CurrencyManager

class ExpenseRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val transactionCollection = firestore.collection("transactions")

    fun getTransactions(): Flow<List<Transaction>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val query = transactionCollection
            .whereEqualTo("userId", uid) // Filter by current user
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("Firestore Error: ${error.message}")
                return@addSnapshotListener
            }

            val docs = snapshot?.toObjects(Transaction::class.java) ?: emptyList()
            trySend(docs) // Pushes new data to the Flow instantly
        }

        awaitClose { subscription.remove() } // Cleans up to prevent memory leaks
    }

    fun addTransaction(transaction: Transaction, onComplete: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        val docRef = transactionCollection.document()

        // Ensure the transaction is 'tagged' with the current user's ID
        val finalData = transaction.copy(id = docRef.id, userId = uid)

        docRef.set(finalData)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
    fun deleteTransaction(transactionId: String, onComplete: (Boolean) -> Unit) {
        transactionCollection.document(transactionId)
            .delete()
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { e ->
                println("Delete Error: ${e.message}")
                onComplete(false)
            }
    }

    fun updateTransaction(transaction: Transaction, onComplete: (Boolean) -> Unit) {
        if (transaction.id.isEmpty()) return
        transactionCollection.document(transaction.id)
            .set(transaction)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}