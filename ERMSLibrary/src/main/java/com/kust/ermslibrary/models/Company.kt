package com.kust.ermslibrary.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Company(
    var id: String = "",
    var name: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    var country: String = "",
    var fullAddress: String = "",
    var email: String = "",
    var phone: String = "",
    var website: String = "",
    var role : String = "",
    val profilePicture : String = "",
    var fcmToken : String = ""
): Parcelable
