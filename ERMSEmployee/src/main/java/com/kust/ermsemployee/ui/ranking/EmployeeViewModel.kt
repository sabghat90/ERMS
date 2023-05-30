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

    private val _updateEmployee = MutableLiveData<UiState<Pair<Employee, String>>>()
    val updateEmployee: LiveData<UiState<Pair<Employee, String>>>
        get() = _updateEmployee

    private val _getEmployeeRank = MutableLiveData<UiState<List<Employee>>>()
    val getEmployeeRank: LiveData<UiState<List<Employee>>>
        get() = _getEmployeeRank

    init {
        getEmployeeList(Employee())
    }

    private fun getEmployeeList(employee: Employee) {
        _getEmployeeList.value = UiState.Loading
        employeeRepository.getEmployeeList(employee) {
            _getEmployeeList.value = it
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

    fun getEmployeeRank() {
        _getEmployeeRank.value = UiState.Loading
        viewModelScope.launch {
            employeeRepository.getEmployeeRank {
                _getEmployeeRank.value = it
            }
        }
    }
}