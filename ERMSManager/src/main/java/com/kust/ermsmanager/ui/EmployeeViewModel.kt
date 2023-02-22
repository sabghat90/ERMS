package com.kust.ermsmanager.ui

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

    private val _employeeList = MutableLiveData<UiState<Pair<EmployeeModel, String>>>()
    val employeeList : LiveData<UiState<Pair<EmployeeModel, String>>>
        get() = _employeeList

    private val _updateEmployee = MutableLiveData<UiState<Pair<EmployeeModel, String>>>()
    val updateEmployee : LiveData<UiState<Pair<EmployeeModel, String>>>
        get() = _updateEmployee


}