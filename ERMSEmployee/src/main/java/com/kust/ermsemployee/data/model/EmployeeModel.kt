package com.kust.ermsemployee.data.model

data class EmployeeModel(
    var id: String = "-",
    val name: String = "-",
    var email: String = "-",
    val phone: String = "-",
    val address: String = "-",
    val city: String = "-",
    val country: String = "-",
    val companyName: String = "-",
    var companyId: String = "-",
    val designation: String = "-",
    val salary: String = "-",
    val points: String = "-",
    val image: Int = 0,
    var role: String = "-"
)
