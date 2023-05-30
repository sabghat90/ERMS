package com.kust.ermsemployee.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kust.ermsemployee.data.repository.TaskRepository
import com.kust.ermslibrary.models.Task
import com.kust.ermslibrary.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
): ViewModel() {
    private val _updateTask = MutableLiveData<UiState<Pair<Task, String>>>()
    val updateTask : LiveData<UiState<Pair<Task, String>>>
        get() = _updateTask

    private val _getTasks = MutableLiveData<UiState<List<Task>>>()
    val getTasks : LiveData<UiState<List<Task>>>
        get() = _getTasks

    suspend fun updateTask(task: Task) {
        viewModelScope.launch {
            _updateTask.value = UiState.Loading
            taskRepository.updateTask(task) {
                _updateTask.value = it
            }
        }
    }

    private fun getTasks() {
        viewModelScope.launch {
            _getTasks.value = UiState.Loading
            withContext(Dispatchers.IO) {
                taskRepository.getTasks {
                    _getTasks.postValue(it)
                }
            }
        }
    }

    init {
        getTasks()
    }
}