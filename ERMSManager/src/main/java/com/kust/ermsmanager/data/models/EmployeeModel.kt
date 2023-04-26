package com.kust.ermsmanager.data.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable


@Parcelize
data class EmployeeModel(
    var id: String = "",
    var name: String = "",
    val employeeId : String = "",
    var email: String = "",
    var phone: String = "",
    val gender: String = "",
    val dob: String = "",
    var address: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val department: String = "",
    var companyId: String = "",
    val jobTitle: String = "",
    val salary: String = "",
    val joiningDate: String = "",
    val points: String = "",
    var role: String = "",
    val profilePicture: String = ""
) : Parcelable
