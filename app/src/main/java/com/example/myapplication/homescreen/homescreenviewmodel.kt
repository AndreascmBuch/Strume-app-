package com.example.myapplication.homescreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDate


class HomeViewModel : ViewModel() {
    val tasks = mutableStateListOf<Task>()
    var showDialog by mutableStateOf(false)
    var textInput by mutableStateOf("")
    var selectedTime by mutableStateOf("")
    val availableTimes = List(24 * 12) { i ->
        String.format("%02d:%02d", i / 12, (i % 12) * 5)
    }
    var selectedDate by mutableStateOf(LocalDate.now())
    var showDatePickerDialog by mutableStateOf(false)
    var showTimePickerDialog by mutableStateOf(false)

    fun addTask(task: Task) {
        tasks.add(task)
    }


    fun addTask() {
        if (textInput.isNotBlank()) {
            val task = Task(
                name = textInput,
                date = selectedDate.toString(),
                time = selectedTime,
                icon = "default_icon"
            )
            tasks.add(task)
            textInput = ""
            selectedTime = availableTimes.first()  // Reset or set a default time if necessary
            showDialog = false
        }
    }

    fun showAddTaskDialog() {
        showDialog = true
    }

    fun hideAddTaskDialog() {
        showDialog = false
    }
}