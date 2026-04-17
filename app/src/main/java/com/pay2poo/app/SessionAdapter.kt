package com.pay2poo.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pay2poo.app.data.PoopSession
import com.pay2poo.app.databinding.ItemSessionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SessionAdapter(
    private val onDelete: (PoopSession) -> Unit
) : ListAdapter<PoopSession, SessionAdapter.ViewHolder>(DIFF) {

    private val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())

    inner class ViewHolder(private val binding: ItemSessionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(session: PoopSession) {
            binding.tvSessionDate.text = dateFormat.format(Date(session.startTime))
            binding.tvSessionDuration.text = session.formattedDuration
            binding.tvSessionEarnings.text = binding.root.context.getString(R.string.earnings_fmt, session.earnings)
            binding.root.setOnLongClickListener {
                onDelete(session)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<PoopSession>() {
            override fun areItemsTheSame(a: PoopSession, b: PoopSession) = a.id == b.id
            override fun areContentsTheSame(a: PoopSession, b: PoopSession) = a == b
        }
    }
}
