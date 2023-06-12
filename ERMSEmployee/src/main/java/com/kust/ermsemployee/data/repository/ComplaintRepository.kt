package com.kust.ermsemployee.data.repository

import com.kust.ermslibrary.models.Complaint
import com.kust.ermslibrary.models.ComplaintHistory
import com.kust.ermslibrary.utils.UiState

interface ComplaintRepository {
    suspend fun createComplaint(complaint: Complaint, complaintHistory: ComplaintHistory, result: (UiState<String>) -> Unit)
    suspend fun getComplaints(result: (UiState<List<Complaint>>) -> Unit)
    suspend fun updateComplaint(complaint: Complaint, history: ComplaintHistory, result: (UiState<String>) -> Unit)
    suspend fun deleteComplaint(complaint: Complaint, result: (UiState<String>) -> Unit)
    suspend fun getComplaintHistory(id: String, result: (UiState<List<ComplaintHistory>>) -> Unit)
}