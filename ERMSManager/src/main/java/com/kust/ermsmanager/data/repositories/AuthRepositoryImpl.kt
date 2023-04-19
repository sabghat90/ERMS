package com.kust.ermsmanager.data.repositories

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.utils.FireStoreCollection
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
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storeUserSession(email) {
                        result(UiState.Success("Login successful"))
                    }
                } else {
                    // handle all the possible errors, in catch block handle all firebase exceptions
                    try {
                        throw task.exception ?: Exception("Unknown error")
                    } catch (e: Exception) {
                        result(UiState.Error(e.message.toString()))
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

    override fun validateUser(email: String): Boolean {
        var isValid = true
        val docRefEmployee = database.collection(FireStoreCollection.EMPLOYEE).document(email)
        val docRefCompany = database.collection(FireStoreCollection.COMPANY).document(email)

        docRefEmployee.get().addOnSuccessListener { document ->
            if (document != null) {
                val employee = document.toObject(EmployeeModel::class.java)
                if (employee?.role == Role.COMPANY) {
                    isValid = false
                }
            }
        }

        docRefCompany.get().addOnSuccessListener { document ->
            if (document != null) {
                val employee = document.toObject(EmployeeModel::class.java)
                if (employee?.role == Role.EMPLOYEE) {
                    isValid = false
                }
            }
        }

        return isValid
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override fun storeUserSession(email: String, result: (EmployeeModel?) -> Unit) {
        val docRef = database.collection(FireStoreCollection.EMPLOYEE).document(email)
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