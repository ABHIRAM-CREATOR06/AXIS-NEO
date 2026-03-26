package com.axisneo.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.axisneo.app.R
import com.axisneo.app.data.HistoryEntry

/**
 * Adapter for displaying scan history
 */
class HistoryAdapter(
    private val onHistoryClick: (HistoryEntry) -> Unit
) : ListAdapter<HistoryEntry, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val historyScore: TextView = itemView.findViewById(R.id.historyScore)
        private val historyUrl: TextView = itemView.findViewById(R.id.historyUrl)
        private val historyMeta: TextView = itemView.findViewById(R.id.historyMeta)

        fun bind(entry: HistoryEntry) {
            val context = itemView.context

            historyScore.text = entry.score.toString()
            historyUrl.text = entry.url

            // Set score color based on value
            val scoreColor = when {
                entry.score >= 90 -> R.color.md_success
                entry.score >= 70 -> R.color.md_warning
                else -> R.color.md_error
            }
            historyScore.setTextColor(ContextCompat.getColor(context, scoreColor))

            // Format timestamp
            val now = System.currentTimeMillis()
            val diff = now - entry.timestamp
            val oneDay = 24 * 60 * 60 * 1000

            val timeStr = when {
                diff < oneDay -> "Today"
                diff < 2 * oneDay -> "Yesterday"
                else -> "${(diff / oneDay)} days ago"
            }

            historyMeta.text = context.getString(
                R.string.history_summary,
                entry.errors,
                entry.warnings,
                timeStr
            )

            itemView.setOnClickListener { onHistoryClick(entry) }
        }
    }

    class HistoryDiffCallback : DiffUtil.ItemCallback<HistoryEntry>() {
        override fun areItemsTheSame(oldItem: HistoryEntry, newItem: HistoryEntry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HistoryEntry, newItem: HistoryEntry): Boolean {
            return oldItem == newItem
        }
    }
}