package com.kust.ermsmanager.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskModel (
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var status: String = "",
    var deadline: String = "",
    var createdDate: String = "",
    var createdBy: String = "",
    var assigneeName: String = "",
    var assigneeEmail: String = "",
    var assigneeId: String = "",
) : Parcelable