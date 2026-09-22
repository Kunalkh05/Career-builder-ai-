package com.example.data.model

enum class EvidenceStatus(val label: String) {
    SUPPORTED_BY_RESUME("Supported by resume"),
    SUPPORTED_BY_USER_INFO("Supported by user-provided info"),
    REQUIRES_CONFIRMATION("Requires user confirmation"),
    NOT_SUFFICIENTLY_SUPPORTED("Not sufficiently supported")
}

enum class ReviewStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    EDITED
}

data class ResumeSuggestion(
    val id: String,
    val section: ResumeSection,
    val targetRef: String, // e.g. "summary", "exp_0_bullet_1", "project_1_bullet_0"
    val originalText: String,
    val suggestedText: String,
    val userEditedText: String? = null,
    val reasonForChange: String,
    val relatedJobRequirement: String,
    val evidenceStatus: EvidenceStatus = EvidenceStatus.SUPPORTED_BY_RESUME,
    val reviewStatus: ReviewStatus = ReviewStatus.PENDING,
    val placeholderNote: String? = null // if requires confirmation of metric/detail
) {
    val activeText: String
        get() = when (reviewStatus) {
            ReviewStatus.ACCEPTED -> suggestedText
            ReviewStatus.EDITED -> userEditedText ?: suggestedText
            ReviewStatus.REJECTED -> originalText
            ReviewStatus.PENDING -> suggestedText
        }
}
