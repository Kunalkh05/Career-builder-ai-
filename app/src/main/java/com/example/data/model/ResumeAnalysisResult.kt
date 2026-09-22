package com.example.data.model

/**
 * Structured result representing the comprehensive output of the AI Resume Modification Engine.
 */
data class ResumeAnalysisResult(
    val matchScore: Int,
    val summary: String = "",
    val matches: List<MatchEvidence> = emptyList(),
    val gaps: List<GapItem> = emptyList(),
    val suggestions: List<ResumeSuggestion> = emptyList(),
    val unsupportedClaims: List<String> = emptyList(),
    val factualIntegrityVerified: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
