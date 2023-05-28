package com.kust.erms_company.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class ComplaintModel(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val status: String = "",
    val dateCreated: String = "",
    val dateUpdated: String = "",
    val dateClosed: String = "",
    val companyId: String = "",
    val employeeId: String = "",
    val employeeFCMToken: String = "",
    val employeeName: String = "",
    val ManagerFCMToken: String = "",
    val isReferToManager: Boolean = false,
) : Parcelable
