package com.kust.ermsemployee.data.repository

import com.kust.ermsemployee.data.model.ComplaintHistoryModel
import com.kust.ermsemployee.data.model.ComplaintModel
import com.kust.ermsemployee.utils.UiState

interface ComplaintRepository {
    suspend fun createComplaint(complaintModel: ComplaintModel, complaintHistoryModel: ComplaintHistoryModel, result: (UiState<String>) -> Unit)
    suspend fun getComplaints(result: (UiState<List<ComplaintModel>>) -> Unit)
    suspend fun updateComplaint(complaintModel: ComplaintModel, result: (UiState<Pair<ComplaintModel,String>>) -> Unit)
    suspend fun deleteComplaint(complaintModel: ComplaintModel, result: (UiState<String>) -> Unit)
    suspend fun getComplaintHistory(id: String, result: (UiState<List<ComplaintHistoryModel>>) -> Unit)
}