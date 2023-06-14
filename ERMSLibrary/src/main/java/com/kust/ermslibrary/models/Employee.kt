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
    var fullAddress: String = "",
    val country: String = "",
    var department: String = "",
    var companyId: String = "",
    var jobTitle: String = "",
    var salary: Double = 0.00,
    val joiningDate: String = "",
    val points: Double = 0.00,
    var role: String = "",
    var website: String = "",
    val profilePicture: String = "",
    var fcmToken: String = ""
): Parcelable
