package com.kust.ermslibrary.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Complaint(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    var status: String = "",
    val dateCreated: String = "",
    var dateUpdated: String = "",
    val dateClosed: String = "",
    val companyId: String = "",
    val employeeId: String = "",
    val employeeFCMToken: String = "",
    val employeeName: String = "",
    val ManagerFCMToken: String = "",
    var isReferToManager: Boolean = false,
    var managerFeedBack: String = "",
    var employeeFeedBack: String = "",
    var companyFeedBack: String = "",
) : Parcelable