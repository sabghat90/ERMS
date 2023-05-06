package com.kust.erms_company.ui.employee

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kust.erms_company.R
import com.kust.erms_company.data.model.EmployeeModel
import com.kust.erms_company.databinding.ItemEmployeeBinding

class EmployeeListingAdapter (
    val onItemClicked: (Int, EmployeeModel) -> Unit
        ) : RecyclerView.Adapter<EmployeeListingAdapter.ViewHolder>() {

    var employees: MutableList<EmployeeModel> = arrayListOf()

//    private lateinit var listener: OnItemClickListener
//
//    interface OnItemClickListener {
//        fun onItemClick(position: Int)
//    }

//    fun setOnItemClickListener(listener: OnItemClickListener) {
//        this.listener = listener
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            ItemEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemView, /*listener*/ )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employee = employees[position]
        holder.bind(employee, position)
    }

    fun updateList(newList: MutableList<EmployeeModel>) {
        employees = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return employees.size
    }

    inner class ViewHolder(private val binding: ItemEmployeeBinding, /* listener : OnItemClickListener*/ ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(employee: EmployeeModel, position: Int) {

            Glide.with(binding.root.context)
                .load(employee.profilePicture)
                .placeholder(R.drawable.avatar4)
                .centerCrop()
                .into(binding.imgEmployee)

            binding.tvEmployeeName.text = employee.name
            binding.tvDepartment.text = employee.department

            if (employee.role == "manager") {
                binding.tvStatus.text = "Manager"
            } else {
                binding.tvStatus.text = "Employee"
            }

            binding.cardEmployee.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(position, employees[position])
                }
            }
        }

//        init {
//            binding.cardEmployee.setOnClickListener {
//                val position = adapterPosition
//                if (position != RecyclerView.NO_POSITION) {
//                    listener.onItemClick(position)
//                }
//            }
//        }
    }
}