package com.axisneo.app.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.axisneo.app.R
import com.axisneo.app.adapter.HistoryAdapter
import com.axisneo.app.data.HistoryEntry

/**
 * Fragment for the history page
 */
class HistoryFragment : Fragment() {

    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyRecyclerView = view.findViewById(R.id.historyRecyclerView)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter { entry ->
            // Handle history item click - could navigate to detail view
        }

        historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }

        // Load sample history data
        loadSampleHistory()
    }

    private fun loadSampleHistory() {
        val historyList = listOf(
            HistoryEntry(
                id = 1,
                url = "https://material.io",
                score = 92,
                errors = 3,
                warnings = 2,
                timestamp = System.currentTimeMillis() - 2 * 60 * 60 * 1000 // 2 hours ago
            ),
            HistoryEntry(
                id = 2,
                url = "https://example.com/shop",
                score = 67,
                errors = 11,
                warnings = 8,
                timestamp = System.currentTimeMillis() - 24 * 60 * 60 * 1000 // 1 day ago
            ),
            HistoryEntry(
                id = 3,
                url = "https://legacy-portal.gov.in",
                score = 34,
                errors = 29,
                warnings = 14,
                timestamp = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000 // 2 days ago
            )
        )

        historyAdapter.submitList(historyList)
    }
}