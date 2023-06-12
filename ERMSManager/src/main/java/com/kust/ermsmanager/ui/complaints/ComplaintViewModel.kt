package com.kust.ermsmanager.ui.complaints

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kust.ermslibrary.models.Complaint
import com.kust.ermslibrary.models.ComplaintHistory
import com.kust.ermslibrary.utils.UiState
import com.kust.ermsmanager.data.repositories.ComplaintRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComplaintViewModel @Inject constructor(
    private val complaintRepository: ComplaintRepository
): ViewModel() {

    private val _getComplaints = MutableLiveData<UiState<List<Complaint>>>()
    val getComplaints: LiveData<UiState<List<Complaint>>>
        get() = _getComplaints

    private val _updateComplaint = MutableLiveData<UiState<String>>()
    val updateComplaint: LiveData<UiState<String>>
        get() = _updateComplaint

    private val _getComplaintHistory = MutableLiveData<UiState<List<ComplaintHistory>>>()
    val getComplaintHistory: LiveData<UiState<List<ComplaintHistory>>>
        get() = _getComplaintHistory

    suspend fun getComplaints() {
        _getComplaints.value = UiState.Loading
        viewModelScope.launch {
            complaintRepository.getComplaints {
                _getComplaints.value = it
            }
        }
    }

    suspend fun updateComplaint(complaint: Complaint, historyModel: ComplaintHistory) {
        _updateComplaint.value = UiState.Loading
        viewModelScope.launch {
            complaintRepository.updateComplaint(complaint, historyModel) {
                _updateComplaint.value = it
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