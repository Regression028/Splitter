package com.example.splitter.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.splitter.ui.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettleUpScreen(
    initialGroupId: String? = null,
    expenseViewModel: ExpenseViewModel,
    onBackClick: () -> Unit,
    onSettlementSaved: () -> Unit
) {
    val groups by expenseViewModel.allGroups.collectAsState()
    val users by expenseViewModel.allUsers.collectAsState()

    var selectedGroupId by remember { mutableStateOf(initialGroupId ?: groups.firstOrNull()?.id) }
    var payerId by remember { mutableStateOf(users.find { it.id != expenseViewModel.currentUserId }?.id ?: "") }
    var payeeId by remember { mutableStateOf(expenseViewModel.currentUserId) }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("Paid via GPay / UPI") }

    var expandedGroup by remember { mutableStateOf(false) }
    var expandedPayer by remember { mutableStateOf(false) }
    var expandedPayee by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record Settlement", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Group Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedGroup,
                onExpandedChange = { expandedGroup = !expandedGroup }
            ) {
                val groupName = groups.find { it.id == selectedGroupId }?.name ?: "Direct (No Group)"
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
                        text = { Text("Direct (No Group)") },
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

            // Payer Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedPayer,
                onExpandedChange = { expandedPayer = !expandedPayer }
            ) {
                val payerName = users.find { it.id == payerId }?.name ?: "Select Payer"
                OutlinedTextField(
                    value = payerName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Payer (Who paid money?)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPayer) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedPayer,
                    onDismissRequest = { expandedPayer = false }
                ) {
                    users.forEach { user ->
                        DropdownMenuItem(
                            text = { Text(user.name) },
                            onClick = {
                                payerId = user.id
                                expandedPayer = false
                            }
                        )
                    }
                }
            }

            // Payee Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedPayee,
                onExpandedChange = { expandedPayee = !expandedPayee }
            ) {
                val payeeName = users.find { it.id == payeeId }?.name ?: "Select Payee"
                OutlinedTextField(
                    value = payeeName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Payee (Who received money?)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPayee) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedPayee,
                    onDismissRequest = { expandedPayee = false }
                ) {
                    users.forEach { user ->
                        DropdownMenuItem(
                            text = { Text(user.name) },
                            onClick = {
                                payeeId = user.id
                                expandedPayee = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Amount Paid (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (e.g. GPay, Cash, UPI)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            val amountVal = amountText.toDoubleOrNull() ?: 0.0

            Button(
                onClick = {
                    if (payerId.isNotBlank() && payeeId.isNotBlank() && payerId != payeeId && amountVal > 0.0) {
                        expenseViewModel.recordSettlement(
                            groupId = selectedGroupId,
                            payerId = payerId,
                            payeeId = payeeId,
                            amount = amountVal,
                            note = note
                        )
                        onSettlementSaved()
                    }
                },
                enabled = payerId.isNotBlank() && payeeId.isNotBlank() && payerId != payeeId && amountVal > 0.0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Record Payment", fontWeight = FontWeight.Bold)
            }
        }
    }
}
