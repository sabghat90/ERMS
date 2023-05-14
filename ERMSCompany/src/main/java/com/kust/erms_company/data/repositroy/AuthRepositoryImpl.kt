package com.kust.erms_company.data.repositroy

import android.content.SharedPreferences
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.kust.erms_company.data.model.CompanyModel
import com.kust.erms_company.utils.FireStoreCollectionConstants
import com.kust.erms_company.utils.Role
import com.kust.erms_company.utils.SharedPreferencesConstants
import com.kust.erms_company.utils.UiState

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val firebaseMessaging: FirebaseMessaging
) : AuthRepository {

    override fun registerCompany(
        email: String,
        password: String,
        companyModel: CompanyModel,
        result: (UiState<String>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    companyModel.id = task.result.user?.uid ?: ""
                    companyModel.role = Role.COMPANY
                    updateCompanyInformation(companyModel) { uiState ->
                        when (uiState) {
                            is UiState.Success -> result(UiState.Success("Registration successful"))
                            is UiState.Error -> result(UiState.Error(uiState.error))
                            is UiState.Loading -> result(UiState.Loading)
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

    override fun loginCompany(email: String, password: String, result: (UiState<String>) -> Unit) {
        // first login the user and then check if it is a company or not from validateUser function
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.user?.let {
                        validateUser(it.uid).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val isCompany = task.result
                                if (isCompany != null && isCompany) {
                                    storeUserSession(it.uid) { companyModel ->
                                        if (companyModel != null) {
                                            result(UiState.Success("Login successful"))
                                        } else {
                                            auth.signOut()
                                            result(UiState.Error("Invalid user"))
                                        }
                                    }
                                } else {
                                    auth.signOut()
                                    result(UiState.Error("Invalid user"))
                                }
                            } else {
                                auth.signOut()
                                result(UiState.Error("Invalid user"))
                            }
                        }
                    }
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

    override fun validateUser(id: String): Task<Boolean> {
        val docRef = database.collection(FireStoreCollectionConstants.USERS).document(id)
        return docRef.get().continueWith { task ->
            val document = task.result
            if (document != null) {
                val role = document.getString("role")
                return@continueWith (role == Role.COMPANY)
            } else {
                return@continueWith false
            }
        }
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

    override fun logoutCompany(result: () -> Unit) {
        auth.signOut()
        result()
        // clear user session
        val editor = sharedPreferences.edit()
        editor.remove(SharedPreferencesConstants.USER_SESSION)
        editor.apply()
    }

    override fun updateCompanyInformation(
        companyModel: CompanyModel,
        result: (UiState<String>) -> Unit
    ) {
        val documentReference =
            database.collection(FireStoreCollectionConstants.USERS).document(companyModel.id)
        documentReference
            .set(companyModel)
            .addOnSuccessListener {
                result.invoke(UiState.Success("CompanyModel updated successfully"))
            }
            .addOnFailureListener {
                result.invoke(UiState.Error("Error updating companyModel"))
            }
    }

    override fun isUserLoggedIn(): Boolean {
        // check if user is logged in or not from shared preferences
        val user = sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION, null)
        return user != null
    }

    override fun storeUserSession(id: String, result: (CompanyModel?) -> Unit) {
        val docRef = database.collection(FireStoreCollectionConstants.USERS).document(id)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val company = document.toObject(CompanyModel::class.java)
                val editor = sharedPreferences.edit()
                editor.putString(SharedPreferencesConstants.USER_SESSION, gson.toJson(company))
                editor.apply()
                result.invoke(company)
            } else {
                result.invoke(null)
            }
        }.addOnFailureListener {
            result.invoke(null)
        }
    }

    override fun getUserSession(result: (CompanyModel?) -> Unit) {
        val user = sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION, null)
        if (user != null) {
            val employee = gson.fromJson(user, CompanyModel::class.java)
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