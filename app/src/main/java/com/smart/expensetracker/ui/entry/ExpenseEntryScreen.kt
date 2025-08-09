package com.smart.expensetracker.ui.entry

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smart.expensetracker.ui.components.*
import com.smart.expensetracker.ui.theme.*
import com.smart.expensetracker.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    viewModel: ExpenseEntryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Scroll state for vertical scroll
    val scrollState = rememberScrollState()

    // Focus manager to clear focus from text fields
    val focusManager = LocalFocusManager.current

    // State to trigger blinking animation on amount
    var blinkAmount by remember { mutableStateOf(false) }

    val amountColor by animateColorAsState(
        targetValue = if (blinkAmount) Color(0xFFFFB300) else PrimaryBlue,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing)
    )

    // Trigger scroll to top when shouldScrollToTop is true
    LaunchedEffect(uiState.shouldScrollToTop) {
        if (uiState.shouldScrollToTop) {
            kotlinx.coroutines.delay(100)
            scrollState.animateScrollTo(0)
            focusManager.clearFocus()
            viewModel.clearError()
            // Additional focus clearing to ensure all fields are unfocused
            kotlinx.coroutines.delay(50)
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(uiState.shouldBlinkTotal) {
        if (uiState.shouldBlinkTotal) {
            blinkAmount = true
            kotlinx.coroutines.delay(300)
            blinkAmount = false
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            // TODO: Show snackbar for error
        }
    }

    LaunchedEffect(uiState.showToast) {
        if (uiState.showToast && uiState.successMessage != null) {
            Toast.makeText(context, uiState.successMessage, Toast.LENGTH_SHORT).show()
            viewModel.hideToast()
        }
    }

    // Clear focus when form is successfully submitted
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            // Immediate focus clearing
            focusManager.clearFocus()
            // Additional delay to ensure focus is cleared from all fields
            kotlinx.coroutines.delay(100)
            focusManager.clearFocus()
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
            onRefresh = { viewModel.getTodaysTotal() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Total Spent Today Card with blinking amount color
            TotalSpentCard(
                amount = uiState.todaysTotal,
                modifier = Modifier.padding(horizontal = 16.dp),
                amountTextColor = amountColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Add New Expense Form
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Add New Expense",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    ExpenseTextField(
                        value = uiState.title,
                        onValueChange = viewModel::updateTitle,
                        label = "Title",
                        placeholder = "Enter expense title",
                        isRequired = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AmountTextField(
                        value = uiState.amount,
                        onValueChange = viewModel::updateAmount,
                        label = "Amount",
                        placeholder = "0.00",
                        isRequired = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CategoryDropdown(
                        value = uiState.category,
                        onValueChange = viewModel::updateCategory,
                        label = "Category",
                        options = Constants.EXPENSE_CATEGORIES,
                        placeholder = "Select category",
                        isRequired = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    NotesTextField(
                        value = uiState.notes,
                        onValueChange = viewModel::updateNotes,
                        label = "Notes",
                        placeholder = "Notes (max 100 characters)"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    UploadReceiptButton(
                        onClick = viewModel::showImagePicker
                    )

                    if (uiState.receiptImagePath.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Selected: ${uiState.receiptImagePath}",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val scale by animateFloatAsState(
                        targetValue = if (uiState.isAnimating) 0.95f else 1f,
                        animationSpec = tween(durationMillis = 200)
                    )

                    PrimaryButton(
                        text = if (uiState.isLoading) "Adding..." else "Add Expense",
                        onClick = viewModel::addExpense,
                        enabled = !uiState.isLoading,
                        modifier = Modifier.scale(scale),
                        icon = {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add",
                                tint = White
                            )
                        }
                    )

                    if (uiState.error != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    if (uiState.showImagePicker) {
        ImagePickerDialog(
            onDismiss = viewModel::hideImagePicker,
            onCameraClick = viewModel::selectImageFromCamera,
            onGalleryClick = viewModel::selectImageFromGallery
        )
    }
}