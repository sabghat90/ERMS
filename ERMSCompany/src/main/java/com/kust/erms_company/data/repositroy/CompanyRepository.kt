package com.kust.erms_company.data.repositroy

import com.kust.erms_company.data.model.CompanyModel
import com.kust.erms_company.utils.UiState

interface CompanyRepository {
    fun getCompanyDetails(companyModel: CompanyModel, result: (UiState<List<CompanyModel>>) -> Unit)
    fun updateCompanyDetails(
        companyId: String,
        companyModel: CompanyModel,
        result: (UiState<Pair<CompanyModel, String>>) -> Unit
    )
}