package com.kust.ermslibrary.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Employee(
    var id: String = "",
    var name: String = "",
    val employeeId : String = "",
    var email: String = "",
    var phone: String = "",
    var gender: String = "",
    var dob: String = "",
    var address: String = "",
    var city: String = "",
    var state: String = "",
    val country: String = "",
    val department: String = "",
    var companyId: String = "",
    val jobTitle: String = "",
    val salary: Double = 0.00,
    val joiningDate: String = "",
    val points: Double = 0.00,
    var role: String = "",
    var website: String = "",
    val profilePicture: String = "",
    var fcmToken: String = ""
): Parcelable
