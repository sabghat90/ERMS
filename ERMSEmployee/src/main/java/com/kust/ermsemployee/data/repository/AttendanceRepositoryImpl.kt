package com.kust.ermsemployee.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kust.ermslibrary.models.Attendance
import com.kust.ermslibrary.utils.FirebaseRealtimeDatabaseConstants
import com.kust.ermslibrary.utils.UiState
import java.text.SimpleDateFormat
import java.util.Date

class AttendanceRepositoryImpl(
    private val firebaseDatabase: FirebaseDatabase,
    private val auth: FirebaseAuth
) : AttendanceRepository {

    private val year = SimpleDateFormat("yyyy").format(Date())
    private val month = SimpleDateFormat("MMMM").format(Date())
    private val day = SimpleDateFormat("dd").format(Date())

    override suspend fun getAttendance(result: (UiState<List<Attendance>>) -> Unit) {
        try {
            val reference = firebaseDatabase.getReference(FirebaseRealtimeDatabaseConstants.ATTENDANCE)
                .child(year)
                .child(month)
                .child(day)

            reference.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val attendanceList = ArrayList<Attendance>()
                    for (data in snapshot.children) {
                        val attendance = data.getValue(Attendance::class.java)
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
        } catch (e: Exception) {
            result(UiState.Error(e.message))
        }
    }

    override suspend fun getAttendanceForToday(
        result: (UiState<List<Attendance>>) -> Unit
    ) {
        try {
            val id = auth.currentUser?.uid.toString()
            val reference = firebaseDatabase.getReference(FirebaseRealtimeDatabaseConstants.ATTENDANCE)
                .child(year)
                .child(month)
                .child(day)
                .orderByChild("employeeId")
                .equalTo(id)

            reference.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val attendanceList = ArrayList<Attendance>()
                    for (data in snapshot.children) {
                        val attendance = data.getValue(Attendance::class.java)
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
        } catch (e: Exception) {
            result(UiState.Error(e.message))
        }
    }
}