package com.kust.ermsmanager.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.utils.FireStoreCollection
import com.kust.ermsmanager.utils.Role
import com.kust.ermsmanager.utils.UiState

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore
) : AuthRepository {
    override fun signUp(email: String, password: String,employeeModel: EmployeeModel, result: (UiState<String>) -> Unit) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    employeeModel.id = task.result.user?.uid!!
                    employeeModel.role = Role.MANAGER
                    updateEmployeeInfo(employeeModel) {state ->
                        when (state) {
                            is UiState.Loading -> result.invoke(UiState.Loading)
                            is UiState.Success -> result(UiState.Success(state.data))
                            is UiState.Error -> result(UiState.Error(state.error))
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

    override fun updateEmployeeInfo(
        employeeModel: EmployeeModel,
        result: (UiState<String>) -> Unit
    ) {
        val docRef = database.collection(FireStoreCollection.EMPLOYEE).document(employeeModel.email)

        docRef
            .set(employeeModel)
            .addOnSuccessListener {
                result.invoke(UiState.Success("Sign Up Successfully"))
            }
            .addOnFailureListener {
                result.invoke(UiState.Error(it.localizedMessage))
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