package com.kust.ermsmanager.data.repositories

import android.net.Uri
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.UiState

interface EmployeeRepository {
    fun getEmployeeList(employee : Employee?, result: (UiState<List<Employee>>) -> Unit)
    fun updateEmployee(employee: Employee?, result : (UiState<Pair<Employee, String>>) -> Unit)
    suspend fun uploadImage(imageUri : Uri, result : (UiState<Uri>) -> Unit)
    suspend fun addPoints(id: String): UiState<String>
    suspend fun removePoints(id: String): UiState<String>
}