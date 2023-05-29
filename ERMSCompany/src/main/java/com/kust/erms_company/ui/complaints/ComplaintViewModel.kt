package com.kust.erms_company.ui.complaints

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kust.erms_company.data.model.ComplaintHistoryModel
import com.kust.erms_company.data.model.ComplaintModel
import com.kust.erms_company.data.repositroy.ComplaintRepository
import com.kust.erms_company.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComplaintViewModel @Inject constructor(
    private val complaintRepository: ComplaintRepository
) : ViewModel() {

    private val _getComplaints = MutableLiveData<UiState<List<ComplaintModel>>>()
    val getComplaints : LiveData<UiState<List<ComplaintModel>>>
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

    suspend fun updateComplaint(complaintModel: ComplaintModel, updateComplaintHistory: ComplaintHistoryModel) {
        _updateComplaint.value = UiState.Loading
        viewModelScope.launch {
            complaintRepository.updateComplaint(complaintModel, updateComplaintHistory) {
                _updateComplaint.value = it
            }
        }
    }

    suspend fun deleteComplaint(complaintModel: ComplaintModel) {
        _deleteComplaint.value = UiState.Loading
        viewModelScope.launch {
            complaintRepository.deleteComplaint(complaintModel) {
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