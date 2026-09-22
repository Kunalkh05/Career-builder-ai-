package com.example.data.model

data class Resume(
    val id: String = "default_resume",
    val title: String = "My Professional Resume",
    val rawText: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val location: String = "",
    val linkedinUrl: String = "",
    val githubUrl: String = "",
    val summary: String = "",
    val experiences: List<WorkExperience> = emptyList(),
    val education: List<EducationItem> = emptyList(),
    val projects: List<ProjectItem> = emptyList(),
    val skills: ResumeSkills = ResumeSkills(),
    val certifications: List<String> = emptyList(),
    val achievements: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class WorkExperience(
    val id: String,
    val company: String,
    val role: String,
    val location: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val isCurrent: Boolean = false,
    val bullets: List<String> = emptyList()
)

data class EducationItem(
    val id: String,
    val institution: String,
    val degree: String,
    val fieldOfStudy: String = "",
    val graduationDate: String = "",
    val gpa: String = ""
)

data class ProjectItem(
    val id: String,
    val name: String,
    val role: String = "",
    val description: String = "",
    val bullets: List<String> = emptyList(),
    val technologies: List<String> = emptyList(),
    val projectUrl: String = ""
)

data class ResumeSkills(
    val technical: List<String> = emptyList(),
    val softSkills: List<String> = emptyList(),
    val toolsAndFrameworks: List<String> = emptyList()
)

enum class ResumeSection(val displayName: String) {
    SUMMARY("Professional Summary"),
    EXPERIENCE("Work Experience"),
    PROJECTS("Projects"),
    SKILLS("Skills & Competencies"),
    EDUCATION("Education"),
    CERTIFICATIONS("Certifications"),
    ACHIEVEMENTS("Achievements")
}
