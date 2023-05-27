package com.kust.ermsemployee.ui.complaints

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kust.ermsemployee.data.model.ComplaintModel
import com.kust.ermsemployee.data.repository.ComplaintRepository
import com.kust.ermsemployee.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComplaintViewModel @Inject constructor(
    private val complaintRepository: ComplaintRepository
) : ViewModel() {

    private val _createComplaint = MutableLiveData<UiState<String>>()
    val createComplaint : MutableLiveData<UiState<String>>
        get() = _createComplaint

    private val _getComplaints = MutableLiveData<UiState<List<ComplaintModel>>>()
    val getComplaints : MutableLiveData<UiState<List<ComplaintModel>>>
        get() = _getComplaints

    private val _updateComplaint = MutableLiveData<UiState<Pair<ComplaintModel,String>>>()
    val updateComplaint : MutableLiveData<UiState<Pair<ComplaintModel,String>>>
        get() = _updateComplaint

    private val _deleteComplaint = MutableLiveData<UiState<String>>()
    val deleteComplaint : MutableLiveData<UiState<String>>
        get() = _deleteComplaint


    suspend fun createComplaint(complaintModel: ComplaintModel) {
        _createComplaint.value = UiState.Loading
        viewModelScope.launch {
            complaintRepository.createComplaint(complaintModel) {
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

    suspend fun updateComplaint(complaintModel: ComplaintModel) {
        _updateComplaint.value = UiState.Loading
        viewModelScope.launch {
            complaintRepository.updateComplaint(complaintModel) {
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
}