package com.kust.ermsemployee.data.repository

import android.content.SharedPreferences
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.FireStoreCollectionConstants
import com.kust.ermslibrary.utils.Role
import com.kust.ermslibrary.utils.SharedPreferencesConstants
import com.kust.ermslibrary.utils.UiState

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val firebaseMessaging: FirebaseMessaging
) : AuthRepository {

    override fun login(email: String, password: String, result: (UiState<String>) -> Unit) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.user?.let {
                        validateUser(it.uid).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val isEmployee = task.result
                                if (isEmployee != null && isEmployee) {
                                    // store FCM token by calling storeFCMToken
                                    storeFCMToken(it.uid) { uiState ->
                                        when (uiState) {
                                            is UiState.Error -> {
                                                result.invoke(UiState.Error(uiState.error))
                                            }

                                            UiState.Loading -> {
                                                result.invoke(UiState.Loading)
                                            }

                                            is UiState.Success -> {
                                                // store user session by calling storeUserSession
                                                storeUserSession(it.uid) {
                                                    result.invoke(UiState.Success("Employee logged in"))
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    auth.signOut()
                                    result(UiState.Error("Invalid user"))
                                }
                            } else {
                                auth.signOut()
                                result(UiState.Error("Invalid user"))
                            }
                        }
                    }
                } else {
                    try {
                        throw task.exception ?: java.lang.Exception("Invalid authentication")
                    } catch (e: FirebaseAuthInvalidUserException) {
                        result(UiState.Error("User does not exist"))
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        result(UiState.Error("Invalid email"))
                    } catch (e: Exception) {
                        result(UiState.Error(e.localizedMessage ?: "Unknown error"))
                    }
                }
            }
    }

    override fun storeFCMToken(id: String, result: (UiState<String>) -> Unit) {
        firebaseMessaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val docRef = database.collection(FireStoreCollectionConstants.USERS)
                    .document(id)
                docRef.update("fcmToken", task.result)
                    .addOnSuccessListener {
                        result(UiState.Success("Successfully stored the token"))
                    }
                    .addOnFailureListener {
                        result(UiState.Error(it.localizedMessage ?: "Error to store the token"))
                    }
            } else {
                result(UiState.Error("Error to get the token"))
            }
        }
    }

    override fun signUp(
        email: String,
        password: String,
        employee: Employee,
        result: (UiState<String>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    employee.id = task.result.user?.uid ?: ""
                    employee.role = Role.EMPLOYEE
                    updateEmployeeInfo(employee) { uiState ->
                        when (uiState) {
                            is UiState.Error -> {
                                result.invoke(UiState.Error(uiState.error))
                            }

                            UiState.Loading -> {
                                result.invoke(UiState.Loading)
                            }

                            is UiState.Success -> {
                                result.invoke(UiState.Success("Employee created"))
                            }
                        }
                    }
                } else {
                    try {
                        throw task.exception ?: java.lang.Exception("Invalid authentication")
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        result(UiState.Error("Weak password"))
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        result(UiState.Error("Invalid email"))
                    } catch (e: FirebaseAuthUserCollisionException) {
                        result(UiState.Error("User already exists"))
                    } catch (e: Exception) {
                        result(UiState.Error(e.localizedMessage ?: "Unknown error"))
                    }
                }
            }
    }

    override fun updateEmployeeInfo(
        employee: Employee,
        result: (UiState<String>) -> Unit
    ) {
        val dbRef =
            database.collection(FireStoreCollectionConstants.USERS).document(employee.id)

        dbRef.set(employee)
            .addOnSuccessListener {
                result.invoke(UiState.Success("Employee Updated"))
            }
            .addOnFailureListener {
                result.invoke(UiState.Error(it.localizedMessage))
            }
    }

    override fun validateUser(id: String): Task<Boolean> {
        val docRef = database.collection(FireStoreCollectionConstants.USERS).document(id)
        return docRef.get().continueWith { task ->
            val document = task.result
            if (document != null) {
                val role = document.getString("role")
                return@continueWith role == Role.EMPLOYEE
            } else {
                return@continueWith false
            }
        }
    }

    override fun forgotPassword(email: String, result: (UiState<String>) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result(UiState.Success("Email sent"))
                } else {

                    try {
                        throw task.exception ?: java.lang.Exception("Invalid authentication")
                    } catch (e: FirebaseAuthInvalidUserException) {
                        result(UiState.Error("User does not exist"))
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        result(UiState.Error("Invalid email"))
                    } catch (e: Exception) {
                        result(UiState.Error(e.localizedMessage ?: "Unknown error"))
                    }
                }
            }
    }

    override fun logout(result: () -> Unit) {
        auth.signOut()
        // clear user session
        sharedPreferences.edit().clear().apply()
        result()
    }

    override fun isUserLoggedIn(): Boolean {
        // check if user is logged in by checking if the user session is stored
        val user = sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION, null)
        return user != null
    }

    override fun storeUserSession(email: String, result: (Employee?) -> Unit) {
        val docRef = database.collection(FireStoreCollectionConstants.USERS).document(email)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val employee = document.toObject(Employee::class.java)
                val editor = sharedPreferences.edit()
                editor.putString(SharedPreferencesConstants.USER_SESSION, gson.toJson(employee))
                editor.apply()
                result.invoke(employee)
            } else {
                result.invoke(null)
            }
        }.addOnFailureListener {
            result.invoke(null)
        }
    }

    override fun getUserSession(result: (Employee?) -> Unit) {
        val user = sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION, null)
        if (user != null) {
            val employee = gson.fromJson(user, Employee::class.java)
            result(employee)
        } else {
            result.invoke(null)
        }
    }

    override fun changePassword(newPassword: String, result: (UiState<String>) -> Unit) {
        val user = auth.currentUser
        user?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result(UiState.Success("Password changed successfully"))
                } else {
                    try {
                        throw task.exception ?: java.lang.Exception("Invalid authentication")
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        result(UiState.Error("Weak password"))
                    } catch (e: Exception) {
                        result(UiState.Error(e.localizedMessage ?: "Unknown error"))
                    }
                }
            }
    }
}