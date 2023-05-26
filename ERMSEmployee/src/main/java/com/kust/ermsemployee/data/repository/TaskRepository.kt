package com.kust.ermsemployee.data.repository

import com.kust.ermsemployee.data.model.TaskModel
import com.kust.ermsemployee.utils.UiState

interface TaskRepository {
    // task repository interface for task repository implementation and return UiState to view model
    fun getTasks(result: (UiState<List<TaskModel>>) -> Unit)
    suspend fun updateTask(task: TaskModel, result: (UiState<Pair<TaskModel, String>>) -> Unit)
}