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

    private val _getTask = MutableLiveData<UiState<List<TaskModel>>>()
    val getTask : LiveData<UiState<List<TaskModel>>>
        get() = _getTask

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

    suspend fun deleteTask(id: Int) {
        _deleteTask.value = UiState.Loading
        taskRepository.deleteTask(id) {
            _deleteTask.value = it
        }
    }

    private fun getTask(taskModel: TaskModel) {
        viewModelScope.launch {
            _getTask.value = UiState.Loading
            withContext(Dispatchers.Main) {
                taskRepository.getTasks(taskModel) {
                    _getTask.postValue(it)
                }
            }
        }
    }

    init {
        getTask(TaskModel())
    }
}