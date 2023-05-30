package com.kust.ermsmanager.data.repositories

import com.kust.ermslibrary.models.Attendance
import com.kust.ermslibrary.utils.UiState

interface AttendanceRepository {
    fun markAttendance(attendance: Attendance, result: (UiState<String>) -> Unit)
    fun getAttendanceForOneEmployee(id: String, result: (UiState<List<Attendance>>) -> Unit)
    fun getAttendance(result: (UiState<List<Attendance>>) -> Unit)
}