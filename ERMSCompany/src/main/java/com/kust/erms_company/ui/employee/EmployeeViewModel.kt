package com.kust.erms_company.ui.employee

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

    private val _registerEmployee = MutableLiveData<UiState<String>>()
    val registerEmployee : MutableLiveData<UiState<String>>
        get() = _registerEmployee

    private val _updateEmployee = MutableLiveData<UiState<Pair<EmployeeModel, String>>>()
    val updateEmployee : MutableLiveData<UiState<Pair<EmployeeModel, String>>>
        get() = _updateEmployee

    private val _deleteEmployee = MutableLiveData<UiState<String>>()
    val deleteEmployee : MutableLiveData<UiState<String>>
        get() = _deleteEmployee

    private val _getEmployeeList = MutableLiveData<UiState<List<EmployeeModel>>>()
    val getEmployeeList : MutableLiveData<UiState<List<EmployeeModel>>>
        get() = _getEmployeeList

    init {
        getEmployee(EmployeeModel())
    }

    fun registerEmployee(email : String, password: String, employeeModel: EmployeeModel) {
        _registerEmployee.value = UiState.Loading
        employeeRepository.registerEmployee(email, password, employeeModel) {
            _registerEmployee.value = it
        }
    }

    fun updateEmployee(employeeModel: EmployeeModel) {
        _updateEmployee.value = UiState.Loading
        employeeRepository.updateEmployee(employeeModel) {
            _updateEmployee.value = it
        }
    }

    fun deleteEmployee(employeeModel: EmployeeModel) {
        _deleteEmployee.value = UiState.Loading
        employeeRepository.deleteEmployee(employeeModel) {
            _deleteEmployee.value = it
        }
    }

    private fun getEmployee(employeeList : EmployeeModel) {
        _getEmployeeList.value = UiState.Loading
        employeeRepository.getEmployeeList(employeeList) {
            _getEmployeeList.value = it
        }
    }
}