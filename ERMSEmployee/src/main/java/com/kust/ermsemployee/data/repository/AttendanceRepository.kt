package com.kust.ermsemployee.data.repository

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.kust.ermsemployee.data.model.AttendanceModel
import com.kust.ermsemployee.utils.UiState
import java.util.Objects

interface AttendanceRepository {
    fun getAttendance(attendanceModel: AttendanceModel, result: (UiState<List<AttendanceModel>>) -> Unit)
    fun getAttendanceForToday(result: (UiState<List<AttendanceModel>>) -> Unit)
}