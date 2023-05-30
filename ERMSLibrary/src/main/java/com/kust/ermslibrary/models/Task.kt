package com.kust.ermslibrary.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task (
    var id: String = "",
    val name: String = "",
    val description: String = "",
    var status: String = "",
    val deadline: String = "",
    val createdDate: String = "",
    val assigneeName: String = "",
    val assigneeEmail: String = "",
    val assigneeId: String = "",
    val companyId : String = "",
    val managerId : String = "",
    val managerName : String = "",
    val managerFCMToken : String = "",
    val assigneeFCMToken : String = "",
) : Parcelable