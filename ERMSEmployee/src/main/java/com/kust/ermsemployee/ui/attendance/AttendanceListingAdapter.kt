package com.kust.ermsemployee.ui.attendance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kust.ermsemployee.databinding.AttendanceItemBinding
import com.kust.ermslibrary.models.Attendance

class AttendanceListingAdapter :
    ListAdapter<Attendance, AttendanceListingAdapter.AttendanceViewHolder>(DiffUtilCallback()) {

    var attendanceList: MutableList<Attendance> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AttendanceListingAdapter.AttendanceViewHolder {
        val binding =
            AttendanceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttendanceViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AttendanceListingAdapter.AttendanceViewHolder,
        position: Int
    ) {
        val attendance = attendanceList[position]
        holder.bind(attendance)
    }

    inner class AttendanceViewHolder(private val binding: AttendanceItemBinding):
    RecyclerView.ViewHolder(binding.root){
        fun bind(attendance: Attendance){
            with(binding) {
                tvEmployeeName.text = attendance.employeeName
                tvDate.text = attendance.date
                tvStatus.text = attendance.status
                tvTime.text = attendance.time
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Attendance>() {
        override fun areItemsTheSame(oldItem: Attendance, newItem: Attendance): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Attendance,
            newItem: Attendance
        ): Boolean {
            return oldItem == newItem
        }
    }
}