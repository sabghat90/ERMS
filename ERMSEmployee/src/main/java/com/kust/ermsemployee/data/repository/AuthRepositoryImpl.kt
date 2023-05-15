package com.kust.ermsemployee.data.repository

import android.content.SharedPreferences
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.kust.ermsemployee.data.model.EmployeeModel
import com.kust.ermsemployee.utils.FireStoreCollectionConstants
import com.kust.ermsemployee.utils.Role
import com.kust.ermsemployee.utils.SharedPreferencesConstants
import com.kust.ermsemployee.utils.UiState

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
                        result(UiState.Error("Unknown error"))
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
                        result(UiState.Error("Error to store the token"))
                    }
            } else {
                result(UiState.Error("Error to store the token"))
            }
        }
    }

    override fun signUp(
        email: String,
        password: String,
        employeeModel: EmployeeModel,
        result: (UiState<String>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    employeeModel.id = task.result.user?.uid ?: ""
                    employeeModel.role = Role.EMPLOYEE
                    updateEmployeeInfo(employeeModel) { uiState ->
                        when (uiState) {
                            is UiState.Error -> {
                                result.invoke(UiState.Error(uiState.error))
                            }

                            UiState.Loading -> {
                                result.invoke(UiState.Loading)
                            }

                            is UiState.Success -> {
                                storeUserSession(employeeModel.id) {
                                    result.invoke(UiState.Success("Employee created"))
                                }
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
                        result(UiState.Error("Unknown error"))
                    }
                }
            }
    }

    override fun updateEmployeeInfo(
        employeeModel: EmployeeModel,
        result: (UiState<String>) -> Unit
    ) {
        val dbRef =
            database.collection(FireStoreCollectionConstants.USERS).document(employeeModel.id)

        dbRef.set(employeeModel)
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
                return@continueWith (role == Role.EMPLOYEE)
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
                        result(UiState.Error("Unknown error"))
                    }
                }
            }
    }

    override fun logout(result: () -> Unit) {
        auth.signOut()
        // clear user session
        val editor = sharedPreferences.edit()
        editor.remove(SharedPreferencesConstants.USER_SESSION)
        editor.apply()
        result()
    }

    override fun isUserLoggedIn(): Boolean {
        // check if user is logged in by checking if the user session is stored
        val user = sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION, null)
        return user != null
    }

    override fun storeUserSession(email: String, result: (EmployeeModel?) -> Unit) {
        val docRef = database.collection(FireStoreCollectionConstants.USERS).document(email)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val employee = document.toObject(EmployeeModel::class.java)
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

    override fun getUserSession(result: (EmployeeModel?) -> Unit) {
        val user = sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION, null)
        if (user != null) {
            val employee = gson.fromJson(user, EmployeeModel::class.java)
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
                        result(UiState.Error("Unknown error"))
                    }
                }
            }
    }
}