package com.kust.ermsemployee.ui.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kust.ermsemployee.data.model.AttendanceModel
import com.kust.ermsemployee.data.repository.AttendanceRepository
import com.kust.ermsemployee.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository
): ViewModel() {

    private val _getAttendanceList = MutableLiveData<UiState<List<AttendanceModel>>>()
    val getAttendanceList: LiveData<UiState<List<AttendanceModel>>>
        get() = _getAttendanceList

    private val _getAttendanceForToday = MutableLiveData<UiState<List<AttendanceModel>>>()
    val getAttendanceForToday: LiveData<UiState<List<AttendanceModel>>>
        get() = _getAttendanceForToday

    init {
        getAttendanceList(AttendanceModel())
    }

    init {
        getAttendanceForToday()
    }

    private fun getAttendanceList(attendanceModel: AttendanceModel) {
        _getAttendanceList.value = UiState.Loading
        attendanceRepository.getAttendance(attendanceModel) {
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