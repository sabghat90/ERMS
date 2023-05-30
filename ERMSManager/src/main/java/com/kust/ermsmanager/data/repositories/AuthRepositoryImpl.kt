package com.kust.ermsmanager.data.repositories

import android.content.SharedPreferences
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
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
        // first login to the firebase and then check if the user is a manager or not and then store the user session
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // check if the user is a manager or not
                    validateUser(auth.currentUser!!.uid).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (task.result!!) {
                                // store FCM token in the database by calling the storeFCMToken function
                                storeFCMToken(auth.currentUser!!.uid) { state ->
                                    if (state is UiState.Success) {
                                        // store the user session
                                        storeUserSession(auth.currentUser!!.uid) { employee ->
                                            if (employee != null) {
                                                result(UiState.Success("Successfully logged in"))
                                            } else {
                                                result(UiState.Error("Error"))
                                            }
                                        }
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
                return@continueWith role == Role.MANAGER
            } else {
                return@continueWith false
            }
        }
    }


    override fun isUserLoggedIn(): Boolean {
        // check if the user is logged in or not by checking the shared preferences
        val user = sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION, null)
        return user != null
    }

    override fun storeUserSession(id: String, result: (Employee?) -> Unit) {
        val docRef = database.collection(FireStoreCollectionConstants.USERS).document(id)
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
                        result(UiState.Error("Unknown error"))
                    }
                }
            }
    }
}