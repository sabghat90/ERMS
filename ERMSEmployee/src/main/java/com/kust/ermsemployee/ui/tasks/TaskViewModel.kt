package com.kust.ermsemployee.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kust.ermsemployee.data.model.TaskModel
import com.kust.ermsemployee.data.repository.TaskRepository
import com.kust.ermsemployee.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
): ViewModel() {
    private val _updateTask = MutableLiveData<UiState<Pair<TaskModel, String>>>()
    val updateTask : LiveData<UiState<Pair<TaskModel, String>>>
        get() = _updateTask

    private val _getTasks = MutableLiveData<UiState<List<TaskModel>>>()
    val getTasks : LiveData<UiState<List<TaskModel>>>
        get() = _getTasks

    suspend fun updateTask(taskModel: TaskModel) {
        viewModelScope.launch {
            _updateTask.value = UiState.Loading
            taskRepository.updateTask(taskModel) {
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