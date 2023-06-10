package com.kust.ermsmanager.ui.employee

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.UiState
import com.kust.ermsmanager.data.repositories.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    private val _getEmployeeList = MutableLiveData<UiState<List<Employee>>>()
    val getEmployeeList: LiveData<UiState<List<Employee>>>
        get() = _getEmployeeList

    val getEmployee = MutableLiveData<UiState<Employee>>()
    val getEmployeeLiveData: LiveData<UiState<Employee>>
        get() = getEmployee

    private val _updateEmployee = MutableLiveData<UiState<Pair<Employee, String>>>()
    val updateEmployee: LiveData<UiState<Pair<Employee, String>>>
        get() = _updateEmployee

    private val _addPoints = MutableLiveData<UiState<String>>()
    val addPoints: LiveData<UiState<String>>
        get() = _addPoints

    private val _removePoints = MutableLiveData<UiState<String>>()
    val removePoints: LiveData<UiState<String>>
        get() = _removePoints

    private val _updateEmployeeProfile = MutableLiveData<UiState<String>>()
    val updateEmployeeProfile: LiveData<UiState<String>>
        get() = _updateEmployeeProfile

    init {
        getEmployee()
    }

    fun getEmployeeList() {
        _getEmployeeList.value = UiState.Loading
        viewModelScope.launch {
            employeeRepository.getEmployeeList {
                _getEmployeeList.value = it
            }
        }
    }

    private fun getEmployee() {
        getEmployee.value = UiState.Loading
        viewModelScope.launch {
            employeeRepository.getEmployee {
                getEmployee.value = it
            }
        }
    }

    fun updateEmployee(employee: Employee) {
        _updateEmployee.value = UiState.Loading
        employeeRepository.updateEmployee(employee) {
            _updateEmployee.value = it
        }
    }

    fun uploadImage(fileUris: Uri, result: (UiState<Uri>) -> Unit) {
        result.invoke(UiState.Loading)
        viewModelScope.launch {
            employeeRepository.uploadImage(fileUris, result)
        }
    }

    fun addPoints(id: String, points: Double) {
        _addPoints.value = UiState.Loading
        viewModelScope.launch {
            _addPoints.value = employeeRepository.addPoints(id, points)
        }
    }

    fun removePoints(id: String, points: Double) {
        _removePoints.value = UiState.Loading
        viewModelScope.launch {
            _removePoints.value = employeeRepository.removePoints(id, points)
        }
    }

    fun updateEmployeeProfile(employee: Employee) {
        _updateEmployeeProfile.value = UiState.Loading
        viewModelScope.launch {
            employeeRepository.updateEmployeeProfile(employee) {
                _updateEmployeeProfile.value = it
            }
        }
    }
}