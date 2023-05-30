package com.kust.ermsemployee.data.repository

import com.kust.ermslibrary.models.Task
import com.kust.ermslibrary.utils.UiState

interface TaskRepository {
    // task repository interface for task repository implementation and return UiState to view model
    fun getTasks(result: (UiState<List<Task>>) -> Unit)
    suspend fun updateTask(task: Task, result: (UiState<Pair<Task, String>>) -> Unit)
}