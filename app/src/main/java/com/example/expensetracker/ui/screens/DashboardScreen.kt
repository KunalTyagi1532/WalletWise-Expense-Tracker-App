package com.example.expensetracker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.ui.components.AddTransactionSheet
import com.example.expensetracker.ui.components.DashboardBudgetCard
import com.example.expensetracker.ui.components.SummaryCard
import com.example.expensetracker.ui.components.TransactionItem
import com.example.expensetracker.ui.viewmodel.ExpenseViewModel
import com.example.expensetracker.ui.viewmodel.ThemeViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ExpenseViewModel,
    themeViewModel: ThemeViewModel,
    onOpenSettings: () -> Unit
) {

    val currentUser =
        FirebaseAuth.getInstance().currentUser

    val userName =
        currentUser?.email
            ?.substringBefore("@")
            ?.replaceFirstChar {
                it.uppercase()
            }
            ?: "User"

    LaunchedEffect(Unit) {

        currentUser?.uid?.let { uid ->

            viewModel.fetchMonthlyBudget(uid)

            viewModel.fetchGoals(uid)
        }
    }

    // =====================================================
    // STATES
    // =====================================================

    val transactions by
    viewModel.transactions.collectAsState()

    val balance by
    viewModel.totalBalance.collectAsState()

    val income by
    viewModel.totalIncome.collectAsState()

    val expense by
    viewModel.totalExpense.collectAsState()

    val remainingBudget by
    viewModel.remainingBudget.collectAsState()

    val budgetProgress by
    viewModel.budgetProgress.collectAsState()

    val isBudgetExceeded by
    viewModel.isBudgetExceeded.collectAsState()

    val isLoading =
        viewModel.isLoading

    val uiMessage =
        viewModel.uiMessage

    val monthlyBudget =
        viewModel.monthlyBudget

    val totalBudget =
        monthlyBudget?.totalMonthlyLimit ?: 0.0

    // =====================================================
    // SHEETS
    // =====================================================

    val transactionSheetState =
        rememberModalBottomSheetState()

    var showTransactionSheet by remember {
        mutableStateOf(false)
    }

    var showBudgetSheet by remember {
        mutableStateOf(false)
    }

    var editingTransaction by remember {
        mutableStateOf<Transaction?>(null)
    }

    val snackbarHostState =
        remember {
            SnackbarHostState()
        }

    LaunchedEffect(uiMessage) {

        if (uiMessage != null) {

            snackbarHostState.showSnackbar(uiMessage)

            viewModel.clearUiMessage()
        }
    }

    val listState =
        rememberLazyListState()

    Scaffold(

        snackbarHost = {

            SnackbarHost(
                hostState = snackbarHostState
            )
        },

        topBar = {

            TopAppBar(

                title = {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),

                        verticalAlignment =
                            Alignment.CenterVertically,

                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Column {

                            Text(
                                text = "Welcome back,",

                                style =
                                    MaterialTheme.typography.labelMedium,

                                color =
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant
                            )

                            Text(
                                text = userName,

                                style =
                                    MaterialTheme.typography.titleLarge,

                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            // =====================================================
                            // THEME TOGGLE
                            // =====================================================

                            FilledIconButton(
                                onClick = {
                                    themeViewModel.toggleTheme()
                                }
                            ) {

                                Crossfade(
                                    targetState =
                                        themeViewModel.isDarkMode.value,

                                    label = "theme_icon"
                                ) { darkMode ->

                                    Icon(
                                        imageVector =
                                            if (darkMode)
                                                Icons.Default.LightMode
                                            else
                                                Icons.Default.DarkMode,

                                        contentDescription = null
                                    )
                                }
                            }

                            // =====================================================
                            // SETTINGS BUTTON
                            // =====================================================

                            FilledIconButton(

                                onClick = {
                                    onOpenSettings()
                                }
                            ) {

                                Icon(

                                    imageVector =
                                        Icons.Default.Settings,

                                    contentDescription = null
                                )
                            }
                        }
                    }
                },

                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
            )
        },

        floatingActionButton = {

            FloatingActionButton(

                onClick = {

                    editingTransaction = null
                    showTransactionSheet = true
                }

            ) {

                Text(
                    text = "+",

                    style =
                        MaterialTheme.typography.headlineMedium
                )
            }
        }

    ) { paddingValues ->

        AnimatedVisibility(
            visible = !isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {

            LazyColumn(

                state = listState,

                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),

                contentPadding = PaddingValues(
                    top = 6.dp,
                    bottom = 140.dp
                ),

                verticalArrangement =
                    Arrangement.spacedBy(18.dp)
            ) {

                item {

                    OutlinedTextField(

                        value = viewModel.searchQuery,

                        onValueChange = {
                            viewModel.updateSearchQuery(it)
                        },

                        leadingIcon = {

                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null
                            )
                        },

                        placeholder = {
                            Text("Search transactions...")
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),

                        singleLine = true,

                        shape = RoundedCornerShape(20.dp)
                    )
                }

                item {

                    SummaryCard(
                        balance = balance,
                        income = income,
                        expense = expense
                    )
                }

                item {

                    DashboardBudgetCard(

                        remaining = remainingBudget,

                        totalLimit = totalBudget,

                        progress = budgetProgress,

                        isExceeded = isBudgetExceeded,

                        onSetBudgetClick = {

                            showBudgetSheet = true
                        }
                    )
                }

                if (transactions.isEmpty()) {

                    item {

                        Text(
                            text = "No Transactions Yet",
                            modifier = Modifier.padding(24.dp)
                        )
                    }

                } else {

                    items(
                        items = transactions,
                        key = { it.id }
                    ) { transaction ->

                        TransactionItem(

                            transaction = transaction,

                            onEditClick = {

                                editingTransaction = transaction
                                showTransactionSheet = true
                            },

                            onDeleteClick = {

                                viewModel.deleteTransaction(
                                    transaction.id
                                )
                            },

                            onRecurringToggleClick = {

                                if (
                                    transaction.recurring
                                ) {

                                    viewModel.setRecurringEnabled(

                                        transaction = transaction,

                                        enabled =
                                            !transaction.recurringEnabled
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // =====================================================
    // TRANSACTION SHEET
    // =====================================================

    if (showTransactionSheet) {

        ModalBottomSheet(

            onDismissRequest = {

                showTransactionSheet = false
            },

            sheetState = transactionSheetState
        ) {

            AddTransactionSheet(

                transactionToEdit =
                    editingTransaction,

                onSave = { transaction ->

                    if (editingTransaction == null) {

                        viewModel.addTransaction(
                            transaction
                        )

                    } else {

                        viewModel.updateTransaction(
                            transaction
                        )
                    }

                    showTransactionSheet = false
                },

                onDismiss = {

                    showTransactionSheet = false
                }
            )
        }
    }

    // =====================================================
    // BUDGET SHEET
    // =====================================================

    if (showBudgetSheet) {

        ModalBottomSheet(

            onDismissRequest = {

                showBudgetSheet = false
            }

        ) {

            var budgetText by remember {

                mutableStateOf(
                    totalBudget.toInt().toString()
                )
            }

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),

                verticalArrangement =
                    Arrangement.spacedBy(16.dp)
            ) {

                Text(

                    text = "Set Monthly Budget",

                    style =
                        MaterialTheme.typography.headlineSmall,

                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(

                    value = budgetText,

                    onValueChange = {

                        budgetText = it
                    },

                    label = {

                        Text("Budget Amount")
                    },

                    singleLine = true,

                    modifier =
                        Modifier.fillMaxWidth()
                )

                Button(

                    onClick = {

                        val value =
                            budgetText.toDoubleOrNull()

                        if (value != null) {

                            currentUser?.uid?.let { uid ->

                                viewModel.saveBudget(
                                    value,
                                    uid
                                )
                            }

                            showBudgetSheet = false
                        }
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text("Save Budget")
                }
            }
        }
    }
}