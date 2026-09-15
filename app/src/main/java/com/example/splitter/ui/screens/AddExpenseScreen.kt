package com.example.splitter.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.splitter.data.model.User
import com.example.splitter.ui.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    initialGroupId: String? = null,
    expenseViewModel: ExpenseViewModel,
    onBackClick: () -> Unit,
    onExpenseSaved: () -> Unit
) {
    val groups by expenseViewModel.allGroups.collectAsState()
    val users by expenseViewModel.allUsers.collectAsState()

    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var selectedGroupId by remember { mutableStateOf(initialGroupId ?: groups.firstOrNull()?.id) }
    var selectedCategory by remember { mutableStateOf("Food") }
    var selectedPaidByUserId by remember { mutableStateOf(expenseViewModel.currentUserId) }

    var expandedGroup by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedPaidBy by remember { mutableStateOf(false) }

    var splitModeIndex by remember { mutableStateOf(0) } // 0 = Equal, 1 = Exact
    val categories = listOf("Food", "Transport", "Utilities", "Hotels", "Entertainment", "Groceries", "Shopping", "Other")

    // Available members for chosen group (or all users)
    val availableMembers = remember(selectedGroupId, groups, users) {
        if (selectedGroupId.isNullOrBlank()) {
            users
        } else {
            // Find group members or default to users
            users
        }
    }

    val exactSplits = remember { mutableStateMapOf<String, String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Expense", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Expense Title (e.g. Dinner, Uber)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Group Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedGroup,
                onExpandedChange = { expandedGroup = !expandedGroup }
            ) {
                val groupName = groups.find { it.id == selectedGroupId }?.name ?: "Non-group expense"
                OutlinedTextField(
                    value = groupName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Group") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGroup) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedGroup,
                    onDismissRequest = { expandedGroup = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("None (1-on-1)") },
                        onClick = {
                            selectedGroupId = null
                            expandedGroup = false
                        }
                    )
                    groups.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group.name) },
                            onClick = {
                                selectedGroupId = group.id
                                expandedGroup = false
                            }
                        )
                    }
                }
            }

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            // Paid By Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedPaidBy,
                onExpandedChange = { expandedPaidBy = !expandedPaidBy }
            ) {
                val paidByName = users.find { it.id == selectedPaidByUserId }?.name ?: "You"
                OutlinedTextField(
                    value = paidByName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Paid By") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPaidBy) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedPaidBy,
                    onDismissRequest = { expandedPaidBy = false }
                ) {
                    users.forEach { user ->
                        DropdownMenuItem(
                            text = { Text(user.name) },
                            onClick = {
                                selectedPaidByUserId = user.id
                                expandedPaidBy = false
                            }
                        )
                    }
                }
            }

            Text("Split Options", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = splitModeIndex == 0,
                    onClick = { splitModeIndex = 0 },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Split Equally")
                }
                SegmentedButton(
                    selected = splitModeIndex == 1,
                    onClick = { splitModeIndex = 1 },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("Exact Amounts")
                }
            }

            val totalAmount = amountText.toDoubleOrNull() ?: 0.0

            if (splitModeIndex == 0) {
                val memberCount = availableMembers.size.coerceAtLeast(1)
                val splitPerPerson = totalAmount / memberCount
                Text(
                    text = "Each person pays: ₹${String.format("%.2f", splitPerPerson)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableMembers.forEach { member ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(member.name, modifier = Modifier.weight(1f))
                            OutlinedTextField(
                                value = exactSplits[member.id] ?: "",
                                onValueChange = { exactSplits[member.id] = it },
                                label = { Text("₹") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.width(120.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val finalAmount = amountText.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && finalAmount > 0.0) {
                        val splitsMap = mutableMapOf<String, Double>()

                        if (splitModeIndex == 0) {
                            val perPerson = finalAmount / availableMembers.size
                            availableMembers.forEach { splitsMap[it.id] = perPerson }
                        } else {
                            availableMembers.forEach { member ->
                                val memberVal = exactSplits[member.id]?.toDoubleOrNull() ?: 0.0
                                splitsMap[member.id] = memberVal
                            }
                        }

                        expenseViewModel.addExpense(
                            groupId = selectedGroupId,
                            title = title,
                            amount = finalAmount,
                            paidByUserId = selectedPaidByUserId,
                            category = selectedCategory,
                            splits = splitsMap
                        )
                        onExpenseSaved()
                    }
                },
                enabled = title.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0.0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Expense", fontWeight = FontWeight.Bold)
            }
        }
    }
}
