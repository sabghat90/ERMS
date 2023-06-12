package com.kust.ermsmanager.data.repositories

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kust.ermslibrary.models.Attendance
import com.kust.ermslibrary.utils.FirebaseRealtimeDatabaseConstants
import com.kust.ermslibrary.utils.UiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceRepositoryImpl(
    private val database: FirebaseDatabase
) : AttendanceRepository {
    private val today = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    override suspend fun markAttendance(
        attendance: Attendance,
        result: (UiState<String>) -> Unit
    ) {
        try {
            val reference = database.getReference(FirebaseRealtimeDatabaseConstants.ATTENDANCE)
                .child(attendance.date)
                .child(attendance.employeeId)

            reference.setValue(attendance).addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Success("Attendance marked successfully"))
                } else {
                    result.invoke(UiState.Error(it.exception?.localizedMessage))
                }
            }
        } catch (e: Exception) {
            result.invoke(UiState.Error(e.localizedMessage))
        }
    }

    override suspend fun getAttendanceForOneEmployee(
        id: String,
        result: (UiState<List<Attendance>>) -> Unit
    ) {
        try {
            val reference = database.getReference(FirebaseRealtimeDatabaseConstants.ATTENDANCE)
                .child(today.toString())
                .child(id)

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
            result.invoke(UiState.Error(e.localizedMessage))
        }
    }

    override suspend fun getAttendance(
        result: (UiState<List<Attendance>>) -> Unit
    ) {
        // get attendance from firebase realtime database where id field is equal to current user id
        try {
            val reference = database.getReference(FirebaseRealtimeDatabaseConstants.ATTENDANCE)
                .child(today.toString())

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
            result.invoke(UiState.Error(e.localizedMessage))
        }
    }
}