package com.example.splitter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.splitter.data.model.ExpenseEntity
import com.example.splitter.domain.GroupSummary
import com.example.splitter.domain.SimplifiedDebt
import com.example.splitter.ui.components.CoralRed
import com.example.splitter.ui.components.EmeraldGreen
import com.example.splitter.ui.components.SlateGray
import com.example.splitter.ui.components.getCategoryIcon
import com.example.splitter.ui.viewmodel.GroupViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: String,
    groupViewModel: GroupViewModel,
    onBackClick: () -> Unit,
    onAddExpenseClick: (groupId: String) -> Unit,
    onSettleUpClick: (groupId: String) -> Unit
) {
    val groupSummaries by groupViewModel.groupSummariesFlow.collectAsState()
    val groupSummary = remember(groupSummaries, groupId) {
        groupSummaries.find { it.group.id == groupId }
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Expenses", "Balances & Debts")

    if (groupSummary == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Group not found")
        }
        return
    }

    val group = groupSummary.group

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = getCategoryIcon(group.category),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(group.name, fontWeight = FontWeight.Bold)
                            Text(
                                text = group.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    OutlinedButton(
                        onClick = { onSettleUpClick(groupId) },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Settle Up")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddExpenseClick(groupId) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Expense", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> GroupExpensesTab(groupSummary = groupSummary)
                1 -> GroupBalancesTab(groupSummary = groupSummary, onSettleUpClick = { onSettleUpClick(groupId) })
            }
        }
    }
}

@Composable
fun GroupExpensesTab(groupSummary: GroupSummary) {
    // Note: Expenses inside groupSummary can be rendered here
    val userBalances = groupSummary.userBalances

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Group Summary", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Total Spending: ₹${String.format("%.2f", groupSummary.totalSpending)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val net = groupSummary.userNetBalance
                    val (statusText, color) = when {
                        net > 0.05 -> "In total, you are owed ₹${String.format("%.2f", net)}" to EmeraldGreen
                        net < -0.05 -> "In total, you owe ₹${String.format("%.2f", Math.abs(net))}" to CoralRed
                        else -> "You are all settled up in this group" to SlateGray
                    }
                    Text(text = statusText, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = color)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Members & Balances", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(userBalances, key = { it.userId }) { balance ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = balance.userName, fontWeight = FontWeight.SemiBold)

                            val (balText, balColor) = when {
                                balance.netBalance > 0.05 -> "+₹${String.format("%.0f", balance.netBalance)}" to EmeraldGreen
                                balance.netBalance < -0.05 -> "-₹${String.format("%.0f", Math.abs(balance.netBalance))}" to CoralRed
                                else -> "settled up" to SlateGray
                            }
                            Text(text = balText, fontWeight = FontWeight.Bold, color = balColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupBalancesTab(groupSummary: GroupSummary, onSettleUpClick: () -> Unit) {
    val debts = groupSummary.simplifiedDebts

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Simplified Debts", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Text(
            text = "Calculated minimum payments to settle all group debts.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (debts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Everyone is settled up in this group! 🎉", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(debts) { debt ->
                    SimplifiedDebtCard(debt = debt, onSettleUpClick = onSettleUpClick)
                }
            }
        }
    }
}

@Composable
fun SimplifiedDebtCard(debt: SimplifiedDebt, onSettleUpClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${debt.fromUserName} → ${debt.toUserName}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${debt.fromUserName} owes ${debt.toUserName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${String.format("%.2f", debt.amount)}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = CoralRed
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onSettleUpClick,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Settle", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
