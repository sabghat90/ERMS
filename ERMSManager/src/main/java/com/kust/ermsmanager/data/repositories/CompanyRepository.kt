package com.kust.ermsmanager.data.repositories

import com.kust.ermslibrary.models.Company
import com.kust.ermslibrary.utils.UiState

interface CompanyRepository {
    suspend fun getCompanyProfile(result: (UiState<List<Company>>) -> Unit)
}