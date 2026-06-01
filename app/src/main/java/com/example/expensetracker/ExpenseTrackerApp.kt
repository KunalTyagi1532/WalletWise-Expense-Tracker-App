package com.example.expensetracker

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.example.expensetracker.utils.CurrencyManager

class ExpenseTrackerApp : Application() {

    override fun onCreate() {

        super.onCreate()

        val firestore =
            FirebaseFirestore.getInstance()

        val settings =

            FirebaseFirestoreSettings.Builder()

                .setPersistenceEnabled(true)

                .build()

        firestore.firestoreSettings =
            settings
    }
}