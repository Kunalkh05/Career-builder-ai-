package com.example.data.model

enum class IssueSeverity {
    INFO,
    WARNING,
    CRITICAL
}

enum class QualityCategory(val title: String) {
    FACTUAL_CONSISTENCY("Factual Consistency & Truth"),
    CONTENT_QUALITY("Content & Writing Quality"),
    JOB_RELEVANCE("Job Alignment & Evidence"),
    FORMATTING("Formatting & Structure")
}

data class QualityIssue(
    val id: String,
    val category: QualityCategory,
    val severity: IssueSeverity,
    val title: String,
    val description: String,
    val affectedSection: String? = null,
    val suggestion: String? = null
)

data class ResumeQualityReport(
    val score: Int = 100, // 0-100
    val issues: List<QualityIssue> = emptyList(),
    val factualIntegrityVerified: Boolean = true,
    val unverifiedPlaceholdersCount: Int = 0
)
