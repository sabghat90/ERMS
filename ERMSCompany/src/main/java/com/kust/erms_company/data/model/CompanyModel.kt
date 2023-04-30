package com.kust.erms_company.data.model


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CompanyModel(
    var id: String = "",
    var name: String = "",
    val address: String = "",
    val city: String = "",
    var country: String = "",
    var email: String = "",
    var phone: String = "",
    var website: String = "",
    var role : String = "",
    val profilePicture : String = ""
) : Parcelable
