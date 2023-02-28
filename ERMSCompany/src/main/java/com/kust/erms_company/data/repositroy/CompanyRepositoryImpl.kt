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
    override fun getCompanyDetails(
        companyModel: CompanyModel,
        result: (UiState<List<CompanyModel>>) -> Unit
    ) {
        val email = auth.currentUser?.email
        val document = database.collection(FireStoreCollection.COMPANY).document(email!!)
        document.get().addOnSuccessListener {
            val company = it.toObject(CompanyModel::class.java)
            val list = arrayListOf<CompanyModel>()
            list.add(company!!)
            result.invoke(UiState.Success(list))
        }.addOnFailureListener {
            result(UiState.Error(it.message.toString()))
        }
    }

    override fun updateCompanyDetails(
        companyId: String,
        companyModel: CompanyModel,
        result: (UiState<Pair<CompanyModel, String>>) -> Unit
    ) {
        val document = database.collection(FireStoreCollection.COMPANY).document(companyModel.email)
        document.set(companyModel).addOnSuccessListener {
            result.invoke(UiState.Success(Pair(companyModel, "Company updated successfully")))
        }.addOnFailureListener {
            result(UiState.Error(it.message.toString()))
        }
    }
}