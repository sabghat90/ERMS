package com.kust.ermsemployee.data.repository

import com.kust.ermsemployee.data.model.AttendanceModel
import com.kust.ermsemployee.utils.UiState

interface AttendanceRepository {
    fun getAttendance(attendanceModel: AttendanceModel, result: (UiState<List<AttendanceModel>>) -> Unit)
}