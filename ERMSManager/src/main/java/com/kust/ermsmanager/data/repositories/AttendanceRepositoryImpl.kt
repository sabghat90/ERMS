package com.kust.ermsmanager.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kust.ermsmanager.data.models.AttendanceModel
import com.kust.ermsmanager.utils.FirebaseRealtimeDatabaseConstants
import com.kust.ermsmanager.utils.UiState
import java.text.SimpleDateFormat
import java.util.Date

class AttendanceRepositoryImpl (
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
        ) : AttendanceRepository {

    private val year = SimpleDateFormat("yyyy").format(Date())
    private val month = SimpleDateFormat("MMMM").format(Date())
    private val day = SimpleDateFormat("dd").format(Date())
    override fun markAttendance(
        attendanceModel: AttendanceModel,
        result: (UiState<String>) -> Unit
    ) {
        val attendanceRef = database.getReference(FirebaseRealtimeDatabaseConstants.ATTENDANCE)
            .child(attendanceModel.year)
            .child(attendanceModel.month)
            .child(attendanceModel.day)
            .child(attendanceModel.employeeId)

        attendanceRef.setValue(attendanceModel).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result(UiState.Success("Attendance marked successfully"))
            } else {
                result(UiState.Error(task.exception?.message.toString()))
            }
        }
    }

    override fun getAttendanceForOneEmployee(
        id: String,
        result: (UiState<List<AttendanceModel>>) -> Unit
    ) {

    }

    override fun getAttendance(
        result: (UiState<List<AttendanceModel>>) -> Unit
    ) {
        // get attendance from firebase realtime database where id field is equal to current user id
        val reference = database.getReference(FirebaseRealtimeDatabaseConstants.ATTENDANCE)
            .child(year)
            .child(month)
            .child(day)

        reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val attendanceList = ArrayList<AttendanceModel>()
                for (data in snapshot.children) {
                    val attendance = data.getValue(AttendanceModel::class.java)
                    if (attendance != null) {
                        attendanceList.add(attendance)
                    }
                }
                result(UiState.Success(attendanceList))
            }

            override fun onCancelled(error: DatabaseError) {
                result(UiState.Error(error.message))
            }
        })
    }
}