package com.kust.ermsemployee.ui.employee

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

    private val getEmployee = MutableLiveData<UiState<Employee>>()
    val getEmployeeLiveData: LiveData<UiState<Employee>>
        get() = getEmployee

    private val _updateProfile = MutableLiveData<UiState<Pair<Employee, String>>>()
    val updateProfile: LiveData<UiState<Pair<Employee, String>>>
        get() = _updateProfile

    private val _addPoints = MutableLiveData<UiState<String>>()
    val addPoints: LiveData<UiState<String>>
        get() = _addPoints

    private val _removePoints = MutableLiveData<UiState<String>>()
    val removePoints: LiveData<UiState<String>>
        get() = _removePoints

    suspend fun getEmployeeList() {
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

    fun updateProfile(employee: Employee) {
        _updateProfile.value = UiState.Loading
        employeeRepository.updateEmployee(employee) {
            _updateProfile.value = it
        }
    }

    fun uploadImage(fileUris: Uri, result: (UiState<Uri>) -> Unit) {
        result.invoke(UiState.Loading)
        viewModelScope.launch {
            employeeRepository.uploadProfilePicture(fileUris, result)
        }
    }
}