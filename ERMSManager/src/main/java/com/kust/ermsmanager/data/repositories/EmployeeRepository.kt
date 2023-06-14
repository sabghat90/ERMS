package com.kust.ermsmanager.data.repositories

import android.net.Uri
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.UiState

interface EmployeeRepository {
    suspend fun getEmployeeList(result: (UiState<List<Employee>>) -> Unit)
    suspend fun getEmployee(result: (UiState<Employee>) -> Unit)
    fun updateEmployee(employee: Employee?, result: (UiState<Pair<Employee, String>>) -> Unit)
    suspend fun updateEmployeeProfile(employee: Employee?, result: (UiState<String>) -> Unit)
    suspend fun uploadProfilePicture(imageUri: Uri, result: (UiState<Uri>) -> Unit)
    suspend fun addPoints(id: String, points: Double): UiState<String>
    suspend fun removePoints(id: String, points: Double): UiState<String>
}