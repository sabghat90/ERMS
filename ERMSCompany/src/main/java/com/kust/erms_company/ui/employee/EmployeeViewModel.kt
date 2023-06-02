package com.kust.erms_company.ui.employee

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kust.erms_company.data.repositroy.EmployeeRepository
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    private val _addEmployee = MutableLiveData<UiState<Pair<Employee, String>>>()
    val registerEmployee : LiveData<UiState<Pair<Employee, String>>>
        get() = _addEmployee

    private val _updateEmployee = MutableLiveData<UiState<Pair<Employee, String>>>()
    val updateEmployee : LiveData<UiState<Pair<Employee, String>>>
        get() = _updateEmployee

    private val _removeEmployee = MutableLiveData<UiState<String>>()
    val removeEmployee : LiveData<UiState<String>>
        get() = _removeEmployee

    private val _getEmployeeList = MutableLiveData<UiState<List<Employee>>>()
    val getEmployeeList : LiveData<UiState<List<Employee>>>
        get() = _getEmployeeList

    fun registerEmployee(email : String, employee: Employee) {
        _addEmployee.value = UiState.Loading
        employeeRepository.addEmployee(email, employee) {
            _addEmployee.value = it
        }
    }

    fun updateEmployee(employee: Employee) {
        _updateEmployee.value = UiState.Loading
        employeeRepository.updateEmployee(employee) {
            _updateEmployee.value = it
        }
    }

    fun removeEmployee(employee: Employee) {
        _removeEmployee.value = UiState.Loading
        employeeRepository.removeEmployee(employee) {
            _removeEmployee.value = it
        }
    }

    fun getEmployeeList() {
        _getEmployeeList.value = UiState.Loading
        employeeRepository.getEmployeeList() {
            _getEmployeeList.value = it
        }
    }
}