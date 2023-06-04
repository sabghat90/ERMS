package com.kust.ermsemployee.ui.employee

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kust.ermsemployee.databinding.ItemEmployeeBinding
import com.kust.ermslibrary.models.Employee

class EmployeeListingAdapter(
    val onItemClicked: (Int, Employee) -> Unit
) :
    ListAdapter<Employee, EmployeeListingAdapter.EmployeeViewHolder>(DiffUtilCallback()) {

    var employeeList: MutableList<Employee> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding =
            ItemEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = employeeList[position]
        holder.bind(employee)
    }

    inner class EmployeeViewHolder(private val binding: ItemEmployeeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(employee: Employee) {

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

    class DiffUtilCallback : DiffUtil.ItemCallback<Employee>() {
        override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem == newItem
        }
    }
}
