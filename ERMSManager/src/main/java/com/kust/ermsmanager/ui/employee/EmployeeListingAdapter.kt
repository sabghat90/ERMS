package com.kust.ermsmanager.ui.employee

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.databinding.ItemEmployeeBinding
import com.kust.ermsmanager.utils.Role

class EmployeeListingAdapter (
    val onItemClicked: (Int, EmployeeModel) -> Unit
        ) :
    ListAdapter<EmployeeModel, EmployeeListingAdapter.EmployeeViewHolder>(DiffUtilCallback()) {

    var employeeList: MutableList<EmployeeModel> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding =
            ItemEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = employeeList[position]
        holder.bind(employee, position)
    }

    inner class EmployeeViewHolder(private val binding: ItemEmployeeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(employee: EmployeeModel, position: Int) {

            binding.tvEmployeeName.text = employee.name
            binding.tvDepartment.text = employee.department

            if (employee.role == Role.MANAGER) {
                binding.tvStatus.text = "Manager"
            } else {
                binding.tvStatus.text = "Employee"
            }

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
