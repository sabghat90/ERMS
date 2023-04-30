package com.kust.ermsemployee.data.repository

import android.content.SharedPreferences
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
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
    private val gson: Gson
) : AuthRepository {

    override fun login(email: String, password: String, result: (UiState<String>) -> Unit) {

        validateUser(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val isValid = task.result
                if (isValid) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                result(UiState.Success("Login successful"))
                            } else {
                                try {
                                    throw task.exception ?: java.lang.Exception("Invalid authentication")
                                } catch (e: FirebaseAuthInvalidUserException) {
                                    result(UiState.Error("User does not exist"))
                                } catch (e: FirebaseAuthInvalidCredentialsException) {
                                    result(UiState.Error("Invalid email or password"))
                                } catch (e: Exception) {
                                    result(UiState.Error("Unknown error"))
                                }
                            }
                        }
                } else {
                    // if the user is not a company, return error
                    result(UiState.Error("User is not a employee"))
                }
            } else {
                result(UiState.Error("Failed to validate user"))
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
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    employeeModel.id = it.result.user?.uid ?: ""
                    employeeModel.role = Role.EMPLOYEE
                    updateEmployeeInfo(employeeModel) {uiState ->
                        when (uiState) {
                            is UiState.Error -> {
                                result.invoke(UiState.Error(uiState.error))
                            }
                            UiState.Loading -> {
                                result.invoke(UiState.Loading)
                            }
                            is UiState.Success -> {
                                result.invoke(UiState.Success(uiState.data))
                            }
                        }
                    }
                } else {
                    try {
                        throw it.exception ?: java.lang.Exception("Invalid authentication")
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
        val dbRef = database.collection(FireStoreCollectionConstants.USERS).document(employeeModel.id)

        dbRef.set(employeeModel)
            .addOnSuccessListener {
                result.invoke(UiState.Success("Employee Updated"))
            }
            .addOnFailureListener {
                result.invoke(UiState.Error(it.localizedMessage))
            }
    }

    override fun validateUser(email: String): Task<Boolean> {
        val docRef = database.collection(FireStoreCollectionConstants.COMPANY).document(email)
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
        result()
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
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
}