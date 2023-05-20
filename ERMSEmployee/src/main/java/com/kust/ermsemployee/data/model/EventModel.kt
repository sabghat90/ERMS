package com.kust.ermsemployee.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventModel(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val dateCreated: String = "",
    val timeCreated: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val image: String = "",
    val type: String = "",
    var companyId: String = "",
) : Parcelable
