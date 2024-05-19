package com.example.myapplication.homescreen

import android.app.TimePickerDialog
import android.util.Log
import android.widget.TimePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import androidx.compose.ui.unit.sp

fun getDayOfMonthSuffix(day: Int): String {
    return when {
        day in 11..13 -> "th"
        day % 10 == 1 -> "st"
        day % 10 == 2 -> "nd"
        day % 10 == 3 -> "rd"
        else -> "th"
    }
}

// Hovedskærmskomponenten
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(viewModel: HomeViewModel) {
    val context = LocalContext.current

    if (viewModel.showDialog) {
        val calendar = Calendar.getInstance()


        // Launch DatePickerDialog
        fun showDatePicker() {
            val datePickerDialog = android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val localDate = LocalDate.of(year, month + 1, dayOfMonth)
                    viewModel.selectedDate = localDate // Store the LocalDate
                    // Format and store the string representation
                    viewModel.selectedDateString = localDate.format(DateTimeFormatter.ofPattern("EEEE, d'" + getDayOfMonthSuffix(dayOfMonth) + "' MMMM"))
                    Log.d("DatePicker", "Selected Date String: ${viewModel.selectedDateString}") // Add this line
                    viewModel.showDatePickerDialog = false // Close the date picker dialog
                },
                viewModel.selectedDate.year,
                viewModel.selectedDate.monthValue - 1,
                viewModel.selectedDate.dayOfMonth
            )
            datePickerDialog.show()
        }

        // Launch TimePickerDialog
        fun showTimePicker() {
            TimePickerDialog(
                context,
                { _: TimePicker, hourOfDay: Int, minute: Int ->
                    viewModel.selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        AlertDialog(
            onDismissRequest = { viewModel.hideAddTaskDialog() },
            title = { Text(if (viewModel.editingTaskId == null) "Add New Task" else "Edit Task") },
            text = {
                Column {
                    OutlinedTextField(
                        value = viewModel.textInput,
                        onValueChange = { viewModel.textInput = it },
                        label = { Text("Task Details") }
                    )
                    Button(onClick = { showDatePicker() }) {
                        Text("Select Date: ${viewModel.selectedDateString}")
                    }
                    Button(onClick = { showTimePicker() }) {
                        Text("Select Time: ${viewModel.selectedTime}")
                    }
                    if (viewModel.editingTaskId != null) {
                        Button(
                            onClick = { viewModel.deleteTask(viewModel.editingTaskId!!) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Delete", color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.addOrUpdateTask() }) {
                    Text(if (viewModel.editingTaskId == null) "Add" else "Update")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.hideAddTaskDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    // This log will show the number of tasks in the ViewModel
    Log.d("HomeScreen", "Recomposing with ${homeViewModel.tasks.size} tasks")

    // Correct the ViewModel instance reference for logging
    Log.d("HomeScreen", "ViewModel instance in HomeScreen: $homeViewModel")

    val viewModel: HomeViewModel = viewModel()
    val tasks = homeViewModel.tasks

    LaunchedEffect(tasks) {
        Log.d("HomeScreen", "Tasks have changed. Size: ${tasks.size}")
        // Potentially other side effects
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF4E4853))
            .padding(start = 16.dp, end = 16.dp, top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Hello",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(text = "Here are your tasks for the day",
            fontSize = 16.sp,
            color = Color.White
        )

        LazyColumn(modifier = Modifier.padding(top = 24.dp)) {
            items(tasks) { task ->
                Log.d("HomeScreen", "Displaying task: ${task.name}, ${task.date}, ${task.time}")
                Text(
                    text = task.date,
                    color = Color.White,  // Making the date stand out
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "${task.observableName}, ${task.observableTime}",
                    color = Color.White,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF737483)) // Grey background only for this part
                        .padding(14.dp) // Padding inside the background
                        .fillMaxWidth() // Ensure the background fills the width
                        .clickable { homeViewModel.editTask(task.id) }
                )
                Log.d("HomeScreen", "Displaying task: ${task.name}")
            }
            Log.d("HomeScreen", "LazyColumn recomposing with ${viewModel.tasks.size} tasks")
        }
        }

        // Call the AddTaskDialog composable function
        AddTaskDialog(viewModel)
}