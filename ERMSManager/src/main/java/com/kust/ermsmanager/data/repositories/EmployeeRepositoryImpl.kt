package com.kust.ermsmanager.data.repositories

import android.content.SharedPreferences
import android.net.Uri
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.FireStoreCollectionConstants
import com.kust.ermslibrary.utils.SharedPreferencesConstants
import com.kust.ermslibrary.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class EmployeeRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val firebaseStorage: StorageReference,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : EmployeeRepository {
    override fun getEmployeeList(
        employee: Employee?,
        result: (UiState<List<Employee>>) -> Unit
    ) {
        // get company id from shared preference
        val employeeJson =
            sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION, null)
        val employeeObj = gson.fromJson(employeeJson, Employee::class.java)
        val companyId = employeeObj.companyId


        // get employee list where company id is equal to employee list company id
        val docRef = database.collection(FireStoreCollectionConstants.USERS)
            .whereEqualTo("companyId", companyId)
        docRef.get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Employee>()
                for (document in documents) {
                    val employee = document.toObject(Employee::class.java)
                    list.add(employee)
                }
                result.invoke(UiState.Success(list))
            }
            .addOnFailureListener { exception ->
                result.invoke(UiState.Error(exception.message.toString()))
            }
    }

    override fun updateEmployee(
        employee: Employee?,
        result: (UiState<Pair<Employee, String>>) -> Unit
    ) {
        val docRef = database.collection(FireStoreCollectionConstants.USERS)
            .document(auth.currentUser?.uid.toString())
        // update existing employee data
        val newEmployeeObj = hashMapOf(
            "name" to employee?.name,
            "phone" to employee?.phone,
            "gender" to employee?.gender,
            "dob" to employee?.dob,
            "city" to employee?.city,
            "state" to employee?.state,
            "country" to employee?.country,
            "profilePicture" to employee?.profilePicture
        )

        docRef.update(newEmployeeObj as Map<String, Any>)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(
                        Pair(
                            employee!!,
                            "Employee updated successfully"
                        )
                    )
                )
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

    override suspend fun addPoints(id: String): UiState<String> {
        val userRef = database.collection(FireStoreCollectionConstants.USERS).document(id)

        return try {
            val result = suspendCoroutine { continuation ->
                database.runTransaction { transaction ->
                    val documentSnapshot = transaction.get(userRef)
                    val points = documentSnapshot.getDouble("points") ?: 0.0

                    // Increment the points by 5
                    val newPoints = points + 5
                    transaction.update(userRef, "points", newPoints)
                }.addOnSuccessListener {
                    continuation.resume(UiState.Success("Successfully added points!"))
                }.addOnFailureListener { exception ->
                    continuation.resume(UiState.Error(exception.message.toString()))
                }
            }
            result
        } catch (exception: Exception) {
            UiState.Error(exception.message.toString())
        }
    }

    override suspend fun removePoints(id: String): UiState<String> {
        val userRef = database.collection(FireStoreCollectionConstants.USERS).document(id)

        return try {
            val result = suspendCoroutine { continuation ->
                database.runTransaction { transaction ->
                    val documentSnapshot = transaction.get(userRef)
                    val points = documentSnapshot.getDouble("points") ?: 0.0

                    // Decrement the points by 5
                    val newPoints = points - 5
                    transaction.update(userRef, "points", newPoints)
                }.addOnSuccessListener {
                    continuation.resume(UiState.Success("Successfully removed points!"))
                }.addOnFailureListener { exception ->
                    continuation.resume(UiState.Error(exception.message.toString()))
                }
            }
            result
        } catch (exception: Exception) {
            UiState.Error(exception.message.toString())
        }
    }
}