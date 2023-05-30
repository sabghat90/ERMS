package com.kust.erms_company.data.repositroy

import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.UiState

interface EmployeeRepository {

    fun addEmployee(email : String, employee: Employee, result: (UiState<Pair<Employee, String>>) -> Unit)
    fun updateEmployee(employee: Employee, result: (UiState<Pair<Employee, String>>) -> Unit)
    fun removeEmployee(employee: Employee, result: (UiState<String>) -> Unit)
    fun getEmployeeList(employeeList : Employee?, result: (UiState<List<Employee>>) -> Unit)
}