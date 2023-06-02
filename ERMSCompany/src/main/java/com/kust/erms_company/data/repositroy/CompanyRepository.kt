package com.kust.erms_company.data.repositroy

import android.net.Uri
import com.kust.ermslibrary.models.Company
import com.kust.ermslibrary.utils.UiState

interface CompanyRepository {
    fun getCompanyDetails(company: Company, result: (UiState<List<Company>>) -> Unit)
    suspend fun updateCompanyDetails(
        company: Company,
        result: (UiState<Pair<Company, String>>) -> Unit
    )

    suspend fun uploadProfilePicture(imageUri: Uri, result: (UiState<Uri>) -> Unit)
}