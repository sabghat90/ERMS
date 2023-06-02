package com.kust.erms_company.data.repositroy

import android.net.Uri
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kust.ermslibrary.models.Company
import com.kust.ermslibrary.utils.FireStoreCollectionConstants
import com.kust.ermslibrary.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CompanyRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val storage: StorageReference
) : CompanyRepository {
    override fun getCompanyDetails(
        company: Company,
        result: (UiState<List<Company>>) -> Unit
    ) {
        val id = auth.currentUser?.uid
        val document = database.collection(FireStoreCollectionConstants.USERS).document(id!!)
        document.get().addOnSuccessListener {
            val companyList = it.toObject(Company::class.java)
            val list = arrayListOf<Company>()
            list.add(companyList!!)
            result.invoke(UiState.Success(list))
        }.addOnFailureListener {
            result(UiState.Error(it.message.toString()))
        }
    }

    override suspend fun updateCompanyDetails(
        company: Company,
        result: (UiState<Pair<Company, String>>) -> Unit
    ) {
        try {
            // update profile in firestore using update method to avoid overwriting the document
            val companyHashMap = hashMapOf<String, Any>()
            companyHashMap["name"] = company.name
            companyHashMap["phone"] = company.phone
            companyHashMap["address"] = company.address
            companyHashMap["website"] = company.website
            companyHashMap["city"] = company.city
            companyHashMap["state"] = company.state
            companyHashMap["country"] = company.country
            companyHashMap["fullAddress"] = company.fullAddress
            companyHashMap["profilePicture"] = company.profilePicture


            val id = auth.currentUser?.uid
            val document = database.collection(FireStoreCollectionConstants.USERS).document(id!!)
            document.update(companyHashMap).await()

            result.invoke(UiState.Success(Pair(company, "Company Details Updated Successfully")))
        } catch (e: FirebaseException) {
            result.invoke(UiState.Error(e.message))
        } catch (e: Exception) {
            result.invoke(UiState.Error(e.message))
        }
    }

    override suspend fun uploadProfilePicture(imageUri: Uri, result: (UiState<Uri>) -> Unit) {
        try {
            val uri: Uri = withContext(Dispatchers.IO) {
                storage
                    .child(auth.currentUser?.uid.toString())
                    .putFile(imageUri)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            result.invoke(UiState.Success(uri))
        } catch (e: FirebaseException) {
            result.invoke(UiState.Error(e.message))
        } catch (e: Exception) {
            result.invoke(UiState.Error(e.message))
        }
    }
}