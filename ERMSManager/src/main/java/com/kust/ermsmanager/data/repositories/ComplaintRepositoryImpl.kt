package com.kust.ermsmanager.data.repositories

import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.kust.ermslibrary.models.Complaint
import com.kust.ermslibrary.models.ComplaintHistory
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.FireStoreCollectionConstants
import com.kust.ermslibrary.utils.SharedPreferencesConstants
import com.kust.ermslibrary.utils.UiState
import kotlinx.coroutines.tasks.await

class ComplaintRepositoryImpl(
    private val database: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val sharedPreferences: SharedPreferences
) : ComplaintRepository {

    private val employeeJson =
        sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION, null)
    val employee: Employee = Gson().fromJson(employeeJson, Employee::class.java)
    private val companyId = employee.companyId
    override suspend fun getComplaints(result: (UiState<List<Complaint>>) -> Unit) {
        return try {
            Log.d("Complaints", "Company Id: $companyId")
            val docRef = database.collection(FireStoreCollectionConstants.COMPLAINTS)
                .whereEqualTo("companyId", companyId)
                .whereEqualTo("referToManager", true)

            val complaints = docRef.get().await().toObjects(Complaint::class.java)
            Log.d("Complaints", complaints.toString())
            result.invoke(UiState.Success(complaints))
        } catch (e: Exception) {
            result.invoke(UiState.Error(e.localizedMessage))
        }
    }

    override suspend fun updateComplaint(complaint: Complaint, historyModel: ComplaintHistory, result: (UiState<String>) -> Unit) {
        return try {
            val docRef = database.collection(FireStoreCollectionConstants.COMPLAINTS)
                .document(complaint.id)
            docRef.set(complaint).await()

            val historyRef = database.collection(FireStoreCollectionConstants.COMPLAINTS)
                .document(complaint.id)
                .collection(FireStoreCollectionConstants.COMPLAINT_HISTORY)
                .document()
            historyModel.id = historyRef.id
            historyRef.set(historyModel).await()
            result.invoke(UiState.Success("Complaint Updated!"))
        } catch (e: Exception) {
            result.invoke(UiState.Error(e.localizedMessage))
        }
    }

    override suspend fun getComplaintHistory(
        id: String,
        result: (UiState<List<ComplaintHistory>>) -> Unit
    ) {
        return try {
            val docRef = database.collection(FireStoreCollectionConstants.COMPLAINTS)
                .document(id)
                .collection(FireStoreCollectionConstants.COMPLAINT_HISTORY)
                .orderBy("date", com.google.firebase.firestore.Query.Direction.ASCENDING)
            val complaintHistory = docRef.get().await().toObjects(ComplaintHistory::class.java)
            result.invoke(UiState.Success(complaintHistory))
        } catch (e: Exception) {
            result.invoke(UiState.Error(e.localizedMessage))
        }
    }
}