package com.kust.ermsmanager.ui.company

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kust.ermslibrary.models.Company
import com.kust.ermslibrary.utils.UiState
import com.kust.ermsmanager.data.repositories.CompanyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyViewModel @Inject constructor(
    private val repository: CompanyRepository
) : ViewModel() {

    private val _getCompanyProfile = MutableLiveData<UiState<List<Company>>>()
    val getCompanyProfile: LiveData<UiState<List<Company>>>
        get() = _getCompanyProfile

    suspend fun getCompanyProfile() {
        _getCompanyProfile.value = UiState.Loading
        viewModelScope.launch {
            repository.getCompanyProfile {
                _getCompanyProfile.value = it
            }
        }
    }
}