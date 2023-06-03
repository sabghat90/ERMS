package com.kust.ermsemployee.data.repository

import com.kust.ermslibrary.models.Attendance
import com.kust.ermslibrary.utils.UiState

interface AttendanceRepository {
    suspend fun getAttendance(result: (UiState<List<Attendance>>) -> Unit)
    suspend fun getAttendanceForToday(result: (UiState<List<Attendance>>) -> Unit)

}