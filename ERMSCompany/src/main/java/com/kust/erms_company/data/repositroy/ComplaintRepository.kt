package com.kust.erms_company.data.repositroy

import com.kust.erms_company.data.model.ComplaintHistoryModel
import com.kust.erms_company.data.model.ComplaintModel
import com.kust.erms_company.utils.UiState

interface ComplaintRepository {
    suspend fun getComplaints(result: (UiState<List<ComplaintModel>>) -> Unit)
    suspend fun updateComplaint(complaintModel: ComplaintModel, historyModel: ComplaintHistoryModel, result: (UiState<String>) -> Unit)
    suspend fun deleteComplaint(complaintModel: ComplaintModel, result: (UiState<String>) -> Unit)
    suspend fun getComplaintHistory(id: String, result: (UiState<List<ComplaintHistoryModel>>) -> Unit)
}