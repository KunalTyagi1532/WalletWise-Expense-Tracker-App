package com.example.expensetracker.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.repository.ExpenseRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import com.example.expensetracker.utils.CurrencyManager

class ExpenseViewModel : ViewModel() {

    private val repository = ExpenseRepository()

    private val db = FirebaseFirestore.getInstance()

    // =====================================================
    // UI STATE
    // =====================================================

    var isLoading by mutableStateOf(true)
        private set

    var uiMessage by mutableStateOf<String?>(null)
        private set

    // =====================================================
    // FILTER / SEARCH STATE
    // =====================================================

    var searchQuery by mutableStateOf("")
        private set

    var selectedCategory by mutableStateOf("All")
        private set

    // =====================================================
    // DATE FILTER SYSTEM
    // =====================================================

    private val _selectedStartDate =
        MutableStateFlow<Long?>(null)

    val selectedStartDate =
        _selectedStartDate.asStateFlow()

    private val _selectedEndDate =
        MutableStateFlow<Long?>(null)

    val selectedEndDate =
        _selectedEndDate.asStateFlow()

    // =====================================================
    // GOALS
    // =====================================================

    private val _goals =
        MutableStateFlow<List<SavingsGoal>>(emptyList())

    val goals: StateFlow<List<SavingsGoal>> =
        _goals.asStateFlow()

    // =====================================================
    // TRANSACTIONS
    // =====================================================

    val transactions: StateFlow<List<Transaction>> =
        repository.getTransactions()
            .onEach {
                isLoading = false
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList()
            )

    // =====================================================
    // FILTERED TRANSACTIONS
    // =====================================================

    val filteredTransactions:
            StateFlow<List<Transaction>> =

        combine(

            transactions,

            snapshotFlow { searchQuery },

            snapshotFlow { selectedCategory },

            _selectedStartDate,

            _selectedEndDate

        ) {

                list,
                query,
                category,
                startDate,
                endDate ->

            list.filter { transaction ->

                // =====================================================
                // SEARCH FILTER
                // =====================================================

                val matchesSearch =

                    transaction.note.contains(
                        query,
                        ignoreCase = true
                    ) ||

                            transaction.category.contains(
                                query,
                                ignoreCase = true
                            )

                // =====================================================
                // CATEGORY FILTER
                // =====================================================

                val matchesCategory =

                    category == "All" ||

                            transaction.category == category

                // =====================================================
                // DATE FILTER
                // =====================================================

                val matchesDate =

                    if (
                        startDate != null &&
                        endDate != null
                    ) {

                        transaction.timestamp in
                                startDate..endDate

                    } else {

                        true
                    }

                matchesSearch &&
                        matchesCategory &&
                        matchesDate
            }

        }.stateIn(

            scope = viewModelScope,

            started =
                SharingStarted.WhileSubscribed(5000),

            initialValue = emptyList()
        )

    // =====================================================
    // BUDGET STATE
    // =====================================================

    var monthlyBudget by mutableStateOf<Budget?>(null)
        private set

    // =====================================================
    // FINANCIAL CALCULATIONS
    // =====================================================

    val totalBalance: StateFlow<Double> =
        transactions.map { list ->

            list.sumOf {

                if (it.type == "Income") {
                    it.amount
                } else {
                    -it.amount
                }
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0.0
        )

    val totalIncome: StateFlow<Double> =
        transactions.map { list ->

            list.filter {
                it.type == "Income"
            }.sumOf {
                it.amount
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0.0
        )

    val totalExpense: StateFlow<Double> =
        transactions.map { list ->

            val currentMonth =
                getCurrentMonthKey()

            list.filter {

                it.type == "Expense" &&
                        getMonthFromTimestamp(
                            it.timestamp
                        ) == currentMonth

            }.sumOf {
                it.amount
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0.0
        )

    // =====================================================
    // CATEGORY ANALYTICS
    // =====================================================

    val categoryBreakdown: StateFlow<Map<String, Double>> =
        transactions.map { list ->

            val currentMonth =
                getCurrentMonthKey()

            list.filter {

                it.type == "Expense" &&
                        getMonthFromTimestamp(
                            it.timestamp
                        ) == currentMonth

            }.groupBy {
                it.category
            }.mapValues { entry ->

                entry.value.sumOf {
                    it.amount
                }
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyMap()
        )

    // =====================================================
    // BUDGET ANALYTICS
    // =====================================================

    val remainingBudget: StateFlow<Double> =
        totalExpense.combine(
            snapshotFlow { monthlyBudget }
        ) { spent, budget ->

            val limit =
                budget?.totalMonthlyLimit ?: 0.0

            limit - spent

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0.0
        )

    val budgetProgress: StateFlow<Float> =
        totalExpense.combine(
            snapshotFlow { monthlyBudget }
        ) { spent, budget ->

            val limit =
                budget?.totalMonthlyLimit ?: 0.0

            if (limit <= 0) {

                0f

            } else {

                (spent / limit)
                    .toFloat()
                    .coerceIn(0f, 1f)
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0f
        )

    val isBudgetExceeded: StateFlow<Boolean> =
        remainingBudget.map {

            it < 0

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    // =====================================================
    // BUDGET WARNING LEVEL
    // =====================================================

    val budgetWarningLevel: StateFlow<String> =
        budgetProgress.map { progress ->

            when {

                progress >= 0.85f -> "DANGER"

                progress >= 0.60f -> "WARNING"

                else -> "SAFE"
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "SAFE"
        )

    // =====================================================
    // FIRESTORE GOALS
    // =====================================================

    fun addMoneyToGoal(

        goalId: String,

        currentSaved: Double,

        amountToAdd: Double
    ) {

        viewModelScope.launch {

            try {

                val updatedAmount =
                    currentSaved + amountToAdd

                db.collection("goals")
                    .document(goalId)
                    .update(
                        "saved",
                        updatedAmount
                    )
                    .await()

                uiMessage =
                    "Money added to goal"

            } catch (e: Exception) {

                uiMessage =
                    "Failed to update goal"
            }
        }
    }

    fun addGoal(
        goal: SavingsGoal
    ) {

        viewModelScope.launch {

            try {

                val document =
                    db.collection("goals")
                        .document()

                val newGoal =
                    goal.copy(
                        id = document.id
                    )

                document.set(newGoal)
                    .await()

                uiMessage =
                    "Goal added successfully"

            } catch (e: Exception) {

                uiMessage =
                    "Failed to save goal"
            }
        }
    }

    fun fetchGoals(
        userId: String
    ) {

        db.collection("goals")
            .whereEqualTo(
                "userId",
                userId
            )

            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null) {

                    _goals.value =

                        snapshot.documents.mapNotNull {

                            it.toObject(
                                SavingsGoal::class.java
                            )
                        }
                }
            }
    }

    fun deleteGoal(
        goalId: String
    ) {

        viewModelScope.launch {

            db.collection("goals")
                .document(goalId)
                .delete()
                .await()
        }
    }

    // =====================================================
    // FIRESTORE BUDGET METHODS
    // =====================================================

    fun fetchMonthlyBudget(
        userId: String
    ) {

        if (userId.isEmpty()) return

        db.collection("budgets")
            .whereEqualTo(
                "userId",
                userId
            )
            .limit(1)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {

                    Log.e(
                        "Budget",
                        "Listen failed",
                        error
                    )

                    uiMessage =
                        "Failed to load budget"

                    return@addSnapshotListener
                }

                if (
                    snapshot != null &&
                    !snapshot.isEmpty
                ) {

                    monthlyBudget =
                        snapshot.documents[0]
                            .toObject(
                                Budget::class.java
                            )

                } else {

                    monthlyBudget = null
                }
            }
    }

    fun saveBudget(
        limit: Double,
        userId: String
    ) {

        val budgetId =
            monthlyBudget?.id
                ?: UUID.randomUUID().toString()

        val budget = Budget(
            id = budgetId,
            userId = userId,
            totalMonthlyLimit = limit
        )

        viewModelScope.launch {

            try {

                db.collection("budgets")
                    .document(budgetId)
                    .set(budget)
                    .await()

                monthlyBudget = budget

                uiMessage =
                    "Budget updated successfully"

            } catch (e: Exception) {

                Log.e(
                    "Budget",
                    "Error saving budget: ${e.message}"
                )

                uiMessage =
                    "Failed to save budget"
            }
        }
    }

    // =====================================================
    // TRANSACTION ACTIONS
    // =====================================================

    fun addTransaction(
        transaction: Transaction
    ) {

        repository.addTransaction(
            transaction
        ) { success ->

            uiMessage =
                if (success) {
                    "Transaction added"
                } else {
                    "Failed to add transaction"
                }
        }
    }

    fun deleteTransaction(
        transactionId: String
    ) {

        viewModelScope.launch {

            repository.deleteTransaction(
                transactionId
            ) { success ->

                if (success) {

                    uiMessage =
                        "Transaction deleted"

                } else {

                    Log.e(
                        "Delete",
                        "Failed to delete"
                    )

                    uiMessage =
                        "Failed to delete transaction"
                }
            }
        }
    }

    fun updateTransaction(
        transaction: Transaction
    ) {

        viewModelScope.launch {

            repository.updateTransaction(
                transaction
            ) { success ->

                if (success) {

                    uiMessage =
                        "Transaction updated"

                } else {

                    Log.e(
                        "Update",
                        "Failed to update"
                    )

                    uiMessage =
                        "Failed to update transaction"
                }
            }
        }
    }

    fun setRecurringEnabled(
        transaction: Transaction,
        enabled: Boolean
    ) {

        viewModelScope.launch {

            repository.updateTransaction(

                transaction.copy(

                    recurringEnabled = enabled,

                    updatedAt =
                        System.currentTimeMillis()
                )

            ) { success ->

                uiMessage =

                    if (success) {

                        if (enabled) {

                            "Recurring transaction resumed"

                        } else {

                            "Recurring transaction paused"
                        }

                    } else {

                        "Failed to update recurring transaction"
                    }
            }
        }
    }

    // =====================================================
    // SEARCH / FILTER
    // =====================================================

    fun updateSearchQuery(
        query: String
    ) {
        searchQuery = query
    }

    fun updateSelectedCategory(
        category: String
    ) {
        selectedCategory = category
    }

    // =====================================================
    // DATE FILTER FUNCTIONS
    // =====================================================

    fun setDateRange(

        start: Long?,

        end: Long?
    ) {

        _selectedStartDate.value = start

        _selectedEndDate.value = end
    }

    fun clearDateFilter() {

        _selectedStartDate.value = null

        _selectedEndDate.value = null
    }

    fun clearUiMessage() {
        uiMessage = null
    }

    // =====================================================
    // DATE HELPERS
    // =====================================================

    private fun getCurrentMonthKey(): String =
        SimpleDateFormat(
            "MM-yyyy",
            Locale.US
        ).format(Date())

    private fun getMonthFromTimestamp(
        ts: Long
    ): String =
        SimpleDateFormat(
            "MM-yyyy",
            Locale.US
        ).format(Date(ts))
}