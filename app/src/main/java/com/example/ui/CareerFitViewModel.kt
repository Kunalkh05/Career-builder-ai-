package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.ResumeEngine
import com.example.data.model.*
import com.example.data.repository.ResumeRepository
import com.example.data.sample.SampleData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class Screen {
    LANDING,
    DASHBOARD,
    RESUME_UPLOAD,
    RESUME_REVIEW,
    TARGET_JOB_SETUP,
    JOB_ANALYSIS,
    MATCH_ANALYSIS,
    SUGGESTIONS_WORKSPACE,
    COMPARISON_EDITOR,
    QUALITY_CHECK,
    VERSION_MANAGEMENT,
    EXPORT_PREVIEW
}

data class UiState(
    val currentScreen: Screen = Screen.LANDING,
    val originalResume: Resume = SampleData.sampleResumeSoftware,
    val modifiedResume: Resume = SampleData.sampleResumeSoftware,
    val targetJob: JobTarget = SampleData.sampleJobGoogle,
    val jobAnalysis: JobAnalysis? = null,
    val matchAnalysis: MatchAnalysis? = null,
    val suggestions: List<ResumeSuggestion> = emptyList(),
    val qualityReport: ResumeQualityReport? = null,
    val savedVersions: List<ResumeVersion> = emptyList(),
    val isAnalyzing: Boolean = false,
    val statusMessage: String = "",
    val activeSectionFilter: ResumeSection? = null,
    val activeStatusFilter: ReviewStatus? = null,
    val toastMessage: String? = null,
    val isDarkTheme: Boolean = false
)

class CareerFitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ResumeRepository(application)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allVersions.collect { versions ->
                _uiState.update { it.copy(savedVersions = versions) }
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun toggleTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun loadSampleResume() {
        val sample = SampleData.sampleResumeSoftware
        _uiState.update {
            it.copy(
                originalResume = sample,
                modifiedResume = sample,
                toastMessage = "Loaded sample resume: ${sample.fullName}"
            )
        }
    }

    fun loadSpecificSampleResume(resume: Resume) {
        _uiState.update {
            it.copy(
                originalResume = resume,
                modifiedResume = resume,
                toastMessage = "Loaded profile: ${resume.fullName}"
            )
        }
    }

    fun loadSampleJob(job: JobTarget) {
        _uiState.update {
            it.copy(
                targetJob = job,
                toastMessage = "Loaded target role: ${job.jobTitle} at ${job.companyName}"
            )
        }
    }

    fun onResumeTextUploaded(rawText: String, candidateName: String = "") {
        val parsed = ResumeEngine.parseRawResumeText(rawText, candidateName)
        _uiState.update {
            it.copy(
                originalResume = parsed,
                modifiedResume = parsed,
                currentScreen = Screen.RESUME_REVIEW,
                toastMessage = "Resume parsed successfully (${parsed.experiences.size} experiences, ${parsed.skills.technical.size} skills)"
            )
        }
        viewModelScope.launch {
            repository.saveResume(parsed)
        }
    }

    fun updateTargetJob(job: JobTarget) {
        _uiState.update { it.copy(targetJob = job) }
    }

    /**
     * Complete AI Optimization Pipeline:
     * 1. Analyze Job Description
     * 2. Compare Resume to Job (Match Analysis)
     * 3. Generate structured AI suggestions adhering strictly to factual accuracy
     */
    fun runFullOptimization() {
        val currentResume = _uiState.value.originalResume
        val targetJob = _uiState.value.targetJob

        if (targetJob.jobDescription.isBlank()) {
            _uiState.update { it.copy(toastMessage = "Please provide a job description first") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAnalyzing = true,
                    statusMessage = "Analyzing job description requirements..."
                )
            }

            val jobAnalysis = ResumeEngine.analyzeJobDescription(targetJob)

            _uiState.update {
                it.copy(
                    jobAnalysis = jobAnalysis,
                    statusMessage = "Evaluating resume-to-job match evidence..."
                )
            }

            val matchAnalysis = ResumeEngine.compareResumeToJob(currentResume, jobAnalysis)

            _uiState.update {
                it.copy(
                    matchAnalysis = matchAnalysis,
                    statusMessage = "Generating tailored, evidence-backed improvements..."
                )
            }

            val suggestions = ResumeEngine.generateSuggestions(currentResume, targetJob, jobAnalysis, matchAnalysis)

            val initialModified = ResumeEngine.applySuggestionsToResume(currentResume, suggestions)

            val qualityReport = ResumeEngine.performQualityCheck(currentResume, initialModified, targetJob, suggestions)

            _uiState.update {
                it.copy(
                    isAnalyzing = false,
                    jobAnalysis = jobAnalysis,
                    matchAnalysis = matchAnalysis,
                    suggestions = suggestions,
                    modifiedResume = initialModified,
                    qualityReport = qualityReport,
                    currentScreen = Screen.JOB_ANALYSIS,
                    toastMessage = "Analysis complete! ${suggestions.size} tailored suggestions ready."
                )
            }
        }
    }

    fun acceptSuggestion(suggestionId: String) {
        _uiState.update { state ->
            val updated = state.suggestions.map {
                if (it.id == suggestionId) it.copy(reviewStatus = ReviewStatus.ACCEPTED) else it
            }
            val newModified = ResumeEngine.applySuggestionsToResume(state.originalResume, updated)
            val newQuality = ResumeEngine.performQualityCheck(state.originalResume, newModified, state.targetJob, updated)
            state.copy(suggestions = updated, modifiedResume = newModified, qualityReport = newQuality)
        }
    }

    fun rejectSuggestion(suggestionId: String) {
        _uiState.update { state ->
            val updated = state.suggestions.map {
                if (it.id == suggestionId) it.copy(reviewStatus = ReviewStatus.REJECTED) else it
            }
            val newModified = ResumeEngine.applySuggestionsToResume(state.originalResume, updated)
            val newQuality = ResumeEngine.performQualityCheck(state.originalResume, newModified, state.targetJob, updated)
            state.copy(suggestions = updated, modifiedResume = newModified, qualityReport = newQuality)
        }
    }

    fun editSuggestion(suggestionId: String, newText: String) {
        _uiState.update { state ->
            val updated = state.suggestions.map {
                if (it.id == suggestionId) it.copy(reviewStatus = ReviewStatus.EDITED, userEditedText = newText) else it
            }
            val newModified = ResumeEngine.applySuggestionsToResume(state.originalResume, updated)
            val newQuality = ResumeEngine.performQualityCheck(state.originalResume, newModified, state.targetJob, updated)
            state.copy(suggestions = updated, modifiedResume = newModified, qualityReport = newQuality)
        }
    }

    fun acceptAllSupported() {
        _uiState.update { state ->
            val updated = state.suggestions.map {
                if (it.evidenceStatus == EvidenceStatus.SUPPORTED_BY_RESUME || it.evidenceStatus == EvidenceStatus.SUPPORTED_BY_USER_INFO) {
                    it.copy(reviewStatus = ReviewStatus.ACCEPTED)
                } else it
            }
            val newModified = ResumeEngine.applySuggestionsToResume(state.originalResume, updated)
            val newQuality = ResumeEngine.performQualityCheck(state.originalResume, newModified, state.targetJob, updated)
            state.copy(
                suggestions = updated,
                modifiedResume = newModified,
                qualityReport = newQuality,
                toastMessage = "Accepted all evidence-supported suggestions"
            )
        }
    }

    fun rejectAll() {
        _uiState.update { state ->
            val updated = state.suggestions.map { it.copy(reviewStatus = ReviewStatus.REJECTED) }
            val newModified = state.originalResume
            val newQuality = ResumeEngine.performQualityCheck(state.originalResume, newModified, state.targetJob, updated)
            state.copy(
                suggestions = updated,
                modifiedResume = newModified,
                qualityReport = newQuality,
                toastMessage = "Reverted all suggestions to original resume"
            )
        }
    }

    fun updateManualResume(newResume: Resume) {
        _uiState.update { state ->
            val newQuality = ResumeEngine.performQualityCheck(state.originalResume, newResume, state.targetJob, state.suggestions)
            state.copy(modifiedResume = newResume, qualityReport = newQuality)
        }
    }

    fun setSectionFilter(section: ResumeSection?) {
        _uiState.update { it.copy(activeSectionFilter = section) }
    }

    fun setStatusFilter(status: ReviewStatus?) {
        _uiState.update { it.copy(activeStatusFilter = status) }
    }

    fun saveVersion(versionName: String, notes: String = "") {
        val state = _uiState.value
        val version = ResumeVersion(
            id = "ver_${UUID.randomUUID().toString().take(8)}",
            versionName = versionName.ifEmpty { "${state.targetJob.jobTitle} - ${state.targetJob.companyName}".ifEmpty { "Tailored Resume" } },
            targetCompany = state.targetJob.companyName,
            targetRole = state.targetJob.jobTitle,
            originalResumeId = state.originalResume.id,
            modifiedResume = state.modifiedResume,
            acceptedCount = state.suggestions.count { it.reviewStatus == ReviewStatus.ACCEPTED || it.reviewStatus == ReviewStatus.EDITED },
            rejectedCount = state.suggestions.count { it.reviewStatus == ReviewStatus.REJECTED },
            notes = notes
        )
        viewModelScope.launch {
            repository.saveVersion(version)
            _uiState.update {
                it.copy(
                    toastMessage = "Resume version '${version.versionName}' saved successfully!"
                )
            }
        }
    }

    fun deleteVersion(id: String) {
        viewModelScope.launch {
            repository.deleteVersion(id)
            _uiState.update { it.copy(toastMessage = "Version deleted") }
        }
    }

    fun restoreVersion(version: ResumeVersion) {
        _uiState.update {
            it.copy(
                modifiedResume = version.modifiedResume,
                targetJob = it.targetJob.copy(
                    companyName = version.targetCompany,
                    jobTitle = version.targetRole
                ),
                currentScreen = Screen.COMPARISON_EDITOR,
                toastMessage = "Loaded version '${version.versionName}'"
            )
        }
    }
}
