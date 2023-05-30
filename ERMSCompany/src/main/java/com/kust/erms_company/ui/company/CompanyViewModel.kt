package com.kust.erms_company.ui.company

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kust.erms_company.data.repositroy.CompanyRepository
import com.kust.ermslibrary.models.Company
import com.kust.ermslibrary.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
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

    init {
        getCompanyDetails(Company())
    }


    private fun getCompanyDetails(company: Company) {
        _getCompanyDetails.value = UiState.Loading
        repository.getCompanyDetails(company) {
            _getCompanyDetails.value = it
        }
    }

    fun updateCompanyDetails(companyId: String, company: Company) {
        _updateCompanyDetails.value = UiState.Loading
        repository.updateCompanyDetails(companyId, company) {
            _updateCompanyDetails.value = it
        }
    }
}