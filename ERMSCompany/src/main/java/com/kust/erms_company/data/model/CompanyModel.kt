package com.kust.erms_company.data.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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
