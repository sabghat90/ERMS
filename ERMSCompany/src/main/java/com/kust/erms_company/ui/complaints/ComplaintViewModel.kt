package com.kust.erms_company.ui.complaints

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kust.erms_company.data.repositroy.ComplaintRepository
import com.kust.ermslibrary.models.Complaint
import com.kust.ermslibrary.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComplaintViewModel @Inject constructor(
    private val complaintRepository: ComplaintRepository
) : ViewModel() {

    private val _getComplaints = MutableLiveData<UiState<List<Complaint>>>()
    val getComplaints : LiveData<UiState<List<Complaint>>>
        get() = _getComplaints

    private val _updateComplaint = MutableLiveData<UiState<String>>()
    val updateComplaint : LiveData<UiState<String>>
        get() = _updateComplaint

    private val _deleteComplaint = MutableLiveData<UiState<String>>()
    val deleteComplaint : LiveData<UiState<String>>
        get() = _deleteComplaint

    private val _getComplaintHistory = MutableLiveData<UiState<List<ComplaintHistoryModel>>>()
    val getComplaintHistory: LiveData<UiState<List<ComplaintHistoryModel>>>
        get() = _getComplaintHistory

    suspend fun getComplaints() {
        _getComplaints.value = UiState.Loading
        viewModelScope.launch {
            complaintRepository.getComplaints {
                _getComplaints.value = it
            }
        }
    }

    suspend fun updateComplaint(complaint: Complaint, updateComplaintHistory: ComplaintHistoryModel) {
        _updateComplaint.value = UiState.Loading
        viewModelScope.launch {
            complaintRepository.updateComplaint(complaint, updateComplaintHistory) {
                _updateComplaint.value = it
            }
        }
    }

    suspend fun deleteComplaint(complaint: Complaint) {
        _deleteComplaint.value = UiState.Loading
        viewModelScope.launch {
            complaintRepository.deleteComplaint(complaint) {
                _deleteComplaint.value = it
            }
        }
    }

    suspend fun getComplaintHistory(id: String) {
        _getComplaintHistory.value = UiState.Loading
        viewModelScope.launch {
            complaintRepository.getComplaintHistory(id) {
                _getComplaintHistory.value = it
            }
        }
    }
}