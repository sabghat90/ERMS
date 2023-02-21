package com.kust.ermsemployee.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.kust.ermsemployee.utils.FireStoreCollection
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