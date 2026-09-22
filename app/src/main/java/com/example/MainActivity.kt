package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.CareerFitViewModel
import com.example.ui.Screen
import com.example.ui.components.AppHeader
import com.example.ui.components.WorkflowStepper
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CareerFitViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()
            val snackbarHostState = remember { SnackbarHostState() }

            // Handle Toast messages
            LaunchedEffect(uiState.toastMessage) {
                uiState.toastMessage?.let { msg ->
                    snackbarHostState.showSnackbar(msg)
                    viewModel.clearToast()
                }
            }

            // Custom back navigation handler
            BackHandler(enabled = uiState.currentScreen != Screen.LANDING) {
                when (uiState.currentScreen) {
                    Screen.DASHBOARD -> viewModel.navigateTo(Screen.LANDING)
                    Screen.RESUME_UPLOAD -> viewModel.navigateTo(Screen.DASHBOARD)
                    Screen.RESUME_REVIEW -> viewModel.navigateTo(Screen.RESUME_UPLOAD)
                    Screen.TARGET_JOB_SETUP -> viewModel.navigateTo(Screen.RESUME_UPLOAD)
                    Screen.JOB_ANALYSIS -> viewModel.navigateTo(Screen.TARGET_JOB_SETUP)
                    Screen.MATCH_ANALYSIS -> viewModel.navigateTo(Screen.JOB_ANALYSIS)
                    Screen.SUGGESTIONS_WORKSPACE -> viewModel.navigateTo(Screen.MATCH_ANALYSIS)
                    Screen.COMPARISON_EDITOR -> viewModel.navigateTo(Screen.SUGGESTIONS_WORKSPACE)
                    Screen.QUALITY_CHECK -> viewModel.navigateTo(Screen.COMPARISON_EDITOR)
                    Screen.EXPORT_PREVIEW -> viewModel.navigateTo(Screen.QUALITY_CHECK)
                    Screen.VERSION_MANAGEMENT -> viewModel.navigateTo(Screen.DASHBOARD)
                    Screen.LANDING -> Unit
                }
            }

            MyApplicationTheme(darkTheme = uiState.isDarkTheme) {
                val isWorkflowScreen = uiState.currentScreen in listOf(
                    Screen.RESUME_UPLOAD,
                    Screen.TARGET_JOB_SETUP,
                    Screen.JOB_ANALYSIS,
                    Screen.MATCH_ANALYSIS,
                    Screen.SUGGESTIONS_WORKSPACE,
                    Screen.COMPARISON_EDITOR,
                    Screen.QUALITY_CHECK,
                    Screen.EXPORT_PREVIEW
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        AppHeader(
                            currentScreen = uiState.currentScreen,
                            onNavigate = { viewModel.navigateTo(it) },
                            onBack = if (uiState.currentScreen != Screen.LANDING) {
                                {
                                    when (uiState.currentScreen) {
                                        Screen.DASHBOARD -> viewModel.navigateTo(Screen.LANDING)
                                        Screen.RESUME_UPLOAD -> viewModel.navigateTo(Screen.DASHBOARD)
                                        Screen.RESUME_REVIEW -> viewModel.navigateTo(Screen.RESUME_UPLOAD)
                                        Screen.TARGET_JOB_SETUP -> viewModel.navigateTo(Screen.RESUME_UPLOAD)
                                        Screen.JOB_ANALYSIS -> viewModel.navigateTo(Screen.TARGET_JOB_SETUP)
                                        Screen.MATCH_ANALYSIS -> viewModel.navigateTo(Screen.JOB_ANALYSIS)
                                        Screen.SUGGESTIONS_WORKSPACE -> viewModel.navigateTo(Screen.MATCH_ANALYSIS)
                                        Screen.COMPARISON_EDITOR -> viewModel.navigateTo(Screen.SUGGESTIONS_WORKSPACE)
                                        Screen.QUALITY_CHECK -> viewModel.navigateTo(Screen.COMPARISON_EDITOR)
                                        Screen.EXPORT_PREVIEW -> viewModel.navigateTo(Screen.QUALITY_CHECK)
                                        Screen.VERSION_MANAGEMENT -> viewModel.navigateTo(Screen.DASHBOARD)
                                        Screen.LANDING -> Unit
                                    }
                                }
                            } else null,
                            isDarkTheme = uiState.isDarkTheme,
                            onToggleTheme = { viewModel.toggleTheme() }
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (isWorkflowScreen) {
                            WorkflowStepper(
                                currentScreen = uiState.currentScreen,
                                onStepClick = { viewModel.navigateTo(it) }
                            )
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            when (uiState.currentScreen) {
                                Screen.LANDING -> {
                                    LandingScreen(
                                        onStartOptimizing = { viewModel.navigateTo(Screen.RESUME_UPLOAD) },
                                        onExploreDashboard = { viewModel.navigateTo(Screen.DASHBOARD) },
                                        onLoadSampleDemo = {
                                            viewModel.loadSampleResume()
                                            viewModel.runFullOptimization()
                                        },
                                        onLoadProfile = { profile ->
                                            viewModel.loadSpecificSampleResume(profile)
                                            viewModel.runFullOptimization()
                                        },
                                        isDarkTheme = uiState.isDarkTheme
                                    )
                                }
                                Screen.DASHBOARD -> {
                                    DashboardScreen(
                                        uiState = uiState,
                                        onStartNewOptimization = { viewModel.navigateTo(Screen.RESUME_UPLOAD) },
                                        onUploadResume = { viewModel.navigateTo(Screen.RESUME_UPLOAD) },
                                        onOpenVersion = { version -> viewModel.restoreVersion(version) },
                                        onDeleteVersion = { id -> viewModel.deleteVersion(id) },
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onQuickStartDemo = {
                                            viewModel.loadSampleResume()
                                            viewModel.runFullOptimization()
                                        }
                                    )
                                }
                                Screen.RESUME_UPLOAD, Screen.RESUME_REVIEW -> {
                                    ResumeUploadScreen(
                                        currentResume = uiState.originalResume,
                                        onUploadText = { text, name ->
                                            viewModel.onResumeTextUploaded(text, name)
                                        },
                                        onLoadSample = {
                                            viewModel.loadSampleResume()
                                        },
                                        onProceedToJobSetup = {
                                            viewModel.navigateTo(Screen.TARGET_JOB_SETUP)
                                        }
                                    )
                                }
                                Screen.TARGET_JOB_SETUP -> {
                                    TargetJobSetupScreen(
                                        jobTarget = uiState.targetJob,
                                        isAnalyzing = uiState.isAnalyzing,
                                        statusMessage = uiState.statusMessage,
                                        onUpdateJob = { viewModel.updateTargetJob(it) },
                                        onRunOptimization = { viewModel.runFullOptimization() }
                                    )
                                }
                                Screen.JOB_ANALYSIS -> {
                                    JobAnalysisScreen(
                                        jobTarget = uiState.targetJob,
                                        jobAnalysis = uiState.jobAnalysis,
                                        onProceedToMatch = { viewModel.navigateTo(Screen.MATCH_ANALYSIS) }
                                    )
                                }
                                Screen.MATCH_ANALYSIS -> {
                                    MatchAnalysisScreen(
                                        matchAnalysis = uiState.matchAnalysis,
                                        onOpenSuggestions = { viewModel.navigateTo(Screen.SUGGESTIONS_WORKSPACE) }
                                    )
                                }
                                Screen.SUGGESTIONS_WORKSPACE -> {
                                    SuggestionsWorkspaceScreen(
                                        suggestions = uiState.suggestions,
                                        onAcceptSuggestion = { viewModel.acceptSuggestion(it) },
                                        onRejectSuggestion = { viewModel.rejectSuggestion(it) },
                                        onEditSuggestion = { id, text -> viewModel.editSuggestion(id, text) },
                                        onAcceptAllSupported = { viewModel.acceptAllSupported() },
                                        onRejectAll = { viewModel.rejectAll() },
                                        onOpenComparisonEditor = { viewModel.navigateTo(Screen.COMPARISON_EDITOR) }
                                    )
                                }
                                Screen.COMPARISON_EDITOR -> {
                                    ComparisonEditorScreen(
                                        originalResume = uiState.originalResume,
                                        modifiedResume = uiState.modifiedResume,
                                        onUpdateModifiedResume = { viewModel.updateManualResume(it) },
                                        onRestoreOriginal = { viewModel.rejectAll() },
                                        onProceedToQualityCheck = { viewModel.navigateTo(Screen.QUALITY_CHECK) }
                                    )
                                }
                                Screen.QUALITY_CHECK -> {
                                    QualityCheckScreen(
                                        qualityReport = uiState.qualityReport,
                                        onSaveVersionAndProceed = { name ->
                                            viewModel.saveVersion(name)
                                            viewModel.navigateTo(Screen.EXPORT_PREVIEW)
                                        },
                                        onBackToEditor = { viewModel.navigateTo(Screen.COMPARISON_EDITOR) }
                                    )
                                }
                                Screen.VERSION_MANAGEMENT -> {
                                    VersionManagementScreen(
                                        versions = uiState.savedVersions,
                                        onOpenVersion = { version -> viewModel.restoreVersion(version) },
                                        onDeleteVersion = { id -> viewModel.deleteVersion(id) },
                                        onStartNewOptimization = { viewModel.navigateTo(Screen.RESUME_UPLOAD) }
                                    )
                                }
                                Screen.EXPORT_PREVIEW -> {
                                    ExportScreen(
                                        resume = uiState.modifiedResume,
                                        targetJob = uiState.targetJob,
                                        suggestions = uiState.suggestions,
                                        onBackToEditor = { viewModel.navigateTo(Screen.COMPARISON_EDITOR) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
