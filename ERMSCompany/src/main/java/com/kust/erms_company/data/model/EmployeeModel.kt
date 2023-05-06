package com.kust.erms_company.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class EmployeeModel(
    var id: String = "",
    val name: String = "",
    val employeeId : String = "",
    val email: String = "",
    val phone: String = "",
    val gender: String = "",
    val dob: String = "",
    val address: String = "",
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
