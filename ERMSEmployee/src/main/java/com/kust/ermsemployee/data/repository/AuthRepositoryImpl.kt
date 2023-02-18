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

//    override fun registerCompany(
//        email: String,
//        password: String,
//        companyModel: CompanyModel,
//        result: (UiState<String>) -> Unit
//    ) {
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    companyModel.id = task.result.user?.uid ?: ""
//                    updateCompanyInformation(companyModel) { uiState ->
//                        when (uiState) {
//                            is UiState.Success -> result(UiState.Success(uiState.data))
//                            is UiState.Error -> result(UiState.Error(uiState.error))
//                            is UiState.Loading -> result(UiState.Loading)
//                        }
//                    }
//                }
//                else {
//                    try {
//                        throw task.exception ?: java.lang.Exception("Invalid authentication")
//                    } catch (e: FirebaseAuthWeakPasswordException) {
//                        result(UiState.Error("Weak password"))
//                    } catch (e: FirebaseAuthInvalidCredentialsException) {
//                        result(UiState.Error("Invalid email"))
//                    } catch (e: FirebaseAuthUserCollisionException) {
//                        result(UiState.Error("User already exists"))
//                    } catch (e: Exception) {
//                        result(UiState.Error("Unknown error"))
//                    }
//                }
//            }
//    }

    override fun login(email: String, password: String, result: (UiState<String>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                val id = auth.currentUser?.uid ?: ""
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
    }

    override fun validateUser(id : String): Boolean {

        val employeeRef =
            database.collection(FireStoreCollection.EMPLOYEE).document(id)
        val companyRef =
            database.collection(FireStoreCollection.COMPANY).document(id)

        employeeRef.get()
            .addOnSuccessListener {
                if (it.getString("role") != "employee") {
                    return@addOnSuccessListener
                }
            }

        companyRef.get()
            .addOnSuccessListener {
                if (it.getString("role") != "employee") {
                    return@addOnSuccessListener
                }
            }

        return false
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

//    override fun updateCompanyInformation(companyModel: CompanyModel, result: (UiState<String>) -> Unit) {
//
//        val documentReference = database.collection(FireStoreCollection.COMPANY).document(companyModel.id)
//
//        documentReference
//            .set(companyModel)
//            .addOnSuccessListener {
//                result.invoke(UiState.Success("CompanyModel updated successfully"))
//            }
//            .addOnFailureListener {
//                result.invoke(UiState.Error("Error updating companyModel"))
//            }
//    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }


}