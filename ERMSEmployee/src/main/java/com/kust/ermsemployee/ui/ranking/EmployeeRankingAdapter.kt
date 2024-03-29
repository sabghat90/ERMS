package com.kust.ermsemployee.ui.ranking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kust.ermsemployee.databinding.EmployeeRankItemBinding
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.R as LibraryR

class EmployeeRankingAdapter(
    val onItemClicked: (Int, Employee) -> Unit
) :
    ListAdapter<Employee, EmployeeRankingAdapter.EmployeeViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding =
            EmployeeRankItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.bind(getItem(position))
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
        fun bind(employee: Employee) {

            binding.tvEmployeeName.text = employee.name
            binding.tvRole.text = employee.role
            binding.tvPoints.text = employee.points.toString()

            // set ranking img for first 3 employees
            when (adapterPosition) {
                0 -> {
                    binding.imgRank.visibility = ViewGroup.VISIBLE
                    binding.imgRank.setImageResource(LibraryR.drawable.ic_first)
                }
                1 -> {
                    binding.imgRank.visibility = ViewGroup.VISIBLE
                    binding.imgRank.setImageResource(LibraryR.drawable.ic_second)
                }
                2 -> {
                    binding.imgRank.visibility = ViewGroup.VISIBLE
                    binding.imgRank.setImageResource(LibraryR.drawable.ic_third)
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
