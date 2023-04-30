package com.kust.ermsmanager.data.repositories

import android.content.SharedPreferences
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.utils.FireStoreCollectionConstants
import com.kust.ermsmanager.utils.Role
import com.kust.ermsmanager.utils.SharedPreferencesConstants
import com.kust.ermsmanager.utils.UiState

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : AuthRepository {

    override fun login(email: String, password: String, result: (UiState<String>) -> Unit) {
        // first login to the firebase and then check if the user is a manager or not and then store the user session
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // check if the user is a manager or not
                    validateUser(auth.currentUser!!.uid).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (task.result!!) {
                                // store the user session
                                storeUserSession(auth.currentUser!!.uid) { employee ->
                                    if (employee != null) {
                                        result(UiState.Success(employee.role))
                                    } else {
                                        result(UiState.Error("Error"))
                                    }
                                }
                            } else {
                                result(UiState.Error("You are not a manager"))
                            }
                        } else {
                            result(UiState.Error("Error"))
                        }
                    }
                } else {
                    when (task.exception) {
                        is FirebaseAuthInvalidUserException -> {
                            result(UiState.Error("User not found"))
                        }

                        is FirebaseAuthInvalidCredentialsException -> {
                            result(UiState.Error("Invalid credentials"))
                        }

                        else -> {
                            result(UiState.Error("Error"))
                        }
                    }
                }
            }
    }

    override fun forgotPassword(email: String, result: (UiState<String>) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result(UiState.Success("Email sent"))
                } else {
                    result(UiState.Error(task.exception?.message.toString()))
                }
            }
    }

    override fun logout(result: () -> Unit) {
        auth.signOut()
        // clear the shared preferences
        sharedPreferences.edit().clear().apply()
        result()
    }

    override fun validateUser(id: String): Task<Boolean> {
        val docRef = database.collection(FireStoreCollectionConstants.USERS).document(id)
        return docRef.get().continueWith { task ->
            val document = task.result
            if (document != null) {
                val role = document.getString("role")
                return@continueWith (role == Role.MANAGER)
            } else {
                return@continueWith false
            }
        }
    }


    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override fun storeUserSession(id: String, result: (EmployeeModel?) -> Unit) {
        val docRef = database.collection(FireStoreCollectionConstants.USERS).document(id)
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