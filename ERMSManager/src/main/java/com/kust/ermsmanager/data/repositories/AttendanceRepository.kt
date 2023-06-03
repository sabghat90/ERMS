package com.kust.ermsmanager.data.repositories

import com.kust.ermslibrary.models.Attendance
import com.kust.ermslibrary.utils.UiState

interface AttendanceRepository {
    suspend fun markAttendance(attendance: Attendance, result: (UiState<String>) -> Unit)
    suspend fun getAttendanceForOneEmployee(id: String, result: (UiState<List<Attendance>>) -> Unit)
    suspend fun getAttendance(result: (UiState<List<Attendance>>) -> Unit)
}