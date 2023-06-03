package com.kust.ermsmanager.ui.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kust.ermslibrary.models.Attendance
import com.kust.ermslibrary.utils.UiState
import com.kust.ermsmanager.data.repositories.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private val _markAttendance = MutableLiveData<UiState<String>>()
    val markAttendance: LiveData<UiState<String>>
        get() = _markAttendance

    private val _getAttendanceForOneEmployee = MutableLiveData<UiState<List<Attendance>>>()
    val getAttendanceForOneEmployee: LiveData<UiState<List<Attendance>>>
        get() = _getAttendanceForOneEmployee

    private val _getAttendance = MutableLiveData<UiState<List<Attendance>>>()
    val getAttendance: LiveData<UiState<List<Attendance>>>
        get() = _getAttendance

    fun markAttendance(attendance: Attendance) {
        _markAttendance.value = UiState.Loading
        viewModelScope.launch {
            attendanceRepository.markAttendance(attendance) {
                _markAttendance.value = it
            }
        }
    }

    fun getAttendanceForOneEmployee(id: String) {
        _getAttendanceForOneEmployee.value = UiState.Loading
        viewModelScope.launch {
            attendanceRepository.getAttendanceForOneEmployee(id) {
                _getAttendanceForOneEmployee.value = it
            }
        }
    }

    fun getAttendance(attendance: Attendance) {
        _getAttendance.value = UiState.Loading
        viewModelScope.launch {
            attendanceRepository.getAttendance() {
                _getAttendance.value = it
            }
        }
    }
}