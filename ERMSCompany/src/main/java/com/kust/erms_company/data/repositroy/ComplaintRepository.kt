package com.kust.erms_company.data.repositroy

import com.kust.ermslibrary.models.Complaint
import com.kust.ermslibrary.utils.UiState

interface ComplaintRepository {
    suspend fun getComplaints(result: (UiState<List<Complaint>>) -> Unit)
    suspend fun updateComplaint(complaint: Complaint, historyModel: ComplaintHistoryModel, result: (UiState<String>) -> Unit)
    suspend fun deleteComplaint(complaint: Complaint, result: (UiState<String>) -> Unit)
    suspend fun getComplaintHistory(id: String, result: (UiState<List<ComplaintHistoryModel>>) -> Unit)
}