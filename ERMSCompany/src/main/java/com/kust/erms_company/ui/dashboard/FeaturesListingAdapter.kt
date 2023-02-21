package com.kust.erms_company.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kust.erms_company.data.model.FeatureModel
import com.kust.erms_company.databinding.FeatureItemBinding

class FeaturesListingAdapter : RecyclerView.Adapter<FeaturesListingAdapter.ViewHolder>() {

    var features: MutableList<FeatureModel> = arrayListOf()

    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            FeatureItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feature = features[position]
        holder.bind(feature, position)
    }

    override fun getItemCount(): Int {
        return features.size
    }

    inner class ViewHolder(private val binding: FeatureItemBinding, listener : OnItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(feature: FeatureModel, position: Int) {
            binding.featureImage.setImageResource(feature.image)
            binding.btnFeature.text = feature.title
        }

        init {
            binding.btnFeature.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position)
                }
            }
        }
    }
}