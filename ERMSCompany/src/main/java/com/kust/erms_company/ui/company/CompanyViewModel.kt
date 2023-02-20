package com.kust.erms_company.ui.company

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kust.erms_company.data.model.CompanyModel
import com.kust.erms_company.data.model.EmployeeModel
import com.kust.erms_company.data.repositroy.CompanyRepository
import com.kust.erms_company.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CompanyViewModel @Inject constructor(
    private val repository: CompanyRepository
) : ViewModel() {

    private val _getCompanyDetails = MutableLiveData<UiState<CompanyModel>>()
    val getCompanyDetails: MutableLiveData<UiState<CompanyModel>>
        get() = _getCompanyDetails

    private val _updateCompanyDetails = MutableLiveData<UiState<Pair<CompanyModel, String>>>()
    val updateCompanyDetails: MutableLiveData<UiState<Pair<CompanyModel, String>>>
        get() = _updateCompanyDetails

    init {

    }

    fun getCompanyDetails(companyId: String) {
        _getCompanyDetails.value = UiState.Loading
        repository.getCompanyDetails(companyId) {
            _getCompanyDetails.value = it
        }
    }

    fun updateCompanyDetails(companyId: String, companyModel: CompanyModel) {
        _updateCompanyDetails.value = UiState.Loading
        repository.updateCompanyDetails(companyId, companyModel) {
            _updateCompanyDetails.value = it
        }
    }
}