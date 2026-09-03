package com.example.splitter.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Groups : BottomNavItem("groups", "Groups", Icons.Default.Group)
    object Friends : BottomNavItem("friends", "Friends", Icons.Default.PersonOutline)
    object Activity : BottomNavItem("activity", "Activity", Icons.Default.History)
    object Account : BottomNavItem("account", "Account", Icons.Default.Person)
}

object NavRoutes {
    const val GROUPS = "groups"
    const val FRIENDS = "friends"
    const val ACTIVITY = "activity"
    const val ACCOUNT = "account"

    const val GROUP_DETAIL = "group_detail/{groupId}"
    fun createGroupDetailRoute(groupId: String) = "group_detail/$groupId"

    const val ADD_EXPENSE = "add_expense"

    const val SETTLE_UP = "settle_up/{groupId}"
    fun createSettleUpRoute(groupId: String) = "settle_up/$groupId"
}
