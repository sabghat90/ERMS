package com.kust.erms_company.data.repositroy

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kust.erms_company.data.model.ComplaintHistoryModel
import com.kust.erms_company.data.model.ComplaintModel
import com.kust.erms_company.utils.FireStoreCollectionConstants
import com.kust.erms_company.utils.UiState
import kotlinx.coroutines.tasks.await

class ComplaintRepositoryImpl(
    private val database: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ComplaintRepository {

    override suspend fun getComplaints(result: (UiState<List<ComplaintModel>>) -> Unit) {
        // use coroutines to get complaints
        return try {
            // get complaints
            val complaints = database.collection(FireStoreCollectionConstants.COMPLAINTS)
                .whereEqualTo("companyId", auth.currentUser?.uid)
                .get()
                .await()
                .toObjects(ComplaintModel::class.java)
            // return success
            result(UiState.Success(complaints))
        } catch (e: Exception) {
            result(UiState.Error(e.message.toString()))
        }
    }

    override suspend fun updateComplaint(
        complaintModel: ComplaintModel,
        historyModel: ComplaintHistoryModel,
        result: (UiState<String>) -> Unit
    ) {
        database.collection(FireStoreCollectionConstants.COMPLAINTS)
            .document(complaintModel.id)
            .set(complaintModel)
            .await()

        val historyRef = database.collection(FireStoreCollectionConstants.COMPLAINTS)
            .document(complaintModel.id)
            .collection(FireStoreCollectionConstants.COMPLAINT_HISTORY)
            .document()

        historyModel.id = historyRef.id

        historyRef.set(historyModel).await()

        result(UiState.Success("Complaint updated"))

    }

    override suspend fun deleteComplaint(
        complaintModel: ComplaintModel,
        result: (UiState<String>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getComplaintHistory(
        id: String,
        result: (UiState<List<ComplaintHistoryModel>>) -> Unit
    ) {
        return try {
            val historyList = database.collection(FireStoreCollectionConstants.COMPLAINTS)
                .document(id)
                .collection(FireStoreCollectionConstants.COMPLAINT_HISTORY)
                .get()
                .await()
                .toObjects(ComplaintHistoryModel::class.java)
            result(UiState.Success(historyList))
        } catch (e: Exception) {
            result(UiState.Error("Error to load history"))
        }
    }
}