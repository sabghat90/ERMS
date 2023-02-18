package com.kust.erms_company.data.repositroy

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kust.erms_company.data.model.CompanyModel
import com.kust.erms_company.utils.FireStoreCollection
import com.kust.erms_company.utils.UiState
import javax.inject.Inject

class CompanyRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore
) : CompanyRepository {

    override fun getCompanyDetails(companyId: String, result: (UiState<CompanyModel>) -> Unit) {

        auth.uid?.let { it ->
            database.collection(FireStoreCollection.COMPANY).document(it).get()
                .addOnSuccessListener { document ->
                    val company = document.toObject(CompanyModel::class.java)
                    result.invoke(UiState.Success(company!!))
                }.addOnFailureListener {
                    result(UiState.Error(it.message.toString()))
                }
        }
    }

    override fun updateCompanyDetails(
        companyId: String,
        companyModel: CompanyModel,
        result: (UiState<CompanyModel>) -> Unit
    ) {
        val document = database.collection(FireStoreCollection.COMPANY).document(companyId)
        document.set(companyModel).addOnSuccessListener {
            result.invoke(UiState.Success(companyModel))
        }.addOnFailureListener {
            result(UiState.Error(it.message.toString()))
        }
    }
}