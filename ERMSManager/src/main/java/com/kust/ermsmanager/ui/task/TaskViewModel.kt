package com.kust.ermsmanager.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kust.ermslibrary.models.Task
import com.kust.ermslibrary.utils.UiState
import com.kust.ermsmanager.data.repositories.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _createTask = MutableLiveData<UiState<Pair<Task, String>>>()
    val createTask: LiveData<UiState<Pair<Task, String>>>
        get() = _createTask

    private val _updateTask = MutableLiveData<UiState<Pair<Task, String>>>()
    val updateTask: LiveData<UiState<Pair<Task, String>>>
        get() = _updateTask

    private val _deleteTask = MutableLiveData<UiState<Pair<Task, String>>>()
    val deleteTask: LiveData<UiState<Pair<Task, String>>>
        get() = _deleteTask

    private val _getTasks = MutableLiveData<UiState<List<Task>>>()
    val getTasks: LiveData<UiState<List<Task>>>
        get() = _getTasks

    suspend fun createTask(task: Task) {
        viewModelScope.launch {
            _createTask.value = UiState.Loading
            withContext(Dispatchers.Main) {
                taskRepository.createTask(task) {
                    _createTask.postValue(it)
                }
            }
        }
    }

    fun updateTask(task: Task) {
        _updateTask.value = UiState.Loading
        taskRepository.updateTask(task) {
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

    fun getTasks() {
        viewModelScope.launch {
            _getTasks.value = UiState.Loading
            withContext(Dispatchers.IO) {
                taskRepository.getTasks {
                    _getTasks.postValue(it)
                }
            }
        }
    }
}