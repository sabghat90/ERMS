package com.kust.erms_company.data.repositroy

import com.kust.ermslibrary.models.Company
import com.kust.ermslibrary.utils.UiState

interface CompanyRepository {
    fun getCompanyDetails(company: Company, result: (UiState<List<Company>>) -> Unit)
    fun updateCompanyDetails(
        companyId: String,
        company: Company,
        result: (UiState<Pair<Company, String>>) -> Unit
    )
}