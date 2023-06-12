package com.kust.ermsemployee.ui.complaints

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kust.ermsemployee.data.repository.ComplaintRepository
import com.kust.ermslibrary.models.Complaint
import com.kust.ermslibrary.models.ComplaintHistory
import com.kust.ermslibrary.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComplaintViewModel @Inject constructor(
    private val complaintRepository: ComplaintRepository
) : ViewModel() {

    private val _createComplaint = MutableLiveData<UiState<String>>()
    val createComplaint : LiveData<UiState<String>>
        get() = _createComplaint

    private val _getComplaints = MutableLiveData<UiState<List<Complaint>>>()
    val getComplaints : LiveData<UiState<List<Complaint>>>
        get() = _getComplaints

    private val _updateComplaint = MutableLiveData<UiState<String>>()
    val updateComplaint : LiveData<UiState<String>>
        get() = _updateComplaint

    private val _deleteComplaint = MutableLiveData<UiState<String>>()
    val deleteComplaint : LiveData<UiState<String>>
        get() = _deleteComplaint

    private val _getComplaintHistory = MutableLiveData<UiState<List<ComplaintHistory>>>()
    val getComplaintHistory: LiveData<UiState<List<ComplaintHistory>>>
        get() = _getComplaintHistory

    suspend fun createComplaint(complaint: Complaint, complaintHistory: ComplaintHistory) {
        _createComplaint.value = UiState.Loading
        viewModelScope.launch {
            complaintRepository.createComplaint(complaint, complaintHistory) {
                _createComplaint.value = it
            }
        }
    }

    suspend fun getComplaints() {
        _getComplaints.value = UiState.Loading
        viewModelScope.launch {
            complaintRepository.getComplaints {
                _getComplaints.value = it
            }
        }
    }

    suspend fun updateComplaint(complaint: Complaint, history: ComplaintHistory) {
        _updateComplaint.value = UiState.Loading
        viewModelScope.launch {
            complaintRepository.updateComplaint(complaint, history) {
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