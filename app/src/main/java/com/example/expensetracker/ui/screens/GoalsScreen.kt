package com.example.expensetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.ui.viewmodel.ExpenseViewModel
import com.example.expensetracker.utils.CurrencyManager
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    viewModel: ExpenseViewModel
) {

    val currentUser =
        FirebaseAuth.getInstance().currentUser

    val goals by
    viewModel.goals.collectAsState()

    LaunchedEffect(Unit) {

        currentUser?.uid?.let {

            viewModel.fetchGoals(it)
        }
    }

    var showSheet by remember {
        mutableStateOf(false)
    }

    var goalName by remember {
        mutableStateOf("")
    }

    var goalTarget by remember {
        mutableStateOf("")
    }

    // =====================================================
    // ADD MONEY STATES
    // =====================================================

    var showAddMoneySheet by remember {
        mutableStateOf(false)
    }

    var selectedGoal by remember {
        mutableStateOf<SavingsGoal?>(null)
    }

    var contributionAmount by remember {
        mutableStateOf("")
    }

    val sheetState =
        rememberModalBottomSheetState()

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(

                        text = "Savings Goals",

                        style =
                            MaterialTheme.typography.headlineSmall,

                        fontWeight = FontWeight.ExtraBold
                    )
                }
            )
        },

        floatingActionButton = {

            FloatingActionButton(

                onClick = {
                    showSheet = true
                }

            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }

    ) { paddingValues ->

        // =====================================================
        // ADD GOAL SHEET
        // =====================================================

        if (showSheet) {

            ModalBottomSheet(

                onDismissRequest = {
                    showSheet = false
                },

                sheetState = sheetState

            ) {

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {

                    Text(

                        text = "Add Goal",

                        style =
                            MaterialTheme.typography
                                .headlineSmall,

                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    OutlinedTextField(

                        value = goalName,

                        onValueChange = {
                            goalName = it
                        },

                        label = {
                            Text("Goal Name")
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(18.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    OutlinedTextField(

                        value = goalTarget,

                        onValueChange = {
                            goalTarget = it
                        },

                        label = {
                            Text("Target Amount")
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(18.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    // =====================================================
                    // LIVE PREVIEW
                    // =====================================================

                    if (goalTarget.toDoubleOrNull() != null) {

                        Text(

                            text =
                                "Preview: ${
                                    CurrencyManager.format(
                                        goalTarget.toDouble()
                                    )
                                }",

                            style =
                                MaterialTheme.typography.bodyMedium,

                            color =
                                MaterialTheme.colorScheme.primary,

                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )

                    Button(

                        onClick = {

                            val target =
                                goalTarget.toDoubleOrNull()
                                    ?: 0.0

                            if (
                                goalName.isNotBlank()
                                && target > 0
                            ) {

                                currentUser?.uid?.let { uid ->

                                    viewModel.addGoal(

                                        SavingsGoal(

                                            title = goalName,

                                            saved = 0.0,

                                            target = target,

                                            userId = uid
                                        )
                                    )
                                }

                                goalName = ""
                                goalTarget = ""

                                showSheet = false
                            }
                        },

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text("Create Goal")
                    }

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }

        // =====================================================
        // ADD MONEY SHEET
        // =====================================================

        if (showAddMoneySheet) {

            ModalBottomSheet(

                onDismissRequest = {
                    showAddMoneySheet = false
                }

            ) {

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {

                    Text(

                        text = "Add Money",

                        style =
                            MaterialTheme.typography
                                .headlineSmall,

                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    OutlinedTextField(

                        value = contributionAmount,

                        onValueChange = {
                            contributionAmount = it
                        },

                        label = {
                            Text("Amount")
                        },

                        modifier =
                            Modifier.fillMaxWidth()
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    if (contributionAmount.toDoubleOrNull() != null) {

                        Text(

                            text =
                                "Preview: ${
                                    CurrencyManager.format(
                                        contributionAmount.toDouble()
                                    )
                                }",

                            style =
                                MaterialTheme.typography.bodyMedium,

                            color =
                                MaterialTheme.colorScheme.primary,

                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )

                    Button(

                        onClick = {

                            val amount =
                                contributionAmount
                                    .toDoubleOrNull()
                                    ?: 0.0

                            val goal =
                                selectedGoal

                            if (
                                goal != null &&
                                amount > 0
                            ) {

                                viewModel.addMoneyToGoal(

                                    goalId = goal.id,

                                    currentSaved = goal.saved,

                                    amountToAdd = amount
                                )

                                contributionAmount = ""

                                showAddMoneySheet = false
                            }
                        },

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text("Add")
                    }

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }

        // =====================================================
        // EMPTY STATE
        // =====================================================

        if (goals.isEmpty()) {

            Box(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),

                contentAlignment = Alignment.Center
            ) {

                Column(

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Box(

                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme
                                    .primaryContainer
                            ),

                        contentAlignment = Alignment.Center
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Flag,

                            contentDescription = null,

                            modifier = Modifier.size(42.dp),

                            tint =
                                MaterialTheme.colorScheme
                                    .primary
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(22.dp)
                    )

                    Text(

                        text = "No Goals Yet",

                        style =
                            MaterialTheme.typography
                                .headlineSmall,

                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(

                        text =
                            "Create savings goals and track your progress.",

                        style =
                            MaterialTheme.typography
                                .bodyMedium,

                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )

                    Spacer(
                        modifier = Modifier.height(28.dp)
                    )

                    Button(

                        onClick = {
                            showSheet = true
                        }

                    ) {

                        Text("Create Goal")
                    }
                }
            }

        } else {

            LazyColumn(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),

                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 16.dp
                ),

                verticalArrangement =
                    Arrangement.spacedBy(18.dp)
            ) {

                items(goals) { goal ->

                    GoalCard(

                        goal = goal,

                        onDelete = {

                            viewModel.deleteGoal(
                                goal.id
                            )
                        },

                        onAddMoney = {

                            selectedGoal = goal

                            showAddMoneySheet = true
                        }
                    )
                }

                item {

                    Spacer(
                        modifier = Modifier.height(120.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalCard(

    goal: SavingsGoal,

    onDelete: () -> Unit,

    onAddMoney: () -> Unit
) {

    val progress =
        (goal.saved / goal.target)
            .toFloat()
            .coerceIn(0f, 1f)

    val remaining =
        goal.target - goal.saved

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(30.dp),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {

        Box(

            modifier = Modifier
                .fillMaxWidth()
                .background(

                    brush = Brush.linearGradient(

                        colors = listOf(
                            Color(0xFF5C6BC0),
                            Color(0xFF7986CB)
                        )
                    )
                )
                .padding(22.dp)
        ) {

            Column {

                Row(

                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(

                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    Color.White.copy(alpha = 0.18f)
                                ),

                            contentAlignment = Alignment.Center
                        ) {

                            Icon(

                                imageVector =
                                    Icons.Default.Flag,

                                contentDescription = null,

                                tint = Color.White
                            )
                        }

                        Spacer(
                            modifier = Modifier.width(14.dp)
                        )

                        Column {

                            Text(

                                text = goal.title,

                                style =
                                    MaterialTheme.typography
                                        .titleLarge,

                                fontWeight = FontWeight.ExtraBold,

                                color = Color.White
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(

                                text =
                                    "${CurrencyManager.format(goal.saved)} / ${
                                        CurrencyManager.format(goal.target)
                                    }",

                                color =
                                    Color.White.copy(alpha = 0.92f)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDelete
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Delete,

                            contentDescription = null,

                            tint = Color.White
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(22.dp)
                )

                LinearProgressIndicator(

                    progress = { progress },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(CircleShape),

                    color = Color.White,

                    trackColor =
                        Color.White.copy(alpha = 0.22f)
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(

                    text =
                        "${CurrencyManager.format(remaining)} remaining",

                    style =
                        MaterialTheme.typography.bodyMedium,

                    color =
                        Color.White.copy(alpha = 0.92f)
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Button(

                    onClick = onAddMoney,

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    )

                ) {

                    Text(

                        text = "Add Money",

                        color = Color(0xFF5C6BC0),

                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}