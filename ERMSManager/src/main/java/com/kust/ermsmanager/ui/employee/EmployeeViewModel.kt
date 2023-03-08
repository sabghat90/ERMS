package com.kust.ermsmanager.ui.employee

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.data.repositories.EmployeeRepository
import com.kust.ermsmanager.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    private val _getEmployeeList = MutableLiveData<UiState<List<EmployeeModel>>>()
    val getEmployeeList : LiveData<UiState<List<EmployeeModel>>>
        get() = _getEmployeeList

    private val _updateEmployee = MutableLiveData<UiState<Pair<EmployeeModel, String>>>()
    val updateEmployee : LiveData<UiState<Pair<EmployeeModel, String>>>
        get() = _updateEmployee

    init {
        getEmployeeList(EmployeeModel())
    }

    private fun getEmployeeList(employeeModel: EmployeeModel) {
        _getEmployeeList.value = UiState.Loading
        employeeRepository.getEmployeeList(employeeModel) {
            _getEmployeeList.value = it
        }
    }

    private fun updateEmployee(employeeModel: EmployeeModel) {
        _updateEmployee.value = UiState.Loading
        employeeRepository.updateEmployee(employeeModel) {
            _updateEmployee.value = it
        }
    }

}