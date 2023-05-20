package com.kust.ermsemployee.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.kust.ermsemployee.data.model.AttendanceModel
import com.kust.ermsemployee.utils.FirebaseRealtimeDatabaseConstants
import com.kust.ermsemployee.utils.UiState

class AttendanceRepositoryImpl(
    private val firebaseDatabase: FirebaseDatabase,
    private val auth: FirebaseAuth
) : AttendanceRepository {
    override fun getAttendance(attendanceModel: AttendanceModel, result: (UiState<List<AttendanceModel>>) -> Unit) {
        // get attendance from firebase realtime database where id field is equal to current user id
        val dbRef = firebaseDatabase.getReference(FirebaseRealtimeDatabaseConstants.ATTENDANCE)
            .orderByChild(auth.currentUser!!.uid)
            .equalTo(attendanceModel.id)

        dbRef.get().addOnSuccessListener {
            val attendanceList = mutableListOf<AttendanceModel>()
            for (data in it.children) {
                val attendance = data.getValue(AttendanceModel::class.java)
                attendanceList.add(attendance!!)
            }
            result(UiState.Success(attendanceList))
        }.addOnFailureListener {
            result(UiState.Error(it.message.toString()))
        }
    }
}