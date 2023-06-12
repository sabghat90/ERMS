package com.kust.ermsmanager.data.repositories

import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.kust.ermslibrary.models.Company
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.FireStoreCollectionConstants
import com.kust.ermslibrary.utils.SharedPreferencesConstants
import com.kust.ermslibrary.utils.UiState
import kotlinx.coroutines.tasks.await

class CompanyRepositoryImpl(
    private val database: FirebaseFirestore,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : CompanyRepository {
    override suspend fun getCompanyProfile(result: (UiState<List<Company>>) -> Unit) {
        result(UiState.Loading)
        // get companyId from shared preferences
        val employeeJson = sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION, null)
        val employee = Gson().fromJson(employeeJson, Employee::class.java)
        val companyId = employee.companyId
        try {
            val docRef = database.collection(FireStoreCollectionConstants.USERS)
                .document(companyId!!)
            docRef.get().await().let {
                if (it.exists()) {
                    val company = it.toObject(Company::class.java)
                    result(UiState.Success(listOf(company!!)))
                } else {
                    result(UiState.Error("Company not found"))
                }
            }
        } catch (e: Exception) {
            result(UiState.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }
}