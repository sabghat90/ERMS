package com.kust.ermsmanager.data.repositories

import com.kust.ermslibrary.models.Complaint
import com.kust.ermslibrary.models.ComplaintHistory
import com.kust.ermslibrary.utils.UiState

interface ComplaintRepository {
    suspend fun getComplaints(result: (UiState<List<Complaint>>) -> Unit)
    suspend fun updateComplaint(complaint: Complaint, historyModel: ComplaintHistory, result: (UiState<String>) -> Unit)
    suspend fun getComplaintHistory(id: String, result: (UiState<List<ComplaintHistory>>) -> Unit)
}