package com.kust.ermsemployee.data.repository

import android.net.Uri
import com.kust.ermsemployee.data.model.EmployeeModel
import com.kust.ermsemployee.utils.UiState

interface EmployeeRepository {
    fun getEmployeeList(employeeModel : EmployeeModel?, result: (UiState<List<EmployeeModel>>) -> Unit)
    fun updateEmployee(employeeModel: EmployeeModel?, result : (UiState<Pair<EmployeeModel, String>>) -> Unit)
    suspend fun uploadImage(imageUri : Uri, result : (UiState<Uri>) -> Unit)
    suspend fun addPoints(id: String): UiState<String>
    suspend fun getEmployeeRank(result: (UiState<List<EmployeeModel>>) -> Unit)
}