package com.kust.ermsmanager.data.repositories

import com.kust.ermslibrary.models.Task
import com.kust.ermslibrary.utils.UiState

interface TaskRepository {
    // task repository interface for task repository implementation and return UiState to view model
    fun getTasks(result: (UiState<List<Task>>) -> Unit)
    fun createTask(task: Task, result: (UiState<Pair<Task, String>>) -> Unit)
    fun updateTask(task: Task, result: (UiState<Pair<Task, String>>) -> Unit)
    suspend fun deleteTask(id: String, result: (UiState<Pair<Task, String>>) -> Unit)
}