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
        // Sign in with email and password and store user session in shared preferences if successful login
        validateUser(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val isValid = task.result
                if (isValid) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                storeUserSession(email) { employeeModel ->
                                    if (employeeModel != null) {
                                        result(UiState.Success("Login successful"))
                                    } else {
                                        result(UiState.Error("Failed to store user session"))
                                    }
                                }
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
                    result(UiState.Error("User is not a Manager"))
                }
            } else {
                result(UiState.Error("Failed to validate user"))
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

    override fun validateUser(email: String): Task<Boolean> {
        val docRef = database.collection(FireStoreCollectionConstants.USER).document(email)
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

    override fun storeUserSession(email: String, result: (EmployeeModel?) -> Unit) {
        val docRef = database.collection(FireStoreCollectionConstants.USER).document(email)
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