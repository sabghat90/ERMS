package com.kust.ermsmanager.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kust.ermsmanager.data.models.TaskModel
import com.kust.ermsmanager.data.repositories.TaskRepository
import com.kust.ermsmanager.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
): ViewModel() {
    private val _createTask = MutableLiveData<UiState<Pair<TaskModel, String>>>()
    val createTask : LiveData<UiState<Pair<TaskModel, String>>>
        get() = _createTask

    private val _updateTask = MutableLiveData<UiState<Pair<TaskModel, String>>>()
    val updateTask : LiveData<UiState<Pair<TaskModel, String>>>
        get() = _updateTask

    private val _deleteTask = MutableLiveData<UiState<Pair<TaskModel, String>>>()
    val deleteTask : LiveData<UiState<Pair<TaskModel, String>>>
        get() = _deleteTask

    private val _getTasks = MutableLiveData<UiState<List<TaskModel>>>()
    val getTasks : LiveData<UiState<List<TaskModel>>>
        get() = _getTasks

    suspend fun createTask(taskModel: TaskModel) {
        viewModelScope.launch {
            _createTask.value = UiState.Loading
            withContext(Dispatchers.Main) {
                taskRepository.createTask(taskModel) {
                    _createTask.postValue(it)
                }
            }
        }
    }

    suspend fun updateTask(taskModel: TaskModel) {
        _updateTask.value = UiState.Loading
        taskRepository.updateTask(taskModel) {
            _updateTask.value = it
        }
    }

    suspend fun deleteTask(id: String) {
        viewModelScope.launch {
            _deleteTask.value = UiState.Loading
            withContext(Dispatchers.Main) {
                taskRepository.deleteTask(id) {
                    _deleteTask.postValue(it)
                }
            }
        }
    }

    private fun getTasks(taskModel: TaskModel?) {
        viewModelScope.launch {
            _getTasks.value = UiState.Loading
            withContext(Dispatchers.IO) {
                taskRepository.getTasks(taskModel) {
                    _getTasks.postValue(it)
                }
            }
        }
    }

    init {
        getTasks(TaskModel())
    }
}