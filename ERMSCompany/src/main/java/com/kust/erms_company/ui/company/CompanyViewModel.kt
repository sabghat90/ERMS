package com.kust.erms_company.ui.company

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kust.erms_company.data.repositroy.CompanyRepository
import com.kust.ermslibrary.models.Company
import com.kust.ermslibrary.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyViewModel @Inject constructor(
    private val repository: CompanyRepository
) : ViewModel() {

    private val _getCompanyDetails = MutableLiveData<UiState<List<Company>>>()
    val getCompanyDetails: LiveData<UiState<List<Company>>>
        get() = _getCompanyDetails

    private val _updateCompanyDetails = MutableLiveData<UiState<Pair<Company, String>>>()
    val updateCompanyDetails: LiveData<UiState<Pair<Company, String>>>
        get() = _updateCompanyDetails

    private val _uploadImage = MutableLiveData<UiState<Uri>>()
    val uploadImage: LiveData<UiState<Uri>>
        get() = _uploadImage


    private fun getCompanyDetails(company: Company) {
        _getCompanyDetails.value = UiState.Loading
        repository.getCompanyDetails(company) {
            _getCompanyDetails.value = it
        }
    }

    fun updateCompanyDetails(company: Company) {
        _updateCompanyDetails.value = UiState.Loading
        viewModelScope.launch {
            repository.updateCompanyDetails(company) {
                _updateCompanyDetails.value = it
            }
        }
    }

    fun uploadImage(fileUris: Uri, result: (UiState<Uri>) -> Unit) {
        _uploadImage.value = UiState.Loading
        viewModelScope.launch {
            repository.uploadProfilePicture(fileUris) {
                _uploadImage.value = it
                result(it)
            }
        }
    }
}