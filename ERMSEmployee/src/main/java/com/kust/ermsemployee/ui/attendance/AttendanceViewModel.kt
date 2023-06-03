package com.kust.ermsemployee.ui.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kust.ermsemployee.data.repository.AttendanceRepository
import com.kust.ermslibrary.models.Attendance
import com.kust.ermslibrary.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository
): ViewModel() {

    private val _getAttendanceList = MutableLiveData<UiState<List<Attendance>>>()
    val getAttendanceList: LiveData<UiState<List<Attendance>>>
        get() = _getAttendanceList

    private val _getAttendanceForToday = MutableLiveData<UiState<List<Attendance>>>()
    val getAttendanceForToday: LiveData<UiState<List<Attendance>>>
        get() = _getAttendanceForToday

    fun getAttendanceList() {
        _getAttendanceList.value = UiState.Loading
        viewModelScope.launch{
            attendanceRepository.getAttendance {
                _getAttendanceList.value = it
            }
        }
    }

    fun getAttendanceForToday() {
        _getAttendanceForToday.value = UiState.Loading
        viewModelScope.launch {
            attendanceRepository.getAttendanceForToday {
                _getAttendanceForToday.value = it
            }
        }
    }
}