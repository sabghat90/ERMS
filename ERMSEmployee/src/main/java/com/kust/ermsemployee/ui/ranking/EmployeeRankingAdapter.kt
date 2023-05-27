package com.kust.ermsemployee.ui.ranking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kust.ermsemployee.R
import com.kust.ermsemployee.data.model.EmployeeModel
import com.kust.ermsemployee.databinding.EmployeeRankItemBinding

class EmployeeRankingAdapter(
    val onItemClicked: (Int, EmployeeModel) -> Unit
) :
    ListAdapter<EmployeeModel, EmployeeRankingAdapter.EmployeeViewHolder>(DiffUtilCallback()) {

    // list of employees where i can index to first 3 employees
    var employeeList = mutableListOf<EmployeeModel>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding =
            EmployeeRankItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = employeeList[position]
        holder.bind(employee)
    }

    // notifies the adapter that the data set has changed
    fun updateList(employeeList: MutableList<EmployeeModel>) {
        this.employeeList = employeeList
        notifyDataSetChanged()
    }

    inner class EmployeeViewHolder(private val binding: EmployeeRankItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(employee: EmployeeModel) {

            binding.tvEmployeeName.text = employee.name
            binding.tvRole.text = employee.role
            binding.tvPoints.text = employee.points.toString()

            // set ranking img for first 3 employees
            when (adapterPosition) {
                0 -> {
                    binding.imgRank.visibility = ViewGroup.VISIBLE
                    binding.imgRank.setImageResource(R.drawable.ic_first)
                }
                1 -> {
                    binding.imgRank.visibility = ViewGroup.VISIBLE
                    binding.imgRank.setImageResource(R.drawable.ic_second)
                }
                2 -> {
                    binding.imgRank.visibility = ViewGroup.VISIBLE
                    binding.imgRank.setImageResource(R.drawable.ic_third)
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
