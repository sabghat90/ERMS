package com.kust.erms_company.data.repositroy

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kust.erms_company.data.model.EmployeeModel
import com.kust.erms_company.utils.FireStoreCollection
import com.kust.erms_company.utils.UiState

class EmployeeRepositoryImpl(
    auth: FirebaseAuth,
    private val database: FirebaseFirestore
) : EmployeeRepository {

    private val companyId = auth.currentUser?.uid

    override fun addEmployee(
        email: String,
        employeeModel: EmployeeModel,
        result: (UiState<Pair<EmployeeModel, String>>) -> Unit
    ) {
        val dbRef = database.collection(FireStoreCollection.EMPLOYEE).document(employeeModel.email)
        dbRef.update(
            "companyId", companyId,
            "department", employeeModel.department,
            "salary", employeeModel.salary,
            "joiningDate", employeeModel.joiningDate
        )
            .addOnSuccessListener {
                result.invoke(UiState.Success(Pair(employeeModel, "Employee added successfully")))
            }
            .addOnFailureListener {
                result.invoke(UiState.Error(it.message.toString()))
            }
    }

    override fun updateEmployee(
        employeeModel: EmployeeModel,
        result: (UiState<Pair<EmployeeModel, String>>) -> Unit
    ) {
        val document =
            database.collection(FireStoreCollection.EMPLOYEE).document(employeeModel.email)
        document.set(employeeModel).addOnSuccessListener {
            result.invoke(UiState.Success(Pair(employeeModel, "Employee updated successfully")))
        }.addOnFailureListener {
            result(UiState.Error(it.message.toString()))
        }
    }

    override fun deleteEmployee(employeeModel: EmployeeModel, result: (UiState<String>) -> Unit) {
        val document =
            database.collection(FireStoreCollection.EMPLOYEE).document(employeeModel.email)
        document.delete().addOnSuccessListener {
            result.invoke(UiState.Success("Employee deleted successfully"))
        }.addOnFailureListener {
            result(UiState.Error(it.message.toString()))
        }
    }

    override fun getEmployeeList(
        employeeList: EmployeeModel?,
        result: (UiState<List<EmployeeModel>>) -> Unit
    ) {

        database.collection(FireStoreCollection.EMPLOYEE)
            .whereEqualTo("companyId", companyId)
            .get()
            .addOnSuccessListener {
                val list = arrayListOf<EmployeeModel>()
                for (document in it) {
                    val employee = document.toObject(EmployeeModel::class.java)
                    list.add(employee)
                }
                result.invoke(UiState.Success(list))
            }.addOnFailureListener {
                result(UiState.Error(it.message.toString()))
            }
    }
}