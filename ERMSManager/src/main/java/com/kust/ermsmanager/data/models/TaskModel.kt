package com.kust.ermsmanager.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskModel (
    var id: Int = 0,
    var name: String = "",
    var description: String = "",
    var status: String = "",
    var priority: String = "",
    var deadline: String = "",
    var created: String = "",
    var updated: String = "",
    var creator: String = "",
    var assignee: String = "",
    var project: String = "",
    var creator_id: Int = 0,
    var assignee_id: Int = 0
) : Parcelable