package com.kust.ermsemployee.data.repository

import android.net.Uri
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.UiState

interface EmployeeRepository {
    suspend fun getEmployeeList(result: (UiState<List<Employee>>) -> Unit)
    suspend fun getEmployee(result: (UiState<Employee>) -> Unit)
    fun updateEmployee(employee: Employee?, result : (UiState<Pair<Employee, String>>) -> Unit)
    suspend fun uploadImage(imageUri : Uri, result : (UiState<Uri>) -> Unit)
    suspend fun getEmployeeRank(result: (UiState<List<Employee>>) -> Unit)
}