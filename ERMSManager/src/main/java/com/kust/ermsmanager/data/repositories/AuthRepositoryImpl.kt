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
        val documentRef = database.collection(FireStoreCollection.EMPLOYEE).document(email)
        documentRef.get().addOnSuccessListener {
            val employee = it.toObject(EmployeeModel::class.java)
            if (employee != null) {
                if (employee.role == Role.MANAGER) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                result(UiState.Success("Login successful"))
                            } else {
                                result(UiState.Error(task.exception?.message.toString()))
                            }
                        }
                } else {
                    result(UiState.Error("You are not a manager"))
                }
            } else {
                result(UiState.Error("User not found"))
            }
        }.addOnFailureListener {
            result(UiState.Error(it.message.toString()))
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
        TODO("Not yet implemented")
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}