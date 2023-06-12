package com.kust.erms_company.data.repositroy

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kust.ermslibrary.models.Complaint
import com.kust.ermslibrary.models.ComplaintHistory
import com.kust.ermslibrary.utils.FireStoreCollectionConstants
import com.kust.ermslibrary.utils.UiState
import kotlinx.coroutines.tasks.await

class ComplaintRepositoryImpl(
    private val database: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ComplaintRepository {

    override suspend fun getComplaints(result: (UiState<List<Complaint>>) -> Unit) {
        // use coroutines to get complaints
        return try {
            // get complaints
            val complaints = database.collection(FireStoreCollectionConstants.COMPLAINTS)
                .whereEqualTo("companyId", auth.currentUser?.uid)
                .get()
                .await()
                .toObjects(Complaint::class.java)
            // return success
            result(UiState.Success(complaints))
        } catch (e: Exception) {
            result(UiState.Error(e.message.toString()))
        }
    }

    override suspend fun updateComplaint(
        complaint: Complaint,
        historyModel: ComplaintHistory,
        result: (UiState<String>) -> Unit
    ) {
        database.collection(FireStoreCollectionConstants.COMPLAINTS)
            .document(complaint.id)
            .set(complaint)
            .await()
        val historyRef = database.collection(FireStoreCollectionConstants.COMPLAINTS)
            .document(complaint.id)
            .collection(FireStoreCollectionConstants.COMPLAINT_HISTORY)
            .document()
        historyModel.id = historyRef.id
        historyRef.set(historyModel).await()
        result(UiState.Success("Complaint updated"))
    }

    override suspend fun deleteComplaint(
        complaint: Complaint,
        result: (UiState<String>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getComplaintHistory(
        id: String,
        result: (UiState<List<ComplaintHistory>>) -> Unit
    ) {
        return try {
            val historyList = database.collection(FireStoreCollectionConstants.COMPLAINTS)
                .document(id)
                .collection(FireStoreCollectionConstants.COMPLAINT_HISTORY)
                .orderBy("date", com.google.firebase.firestore.Query.Direction.ASCENDING)
                .get()
                .await()
                .toObjects(ComplaintHistory::class.java)
            result(UiState.Success(historyList))
        } catch (e: Exception) {
            result(UiState.Error("Error to load history"))
        }
    }
}