package com.kust.ermsmanager.data.repositories

import android.net.Uri
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.utils.FireStoreCollectionConstants
import com.kust.ermsmanager.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EmployeeRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val firebaseStorage: StorageReference
) : EmployeeRepository {
    override fun getEmployeeList(
        employeeList: EmployeeModel?,
        result: (UiState<List<EmployeeModel>>) -> Unit
    ) {
        // get employee list where company id is equal to employee list company id
        val docRef = database.collection(FireStoreCollectionConstants.USERS)
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

    override fun updateEmployee(
        employeeModel: EmployeeModel?,
        result: (UiState<Pair<EmployeeModel, String>>) -> Unit
    ) {
        val docRef = database.collection(FireStoreCollectionConstants.USERS)
            .document(auth.currentUser?.uid.toString())
        // update existing employee data
        val newEmployeeObj = hashMapOf(
            "name" to employeeModel?.name,
            "phone" to employeeModel?.phone,
            "gender" to employeeModel?.gender,
            "dob" to employeeModel?.dob,
            "city" to employeeModel?.city,
            "state" to employeeModel?.state,
            "country" to employeeModel?.country,
            "profilePicture" to employeeModel?.profilePicture
        )

        docRef.update(newEmployeeObj as Map<String, Any>)
            .addOnSuccessListener {
                result.invoke(UiState.Success(Pair(employeeModel!!, "Employee updated successfully")))
            }
            .addOnFailureListener { exception ->
                result.invoke(UiState.Error(exception.message.toString()))
            }
    }

    override suspend fun uploadImage(imageUri: Uri, result: (UiState<Uri>) -> Unit) {
        try {
            val uri: Uri = withContext(Dispatchers.IO) {
                firebaseStorage
                    .child(auth.currentUser?.uid.toString())
                    .putFile(imageUri)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            result.invoke(UiState.Success(uri))
        } catch (e: FirebaseException) {
            result.invoke(UiState.Error(e.message))
        } catch (e: Exception) {
            result.invoke(UiState.Error(e.message))
        }
    }
}