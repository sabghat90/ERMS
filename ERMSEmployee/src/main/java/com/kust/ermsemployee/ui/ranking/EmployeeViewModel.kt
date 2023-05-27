package com.kust.ermsemployee.ui.ranking

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kust.ermsemployee.data.model.EmployeeModel
import com.kust.ermsemployee.data.repository.EmployeeRepository
import com.kust.ermsemployee.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    private val _getEmployeeList = MutableLiveData<UiState<List<EmployeeModel>>>()
    val getEmployeeList: LiveData<UiState<List<EmployeeModel>>>
        get() = _getEmployeeList

    private val _updateEmployee = MutableLiveData<UiState<Pair<EmployeeModel, String>>>()
    val updateEmployee: LiveData<UiState<Pair<EmployeeModel, String>>>
        get() = _updateEmployee

    private val _addPoints = MutableLiveData<UiState<String>>()
    val addPoints: LiveData<UiState<String>>
        get() = _addPoints

    private val _getEmployeeRank = MutableLiveData<UiState<List<EmployeeModel>>>()
    val getEmployeeRank: LiveData<UiState<List<EmployeeModel>>>
        get() = _getEmployeeRank

    init {
        getEmployeeList(EmployeeModel())
    }

    private fun getEmployeeList(employeeModel: EmployeeModel) {
        _getEmployeeList.value = UiState.Loading
        employeeRepository.getEmployeeList(employeeModel) {
            _getEmployeeList.value = it
        }
    }

    fun updateEmployee(employeeModel: EmployeeModel) {
        _updateEmployee.value = UiState.Loading
        employeeRepository.updateEmployee(employeeModel) {
            _updateEmployee.value = it
        }
    }

    fun uploadImage(fileUris: Uri, result: (UiState<Uri>) -> Unit) {
        result.invoke(UiState.Loading)
        viewModelScope.launch {
            employeeRepository.uploadImage(fileUris, result)
        }
    }

    fun addPoints(id: String) {
        _addPoints.value = UiState.Loading
        viewModelScope.launch {
            _addPoints.value = employeeRepository.addPoints(id)
        }
    }

    fun getEmployeeRank() {
        _getEmployeeRank.value = UiState.Loading
        viewModelScope.launch {
            employeeRepository.getEmployeeRank {
                _getEmployeeRank.value = it
            }
        }
    }
}