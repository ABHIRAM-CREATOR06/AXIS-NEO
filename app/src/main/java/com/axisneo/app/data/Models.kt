package com.axisneo.app.data

/**
 * Represents an accessibility issue found during a scan
 */
data class Issue(
    val id: Int,
    val type: IssueType,
    val category: IssueCategory,
    val rule: String,
    val title: String,
    val description: String,
    val code: String,
    val fix: String,
    val wcag: List<String>
)

enum class IssueType {
    ERROR, WARNING, INFO
}

enum class IssueCategory {
    ALL, IMAGES, CONTRAST, FORMS, KEYBOARD, STRUCTURE
}

/**
 * Represents a completed scan result
 */
data class ScanResult(
    val url: String,
    val score: Int,
    val grade: String,
    val errors: Int,
    val warnings: Int,
    val info: Int,
    val passed: Int,
    val issues: List<Issue>
)

/**
 * Represents a history entry
 */
data class HistoryEntry(
    val id: Long = System.currentTimeMillis(),
    val url: String,
    val score: Int,
    val errors: Int,
    val warnings: Int,
    val timestamp: Long = System.currentTimeMillis()
)