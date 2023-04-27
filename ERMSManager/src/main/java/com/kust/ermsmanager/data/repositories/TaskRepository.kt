package com.kust.ermsmanager.data.repositories

import com.kust.ermsmanager.data.models.TaskModel
import com.kust.ermsmanager.utils.UiState

interface TaskRepository {
    // task repository interface for task repository implementation and return UiState to view model
    fun getTasks(taskModel: TaskModel?, result: (UiState<List<TaskModel>>) -> Unit)
    fun createTask(task: TaskModel, result: (UiState<Pair<TaskModel, String>>) -> Unit)
    suspend fun updateTask(task: TaskModel, result: (UiState<Pair<TaskModel, String>>) -> Unit)
    suspend fun deleteTask(id: String, result: (UiState<Pair<TaskModel, String>>) -> Unit)
}