package com.kust.ermsemployee.data.repository

import com.kust.ermslibrary.models.Attendance
import com.kust.ermslibrary.utils.UiState

interface AttendanceRepository {
    fun getAttendance(attendance: Attendance, result: (UiState<List<Attendance>>) -> Unit)
    fun getAttendanceForToday(result: (UiState<List<Attendance>>) -> Unit)
}