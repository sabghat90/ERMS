package com.kust.ermsemployee.data.repository

import androidx.compose.ui.semantics.Role
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.kust.ermsemployee.data.model.EmployeeModel
import com.kust.ermsemployee.utils.FireStoreCollection
import com.kust.ermsemployee.utils.FirebaseStorageConstants
import com.kust.ermsemployee.utils.UiState

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore
) : AuthRepository {

    override fun login(email: String, password: String, result: (UiState<String>) -> Unit) {

        if (!validateUser(email)) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    auth.currentUser?.uid ?: ""
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
            result(UiState.Error("User not an employee"))
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
                    employeeModel.role = com.kust.ermsemployee.utils.Role.EMPLOYEE
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
        val dbRef = database.collection(FireStoreCollection.EMPLOYEE).document(employeeModel.email)

        dbRef.set(employeeModel)
            .addOnSuccessListener {
                result.invoke(UiState.Success("Employee Updated"))
            }
            .addOnFailureListener {
                result.invoke(UiState.Error(it.localizedMessage))
            }
    }

    override fun validateUser(email : String): Boolean {
        var isValid = false

        val documentReference = database.collection(FireStoreCollection.EMPLOYEE).document()
        documentReference.collection(FireStoreCollection.EMPLOYEE).whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { document ->
                val role = document.documents[0].get("role").toString()
                isValid = role == "employee"
            }
            .addOnFailureListener {
                isValid = false
            }

        return isValid
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
}