package com.example.data.model

data class MatchAnalysis(
    val matchScore: Int = 0,
    val potentialMatches: List<MatchEvidence> = emptyList(),
    val possibleGaps: List<GapItem> = emptyList(),
    val improvementOpportunities: List<ImprovementOpportunity> = emptyList(),
    val unsupportedClaims: List<String> = emptyList()
)

data class MatchEvidence(
    val id: String,
    val jobRequirement: String,
    val resumeEvidence: String,
    val confidence: String = "High",
    val matchedSection: ResumeSection = ResumeSection.EXPERIENCE
)

data class GapItem(
    val id: String,
    val requirement: String,
    val carefulNotice: String = "This requirement was not identified in the uploaded resume. Please confirm if you have experience in this area.",
    val category: String = "Skill / Tool"
)

data class ImprovementOpportunity(
    val id: String,
    val title: String,
    val description: String,
    val targetSection: ResumeSection
)
