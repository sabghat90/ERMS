package com.kust.ermsemployee.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kust.ermsemployee.data.model.ComplaintHistoryModel
import com.kust.ermsemployee.data.model.ComplaintModel
import com.kust.ermsemployee.utils.FireStoreCollectionConstants
import com.kust.ermsemployee.utils.UiState
import kotlinx.coroutines.tasks.await

class ComplaintRepositoryImpl(
    private val database: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ComplaintRepository {
    override suspend fun createComplaint(
        complaintModel: ComplaintModel,
        complaintHistoryModel: ComplaintHistoryModel,
        result: (UiState<String>) -> Unit
    ) {
        // use coroutines to create complaint
        return try {
            // create complaint
            val docRef = database.collection(FireStoreCollectionConstants.COMPLAINTS)
                .document()
            val complaintId = docRef.id
            complaintModel.id = complaintId
            docRef.set(complaintModel).await()

            val historyRef = database
                .collection(FireStoreCollectionConstants.COMPLAINTS)
                .document(complaintId)
                .collection(FireStoreCollectionConstants.COMPLAINT_HISTORY)
                .document()
            val historyId = historyRef.id
            complaintHistoryModel.id = historyId
            historyRef.set(complaintHistoryModel).await()

            // return success
            result(UiState.Success("Complaint created successfully"))
        } catch (e: Exception) {
            result(UiState.Error(e.message.toString()))
        }
    }

    override suspend fun getComplaints(result: (UiState<List<ComplaintModel>>) -> Unit) {
        // use coroutines to get complaints
        return try {
            // get complaints
            val complaints = database.collection(FireStoreCollectionConstants.COMPLAINTS)
                .whereEqualTo("employeeId", auth.currentUser?.uid)
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
        result: (UiState<Pair<ComplaintModel,String>>) -> Unit
    ) {
        TODO("Not yet implemented")
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