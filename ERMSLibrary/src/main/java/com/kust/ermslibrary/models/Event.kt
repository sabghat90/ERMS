package com.kust.ermslibrary.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val dateCreated: String = "",
    val eventDate: String = "",
    val location: String = "",
    val image: String = "",
    val type: String = "",
    var companyId: String = "",
) : Parcelable
