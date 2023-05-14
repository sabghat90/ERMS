package com.kust.ermsmanager.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.kust.ermsmanager.data.models.AttendanceModel
import com.kust.ermsmanager.utils.UiState

class AttendanceRepositoryImpl (
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
        ) : AttendanceRepository {
    override fun markAttendance(
        attendanceModel: AttendanceModel,
        result: (UiState<String>) -> Unit
    ) {
        val attendanceRef = database.getReference("attendance")
        val attendanceId = attendanceRef.push().key
        attendanceId?.let {
            attendanceRef.child(it).setValue(attendanceModel)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        result(UiState.Success("Attendance marked successfully"))
                    } else {
                        result(UiState.Error(task.exception?.message ?: "Error marking attendance"))
                    }
                }
        }
    }

    override fun getAttendanceForOneEmployee(
        id: String,
        result: (UiState<List<AttendanceModel>>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getAttendance(
        attendanceModel: AttendanceModel,
        result: (UiState<List<AttendanceModel>>) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}