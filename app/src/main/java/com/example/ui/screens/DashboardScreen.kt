package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ResumeVersion
import com.example.ui.Screen
import com.example.ui.UiState
import com.example.ui.components.SectionHeader
import com.example.ui.theme.StatusSupported

@Composable
fun DashboardScreen(
    uiState: UiState,
    onStartNewOptimization: () -> Unit,
    onUploadResume: () -> Unit,
    onOpenVersion: (ResumeVersion) -> Unit,
    onDeleteVersion: (String) -> Unit,
    onNavigate: (Screen) -> Unit,
    onQuickStartDemo: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        // Workspace Title & Primary Action
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Resume Intelligence Workspace",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Manage your tailored resumes, active optimizations, and company target versions.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Button(
                    onClick = onStartNewOptimization,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("dashboard_new_optimization_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddCircleOutline, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create New Resume Optimization",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Overview Metrics Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Active Resume",
                    value = if (uiState.originalResume.fullName.isNotEmpty()) uiState.originalResume.fullName else "1 Profile",
                    icon = Icons.Default.PersonOutline,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Saved Versions",
                    value = "${uiState.savedVersions.size}",
                    icon = Icons.Default.FolderOpen,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Target Match",
                    value = if (uiState.matchAnalysis != null) "${uiState.matchAnalysis.matchScore}%" else "Ready",
                    icon = Icons.Default.GpsFixed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Start / Sample Demo Banner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Quick Start Demonstration",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Pre-load Alex Chen (Android Engineer) and target role at Google.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Button(
                        onClick = onQuickStartDemo,
                        modifier = Modifier.testTag("dashboard_quick_start_demo_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("1-Tap Run")
                    }
                }
            }
        }

        // Active Session Status (if analyzed)
        if (uiState.matchAnalysis != null || uiState.suggestions.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Current Optimization Session",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StatusSupported.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Active",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = StatusSupported,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Text(
                            text = "Target: ${uiState.targetJob.jobTitle} @ ${uiState.targetJob.companyName.ifEmpty { "Target Company" }}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Match Score: ${uiState.matchAnalysis?.matchScore ?: 0}%",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Suggestions: ${uiState.suggestions.size}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Accepted: ${uiState.suggestions.count { it.reviewStatus.name == "ACCEPTED" }}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { onNavigate(Screen.SUGGESTIONS_WORKSPACE) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("dashboard_resume_session_button")
                            ) {
                                Text("Open AI Modifier")
                            }
                            OutlinedButton(
                                onClick = { onNavigate(Screen.COMPARISON_EDITOR) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("dashboard_compare_session_button")
                            ) {
                                Text("Compare & Edit")
                            }
                        }
                    }
                }
            }
        }

        // Saved Resume Versions Header
        item {
            SectionHeader(
                title = "Saved Role Versions",
                subtitle = "Customized variations tailored for specific job listings",
                icon = Icons.Default.FolderSpecial
            )
        }

        // Saved Versions List or Empty State
        if (uiState.savedVersions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NoteAdd,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "No Tailored Versions Saved Yet",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Start a resume optimization session to tailor your resume for a company, approve suggestions, and save as a version.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(uiState.savedVersions) { version ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = version.versionName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${version.targetRole} • ${version.targetCompany.ifEmpty { "General" }}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                            IconButton(
                                onClick = { onDeleteVersion(version.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete version",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Accepted changes: ${version.acceptedCount}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )

                            Button(
                                onClick = { onOpenVersion(version) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Open & Export", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}
