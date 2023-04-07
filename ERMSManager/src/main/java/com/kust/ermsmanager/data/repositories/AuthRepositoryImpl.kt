package com.kust.ermsmanager.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.utils.FireStoreCollection
import com.kust.ermsmanager.utils.Role
import com.kust.ermsmanager.utils.UiState

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore
) : AuthRepository {

    override fun login(email: String, password: String, result: (UiState<String>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result(UiState.Success("Login successful"))
                } else {
                    result(UiState.Error(task.exception?.message.toString()))
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
}