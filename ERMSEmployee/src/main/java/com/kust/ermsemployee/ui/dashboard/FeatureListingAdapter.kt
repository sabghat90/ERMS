package com.kust.ermsemployee.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kust.ermsemployee.data.model.FeatureModel
import com.kust.ermsemployee.databinding.FeatureItemBinding

class FeatureListingAdapter : RecyclerView.Adapter<FeatureListingAdapter.FeatureListingViewHolder>() {

    var features : MutableList<FeatureModel> = arrayListOf()

    private lateinit var listener : OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureListingViewHolder {
        val view = FeatureItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeatureListingViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: FeatureListingViewHolder, position: Int) {
        val feature = features[position]
        holder.bind(feature)
    }

    override fun getItemCount(): Int {
        return features.size
    }

    inner class FeatureListingViewHolder(private val binding: FeatureItemBinding, listener : OnItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        fun bind(feature: FeatureModel) {
            binding.featureImage.setImageResource(feature.image)
            binding.featureName.text = feature.title
        }

        init {
            binding.featureCard.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position)
                }
            }
        }
    }
}