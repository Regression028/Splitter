package com.example.splitter.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.splitter.ui.navigation.BottomNavItem
import com.example.splitter.ui.navigation.NavRoutes
import com.example.splitter.ui.viewmodel.ActivityViewModel
import com.example.splitter.ui.viewmodel.AuthViewModel
import com.example.splitter.ui.viewmodel.ExpenseViewModel
import com.example.splitter.ui.viewmodel.FriendViewModel
import com.example.splitter.ui.viewmodel.GroupViewModel

@Composable
fun MainScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    groupViewModel: GroupViewModel = hiltViewModel(),
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    friendViewModel: FriendViewModel = hiltViewModel(),
    activityViewModel: ActivityViewModel = hiltViewModel()
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    var isRegisterScreenVisible by remember { mutableStateOf(false) }

    if (!isLoggedIn) {
        if (isRegisterScreenVisible) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = { },
                onNavigateToLogin = { isRegisterScreenVisible = false }
            )
        } else {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { },
                onNavigateToRegister = { isRegisterScreenVisible = true }
            )
        }
        return
    }

    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem.Groups,
        BottomNavItem.Friends,
        BottomNavItem.Activity,
        BottomNavItem.Account
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isBottomBarVisible = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.GROUPS,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.GROUPS) {
                GroupsScreen(
                    groupViewModel = groupViewModel,
                    friendViewModel = friendViewModel,
                    onGroupClick = { groupId ->
                        navController.navigate(NavRoutes.createGroupDetailRoute(groupId))
                    },
                    onAddExpenseClick = {
                        navController.navigate(NavRoutes.createAddExpenseRoute())
                    }
                )
            }

            composable(NavRoutes.FRIENDS) {
                FriendsScreen(
                    friendViewModel = friendViewModel,
                    onSettleUpClick = {
                        navController.navigate(NavRoutes.createAddExpenseRoute())
                    }
                )
            }

            composable(NavRoutes.ACTIVITY) {
                ActivityScreen(activityViewModel = activityViewModel)
            }

            composable(NavRoutes.ACCOUNT) {
                val currentUser by authViewModel.currentUser.collectAsState()
                AccountScreen(
                    userName = currentUser?.name ?: authViewModel.currentUserName,
                    userEmail = currentUser?.email ?: authViewModel.currentUserEmail,
                    groupViewModel = groupViewModel,
                    friendViewModel = friendViewModel,
                    onLogoutClick = { authViewModel.logout() }
                )
            }

            composable(
                route = NavRoutes.GROUP_DETAIL,
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                GroupDetailScreen(
                    groupId = groupId,
                    groupViewModel = groupViewModel,
                    onBackClick = { navController.popBackStack() },
                    onAddExpenseClick = { id ->
                        navController.navigate(NavRoutes.createAddExpenseRoute(id))
                    },
                    onSettleUpClick = { id ->
                        navController.navigate(NavRoutes.createSettleUpRoute(id))
                    }
                )
            }

            composable(
                route = NavRoutes.ADD_EXPENSE,
                arguments = listOf(
                    navArgument("groupId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId")
                AddExpenseScreen(
                    initialGroupId = groupId,
                    expenseViewModel = expenseViewModel,
                    onBackClick = { navController.popBackStack() },
                    onExpenseSaved = { navController.popBackStack() }
                )
            }

            composable(
                route = NavRoutes.SETTLE_UP,
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId")
                SettleUpScreen(
                    initialGroupId = groupId,
                    expenseViewModel = expenseViewModel,
                    onBackClick = { navController.popBackStack() },
                    onSettlementSaved = { navController.popBackStack() }
                )
            }
        }
    }
}
