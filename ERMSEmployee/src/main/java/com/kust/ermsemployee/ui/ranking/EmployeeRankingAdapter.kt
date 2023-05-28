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

    /**
    Binds the employee data to the views in the item layout.
    Sets the employee name, role, and points to the respective TextViews.
    Determines the ranking image to be displayed based on the adapter position:

        If the adapter position is 0, sets the first place ranking image.

        If the adapter position is 1, sets the second place ranking image.

        If the adapter position is 2, sets the third place ranking image.

    Sets the visibility and image resource of the ranking image view accordingly.
        **/

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
