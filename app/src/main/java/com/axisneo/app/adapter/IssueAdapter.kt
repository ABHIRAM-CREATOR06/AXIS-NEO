package com.axisneo.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.axisneo.app.R
import com.axisneo.app.data.Issue
import com.axisneo.app.data.IssueType
import com.google.android.material.chip.Chip

/**
 * Adapter for displaying accessibility issues
 */
class IssueAdapter(
    private val onIssueClick: (Issue) -> Unit
) : ListAdapter<Issue, IssueAdapter.IssueViewHolder>(IssueDiffCallback()) {

    private val expandedItems = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_issue, parent, false)
        return IssueViewHolder(view)
    }

    override fun onBindViewHolder(holder: IssueViewHolder, position: Int) {
        val issue = getItem(position)
        holder.bind(issue, expandedItems.contains(issue.id))
    }

    inner class IssueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val issueIconContainer: View = itemView.findViewById(R.id.issueIconContainer)
        private val issueIcon: ImageView = itemView.findViewById(R.id.issueIcon)
        private val issueRule: TextView = itemView.findViewById(R.id.issueRule)
        private val issueTitle: TextView = itemView.findViewById(R.id.issueTitle)
        private val issueDesc: TextView = itemView.findViewById(R.id.issueDesc)
        private val issueChevron: ImageView = itemView.findViewById(R.id.issueChevron)
        private val issueDetail: LinearLayout = itemView.findViewById(R.id.issueDetail)
        private val issueCode: TextView = itemView.findViewById(R.id.issueCode)
        private val issueFix: TextView = itemView.findViewById(R.id.issueFix)
        private val wcagChips: LinearLayout = itemView.findViewById(R.id.wcagChips)

        fun bind(issue: Issue, isExpanded: Boolean) {
            val context = itemView.context

            // Set icon based on issue type
            val (bgColor, iconRes) = when (issue.type) {
                IssueType.ERROR -> Pair(R.color.md_error_container, R.drawable.ic_error)
                IssueType.WARNING -> Pair(R.color.md_warning_container, R.drawable.ic_warning)
                IssueType.INFO -> Pair(R.color.md_secondary_container, R.drawable.ic_info)
            }
            issueIconContainer.setBackgroundColor(ContextCompat.getColor(context, bgColor))
            issueIcon.setImageResource(iconRes)
            issueIcon.setColorFilter(ContextCompat.getColor(
                context,
                when (issue.type) {
                    IssueType.ERROR -> R.color.md_error
                    IssueType.WARNING -> R.color.md_warning
                    IssueType.INFO -> R.color.md_secondary
                }
            ))

            issueRule.text = issue.rule
            issueTitle.text = issue.title
            issueDesc.text = issue.description
            issueCode.text = issue.code
            issueFix.text = issue.fix

            // Show/hide detail
            issueDetail.visibility = if (isExpanded) View.VISIBLE else View.GONE
            issueChevron.rotation = if (isExpanded) 180f else 0f

            // Setup WCAG chips
            wcagChips.removeAllViews()
            issue.wcag.forEach { wcagCode ->
                val chip = Chip(context).apply {
                    text = wcagCode
                    setChipBackgroundColorResource(R.color.md_primary_container)
                    setTextColor(ContextCompat.getColor(context, R.color.md_on_primary_container))
                    textSize = 11f
                    chipMinHeight = 28f
                    isClickable = false
                }
                wcagChips.addView(chip)
            }

            // Click listener
            itemView.setOnClickListener {
                if (expandedItems.contains(issue.id)) {
                    expandedItems.remove(issue.id)
                } else {
                    expandedItems.add(issue.id)
                }
                notifyItemChanged(adapterPosition)
                onIssueClick(issue)
            }
        }
    }

    class IssueDiffCallback : DiffUtil.ItemCallback<Issue>() {
        override fun areItemsTheSame(oldItem: Issue, newItem: Issue): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Issue, newItem: Issue): Boolean {
            return oldItem == newItem
        }
    }
}