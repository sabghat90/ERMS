package com.kust.ermsmanager.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.utils.UiState
import javax.inject.Inject

interface EmployeeRepository {
    fun getEmployeeList(employeeModel: EmployeeModel?, result : (UiState<List<EmployeeModel>>) -> Unit)
    fun updateEmployee(employeeModel: EmployeeModel?, result : (UiState<Pair<EmployeeModel, String>>) -> Unit)
}