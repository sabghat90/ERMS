package com.kust.erms_company.ui.employee

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kust.erms_company.data.model.EmployeeModel
import com.kust.erms_company.data.repositroy.EmployeeRepository
import com.kust.erms_company.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    private val _addEmployee = MutableLiveData<UiState<Pair<EmployeeModel, String>>>()
    val registerEmployee : LiveData<UiState<Pair<EmployeeModel, String>>>
        get() = _addEmployee

    private val _updateEmployee = MutableLiveData<UiState<Pair<EmployeeModel, String>>>()
    val updateEmployee : LiveData<UiState<Pair<EmployeeModel, String>>>
        get() = _updateEmployee

    private val _removeEmployee = MutableLiveData<UiState<String>>()
    val removeEmployee : LiveData<UiState<String>>
        get() = _removeEmployee

    private val _getEmployeeList = MutableLiveData<UiState<List<EmployeeModel>>>()
    val getEmployeeList : LiveData<UiState<List<EmployeeModel>>>
        get() = _getEmployeeList

    init {
        getEmployee(EmployeeModel())
    }

    fun registerEmployee(email : String, employeeModel: EmployeeModel) {
        _addEmployee.value = UiState.Loading
        employeeRepository.addEmployee(email, employeeModel) {
            _addEmployee.value = it
        }
    }

    fun updateEmployee(employeeModel: EmployeeModel) {
        _updateEmployee.value = UiState.Loading
        employeeRepository.updateEmployee(employeeModel) {
            _updateEmployee.value = it
        }
    }

    fun removeEmployee(employeeModel: EmployeeModel) {
        _removeEmployee.value = UiState.Loading
        employeeRepository.removeEmployee(employeeModel) {
            _removeEmployee.value = it
        }
    }

    private fun getEmployee(employeeList : EmployeeModel) {
        _getEmployeeList.value = UiState.Loading
        employeeRepository.getEmployeeList(employeeList) {
            _getEmployeeList.value = it
        }
    }
}