package com.smart.expensetracker.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smart.expensetracker.ui.entry.ExpenseEntryScreen
import com.smart.expensetracker.ui.list.ExpenseListScreen
import com.smart.expensetracker.ui.report.ExpenseReportScreen
import com.smart.expensetracker.ui.theme.DarkGrey
import com.smart.expensetracker.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = White,
                modifier = Modifier.background(
                    color = Color.Gray,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp
                    )
                )
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // Colors so the selected pill background (indicator) wraps icon + label
                val navItemColors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFFEAEAEA), // light pill background
                    selectedIconColor = Color.Black,
                    selectedTextColor = Color.Black,
                    unselectedIconColor = Color.DarkGray,
                    unselectedTextColor = Color.DarkGray
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Add, contentDescription = "Add Expense") },
                    label = { Text("Add") },
                    selected = currentDestination?.hierarchy?.any { it.route == NavRoutes.ExpenseEntry.route } == true,
                    onClick = {
                        navController.navigate(NavRoutes.ExpenseEntry.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = navItemColors,
                    alwaysShowLabel = true
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.List, contentDescription = "Expense List") },
                    label = { Text("Expenses") },
                    selected = currentDestination?.hierarchy?.any { it.route == NavRoutes.ExpenseList.route } == true,
                    onClick = {
                        navController.navigate(NavRoutes.ExpenseList.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = navItemColors,
                    alwaysShowLabel = true
                )
                NavigationBarItem(
                    icon = { Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.smart.expensetracker.R.drawable.reports),
                        contentDescription = "Expense Report"
                    ) },
                    label = { Text("Reports") },
                    selected = currentDestination?.hierarchy?.any { it.route == NavRoutes.ExpenseReport.route } == true,
                    onClick = {
                        navController.navigate(NavRoutes.ExpenseReport.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = navItemColors,
                    alwaysShowLabel = true
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.ExpenseEntry.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.ExpenseEntry.route) { ExpenseEntryScreen() }
            composable(NavRoutes.ExpenseList.route) { ExpenseListScreen() }
            composable(NavRoutes.ExpenseReport.route) { ExpenseReportScreen() }
        }
    }
} 