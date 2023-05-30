package com.kust.ermsemployee.ui.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kust.ermsemployee.data.repository.AttendanceRepository
import com.kust.ermslibrary.models.Attendance
import com.kust.ermslibrary.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
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

    init {
        getAttendanceList(Attendance())
    }

    init {
        getAttendanceForToday()
    }

    private fun getAttendanceList(attendance: Attendance) {
        _getAttendanceList.value = UiState.Loading
        attendanceRepository.getAttendance(attendance) {
            _getAttendanceList.value = it
        }
    }

    private fun getAttendanceForToday() {
        _getAttendanceForToday.value = UiState.Loading
        attendanceRepository.getAttendanceForToday {
            _getAttendanceForToday.value = it
        }
    }
}