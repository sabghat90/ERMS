package com.kust.ermsmanager.ui.attendance

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kust.ermsmanager.data.models.AttendanceModel
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.databinding.EmployeeItemForAttendanceBinding

class AttendanceListingAdapter(
    val onItemClicked: (Int, EmployeeModel) -> Unit,
) :
    ListAdapter<EmployeeModel, AttendanceListingAdapter.EmployeeViewHolder>(DiffUtilCallback()) {

    var employeeList: MutableList<EmployeeModel> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding =
            EmployeeItemForAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = employeeList[position]
        holder.bind(employee)
    }

    inner class EmployeeViewHolder(private val binding: EmployeeItemForAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(employee: EmployeeModel) {

            binding.tvEmployeeName.text = employee.name
            binding.tvDepartment.text = employee.department
            binding.tvStatus.text = employee.role

            binding.cardEmployee.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(position, employeeList[position])
                }
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<EmployeeModel>() {
        override fun areItemsTheSame(oldItem: EmployeeModel, newItem: EmployeeModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EmployeeModel, newItem: EmployeeModel): Boolean {
            return oldItem == newItem
        }
    }
}
