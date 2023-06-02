package com.kust.erms_company.data.repositroy

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.FireStoreCollectionConstants
import com.kust.ermslibrary.utils.UiState

class EmployeeRepositoryImpl(
    auth: FirebaseAuth,
    private val database: FirebaseFirestore
) : EmployeeRepository {

    private val companyId = auth.currentUser?.uid

    override fun addEmployee(
        email: String,
        employee: Employee,
        result: (UiState<Pair<Employee, String>>) -> Unit
    ) {
        val dbRef = database.collection(FireStoreCollectionConstants.USERS)
            .whereEqualTo("email", email)

        val employeeHashMap = hashMapOf<String, Any>()

        employeeHashMap["companyId"] = companyId!!

        dbRef.get().addOnSuccessListener { it ->
            if (it.isEmpty) {
                result(UiState.Error("No user found with this email"))
            } else {
                val document = database.collection(FireStoreCollectionConstants.USERS)
                    .document(it.documents[0].id)

                // check if employee already added or not by checking companyId field in employee document
                val existingCompanyId = it.documents[0].data?.get("companyId") as? String

                if (!(existingCompanyId != null && existingCompanyId != companyId)) {
                    result(UiState.Error("Employee already added"))
                    return@addOnSuccessListener
                } else {
                    document.update(employeeHashMap).addOnSuccessListener {
                        result.invoke(
                            UiState.Success(
                                Pair(
                                    employee,
                                    "Employee added successfully"
                                )
                            )
                        )
                    }.addOnFailureListener {
                        result(UiState.Error(it.message.toString()))
                    }
                }
            }
        }.addOnFailureListener {
            result(UiState.Error(it.message.toString()))
        }
    }

    override fun updateEmployee(
        employee: Employee,
        result: (UiState<Pair<Employee, String>>) -> Unit
    ) {
        val document =
            database.collection(FireStoreCollectionConstants.USERS).document(employee.id)
        document.set(employee).addOnSuccessListener {
            result.invoke(UiState.Success(Pair(employee, "Employee updated successfully")))
        }.addOnFailureListener {
            result(UiState.Error(it.message.toString()))
        }
    }

    override fun removeEmployee(employee: Employee, result: (UiState<String>) -> Unit) {
        // remove employee by setting companyId field to null
        val employeeHashMap = hashMapOf<String, Any>()
        employeeHashMap["companyId"] = ""
        val document =
            database.collection(FireStoreCollectionConstants.USERS).document(employee.id)
        document.update(employeeHashMap).addOnSuccessListener {
            result.invoke(UiState.Success("Employee removed successfully"))
        }.addOnFailureListener {
            result(UiState.Error(it.message.toString()))
        }
    }

    override fun getEmployeeList(
        result: (UiState<List<Employee>>) -> Unit
    ) {

        database.collection(FireStoreCollectionConstants.USERS)
            .whereEqualTo("companyId", companyId)
            .get()
            .addOnSuccessListener {
                val list = arrayListOf<Employee>()
                for (document in it) {
                    val employee = document.toObject(Employee::class.java)
                    list.add(employee)
                }
                result.invoke(UiState.Success(list))
            }.addOnFailureListener {
                result(UiState.Error(it.message.toString()))
            }
    }
}