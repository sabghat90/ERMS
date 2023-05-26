package com.kust.ermsmanager.data.repositories

import com.kust.ermsmanager.data.models.AttendanceModel
import com.kust.ermsmanager.utils.UiState

interface AttendanceRepository {
    fun markAttendance(attendanceModel: AttendanceModel, result: (UiState<String>) -> Unit)
    fun getAttendanceForOneEmployee(id: String, result: (UiState<List<AttendanceModel>>) -> Unit)
    fun getAttendance(result: (UiState<List<AttendanceModel>>) -> Unit)
}