package com.kust.erms_company.data.repositroy

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kust.ermslibrary.models.Company
import com.kust.ermslibrary.utils.FireStoreCollectionConstants
import com.kust.ermslibrary.utils.UiState
import javax.inject.Inject

class CompanyRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore
) : CompanyRepository {
    override fun getCompanyDetails(
        company: Company,
        result: (UiState<List<Company>>) -> Unit
    ) {
        val id = auth.currentUser?.uid
        val document = database.collection(FireStoreCollectionConstants.USERS).document(id!!)
        document.get().addOnSuccessListener {
            val company = it.toObject(Company::class.java)
            val list = arrayListOf<Company>()
            list.add(company!!)
            result.invoke(UiState.Success(list))
        }.addOnFailureListener {
            result(UiState.Error(it.message.toString()))
        }
    }

    override fun updateCompanyDetails(
        companyId: String,
        company: Company,
        result: (UiState<Pair<Company, String>>) -> Unit
    ) {
        val document = database.collection(FireStoreCollectionConstants.USERS).document(company.id)
        document.set(company).addOnSuccessListener {
            result.invoke(UiState.Success(Pair(company, "Company updated successfully")))
        }.addOnFailureListener {
            result(UiState.Error(it.message.toString()))
        }
    }
}