package com.kust.ermsmanager.ui.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kust.ermsmanager.data.models.AttendanceModel
import com.kust.ermsmanager.data.repositories.AttendanceRepository
import com.kust.ermsmanager.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private val _markAttendance = MutableLiveData<UiState<String>>()
    val markAttendance: LiveData<UiState<String>>
        get() = _markAttendance

    private val _getAttendanceForOneEmployee = MutableLiveData<UiState<List<AttendanceModel>>>()
    val getAttendanceForOneEmployee: LiveData<UiState<List<AttendanceModel>>>
        get() = _getAttendanceForOneEmployee

    private val _getAttendance = MutableLiveData<UiState<List<AttendanceModel>>>()
    val getAttendance: LiveData<UiState<List<AttendanceModel>>>
        get() = _getAttendance

    fun markAttendance(attendanceModel: AttendanceModel) {
        _markAttendance.value = UiState.Loading
        attendanceRepository.markAttendance(attendanceModel) {
            _markAttendance.value = it
        }
    }

    fun getAttendanceForOneEmployee(id: String) {
        _getAttendanceForOneEmployee.value = UiState.Loading
        attendanceRepository.getAttendanceForOneEmployee(id) {
            _getAttendanceForOneEmployee.value = it
        }
    }

    fun getAttendance(attendanceModel: AttendanceModel) {
        _getAttendance.value = UiState.Loading
        attendanceRepository.getAttendance() {
            _getAttendance.value = it
        }
    }
}