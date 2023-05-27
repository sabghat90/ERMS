package com.kust.ermsemployee.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class ComplaintModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val status: String = "",
    val dateCreated: Timestamp = Timestamp.now(),
    val dateUpdated: Timestamp = Timestamp.now(),
    val dateClosed: Timestamp = Timestamp.now(),
    val companyId: String = "",
    val employeeId : String = "",
    val employeeFCMToken : String = "",
    val employeeName : String = "",
) : Parcelable
