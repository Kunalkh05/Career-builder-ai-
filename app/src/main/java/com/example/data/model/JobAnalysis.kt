package com.example.data.model

data class JobAnalysis(
    val requiredSkills: List<RequirementItem> = emptyList(),
    val preferredSkills: List<RequirementItem> = emptyList(),
    val responsibilities: List<RequirementItem> = emptyList(),
    val experienceRequirements: List<RequirementItem> = emptyList(),
    val educationRequirements: List<RequirementItem> = emptyList(),
    val toolsAndTechnologies: List<RequirementItem> = emptyList(),
    val keywordsAndConcepts: List<RequirementItem> = emptyList(),
    val behavioralAndSoftSkills: List<RequirementItem> = emptyList(),
    val analyzedAt: Long = System.currentTimeMillis()
)

data class RequirementItem(
    val id: String,
    val text: String,
    val classification: RequirementClassification = RequirementClassification.EXPLICITLY_REQUIRED,
    val contextNote: String = ""
)

enum class RequirementClassification(val label: String) {
    EXPLICITLY_REQUIRED("Explicitly Required"),
    PREFERRED("Preferred / Plus"),
    OPTIONAL("Optional"),
    UNCLEAR("Contextual / Unclear")
}
