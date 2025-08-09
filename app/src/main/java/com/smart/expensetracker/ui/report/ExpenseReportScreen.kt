package com.smart.expensetracker.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smart.expensetracker.ui.components.*
import com.smart.expensetracker.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseReportScreen(
    viewModel: ExpenseReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            // TODO: Show snackbar for error
        }
    }

    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    LaunchedEffect(uiState.exportMessage) {
        uiState.exportMessage?.let {
            snackbarMessage = it
            showSnackbar = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        // Sticky Top Bar
        TopBar(
            title = "Expense Tracker",
            onRefresh = { viewModel.loadReportData() }
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Weekly Report Header
            WeeklyReportHeader(
                dateRange = "Last 7 days • 3 Aug - 9 Aug",
                onExportCsv = viewModel::exportAsCsv,
                onShare = viewModel::shareReport,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Weekly Report Cards
            WeeklyReportCard(
                weekTotal = uiState.weeklyTotal,
                averageDaily = uiState.averageDaily,
                vsLastWeek = uiState.vsLastWeek,
                changeType = uiState.changeType,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Daily Expenses Chart
            DailyExpensesChart(
                dailyData = uiState.dailyData,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Breakdown Chart
            CategoryBreakdownChart(
                categories = uiState.categoryData,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Export Report Section
            ExportReportSection(
                onExportPdf = viewModel::exportAsPdf,
                onExportCsv = viewModel::exportAsCsv,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

        }
    }

    // Snackbar for export messages
    if (showSnackbar) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(
                    onClick = { showSnackbar = false }
                ) {
                    Text("Dismiss")
                }
            }
        ) {
            Text(snackbarMessage)
        }
    }
} 