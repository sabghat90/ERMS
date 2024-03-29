package com.kust.ermsemployee.ui.ranking

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kust.ermsemployee.data.repository.EmployeeRepository
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.UiState
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

    private val _getEmployee = MutableLiveData<UiState<Employee>>()
    val getEmployee: LiveData<UiState<Employee>>
        get() = _getEmployee

    private val _updateEmployee = MutableLiveData<UiState<Pair<Employee, String>>>()
    val updateEmployee: LiveData<UiState<Pair<Employee, String>>>
        get() = _updateEmployee

    private val _getEmployeeRank = MutableLiveData<UiState<List<Employee>>>()
    val getEmployeeRank: LiveData<UiState<List<Employee>>>
        get() = _getEmployeeRank

    fun getEmployeeList() {
        _getEmployeeList.value = UiState.Loading
        viewModelScope.launch {
            employeeRepository.getEmployeeList {
                _getEmployeeList.value = it
            }
        }
    }

    suspend fun getEmployee() {
        _getEmployee.value = UiState.Loading
        viewModelScope.launch {
            employeeRepository.getEmployee {
                _getEmployee.value = it
            }
        }
    }

    fun updateEmployee(employee: Employee) {
        _updateEmployee.value = UiState.Loading
        employeeRepository.updateEmployee(employee) {
            _updateEmployee.value = it
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