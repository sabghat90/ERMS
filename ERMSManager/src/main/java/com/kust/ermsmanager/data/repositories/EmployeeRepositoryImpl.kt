package com.kust.ermsmanager.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.utils.FireStoreCollection
import com.kust.ermsmanager.utils.UiState
import javax.inject.Inject

class EmployeeRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore
) : EmployeeRepository {
    override fun getEmployeeList(
        employeeList: EmployeeModel?,
        result: (UiState<List<EmployeeModel>>) -> Unit
    ) {
        // get current employee from database and store in employeeList
        val docRefEmployee = database.collection(FireStoreCollection.EMPLOYEE)
            .document(auth.currentUser?.email.toString())
        docRefEmployee.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val employeeList = document.toObject(EmployeeModel::class.java)

                    // get employee list where company id is equal to employee list company id
                    val docRef = database.collection(FireStoreCollection.EMPLOYEE)
                        .whereEqualTo("companyId", employeeList?.companyId)
                    docRef.get()
                        .addOnSuccessListener { documents ->
                            val employeeList = mutableListOf<EmployeeModel>()
                            for (document in documents) {
                                val employee = document.toObject(EmployeeModel::class.java)
                                employeeList.add(employee)
                            }
                            result.invoke(UiState.Success(employeeList))
                        }
                        .addOnFailureListener { exception ->
                            result.invoke(UiState.Error(exception.message.toString()))
                        }
                }
            }
    }

    override fun updateEmployee(
        employeeModel: EmployeeModel?,
        result: (UiState<Pair<EmployeeModel, String>>) -> Unit
    ) {
        val docRef = database.collection("employee").document(employeeModel?.id.toString())
        docRef.set(employeeModel!!)
            .addOnSuccessListener {
                result.invoke(UiState.Success(Pair(employeeModel, "Update Success")))
            }
            .addOnFailureListener { exception ->
                result.invoke(UiState.Error(exception.message.toString()))
            }
    }
}