package com.kust.ermsmanager.data.repositories

import android.net.Uri
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.data.models.TaskModel
import com.kust.ermsmanager.utils.UiState

interface EmployeeRepository {
    fun getEmployeeList(employeeModel : EmployeeModel?, result: (UiState<List<EmployeeModel>>) -> Unit)
    fun updateEmployee(employeeModel: EmployeeModel?, result : (UiState<Pair<EmployeeModel, String>>) -> Unit)
    suspend fun uploadImage(imageUri : Uri, result : (UiState<Uri>) -> Unit)
    suspend fun addPoints(id: String): UiState<String>
}