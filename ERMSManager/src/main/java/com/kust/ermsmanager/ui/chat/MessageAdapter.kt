package com.kust.ermsmanager.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.kust.ermslibrary.models.Message
import com.kust.ermslibrary.utils.ConvertDateAndTimeFormat
import com.kust.ermsmanager.databinding.MessageItemReceiverBinding
import com.kust.ermsmanager.databinding.MessageItemSenderBinding
import java.text.SimpleDateFormat

class MessageAdapter : ListAdapter<Message, RecyclerView.ViewHolder>(DiffUtilCallback()) {

    companion object {
        const val VIEW_TYPE_SENDER = 1
        const val VIEW_TYPE_RECEIVER = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENDER) {
            val binding =
                MessageItemSenderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SenderViewHolder(binding)
        } else {
            val binding =
                MessageItemReceiverBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ReceiverViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.javaClass == SenderViewHolder::class.java) {
            (holder as SenderViewHolder).bind(getItem(position))
        } else {
            (holder as ReceiverViewHolder).bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {

        val message = getItem(position)
        return if (FirebaseAuth.getInstance().currentUser?.uid.equals(message.senderId)) {
            VIEW_TYPE_SENDER
        } else {
            VIEW_TYPE_RECEIVER
        }
    }

    class SenderViewHolder(private val binding: MessageItemSenderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            val time = ConvertDateAndTimeFormat().formatTime(message.time.toString())
            binding.tvMessage.text = message.body
            binding.tvTime.text = time
        }
    }

    class ReceiverViewHolder(private val binding: MessageItemReceiverBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            val time = ConvertDateAndTimeFormat().formatTime(message.time.toString())
            binding.tvMessage.text = message.body
            binding.tvTime.text = time
        }
    }

    class DiffUtilCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

    }
}