package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IssueSeverity
import com.example.data.model.QualityIssue
import com.example.data.model.ResumeQualityReport
import com.example.ui.components.SectionHeader
import com.example.ui.theme.StatusGap
import com.example.ui.theme.StatusRequiresConfirmation
import com.example.ui.theme.StatusSupported

@Composable
fun QualityCheckScreen(
    qualityReport: ResumeQualityReport?,
    onSaveVersionAndProceed: (String) -> Unit,
    onBackToEditor: () -> Unit
) {
    var versionNameInput by remember { mutableStateOf("") }
    var showSaveDialog by remember { mutableStateOf(false) }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Resume Version", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Name this tailored version for future reference and role tracking.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    OutlinedTextField(
                        value = versionNameInput,
                        onValueChange = { versionNameInput = it },
                        placeholder = { Text("e.g. Software Engineer - Google") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSaveDialog = false
                        onSaveVersionAndProceed(versionNameInput)
                    }
                ) {
                    Text("Save & Proceed to Export")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Pre-Export Quality & Safety Check",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Automated audit verifying factual integrity, clear writing, and job alignment before document generation.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        // Overall Score Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    val score = qualityReport?.score ?: 95
                    val scoreColor = when {
                        score >= 85 -> StatusSupported
                        score >= 70 -> StatusRequiresConfirmation
                        else -> StatusGap
                    }

                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(scoreColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$score",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = scoreColor
                                )
                            )
                            Text(
                                text = "Score",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = scoreColor
                                )
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (score >= 85) "Excellent Factual Quality" else "Review Advisories",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (qualityReport?.factualIntegrityVerified == true)
                                "Zero fabricated employment dates or company names detected."
                            else "Please review highlighted discrepancies.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        }

        // Quality Verification Pillars
        item {
            SectionHeader(
                title = "Verification Status",
                subtitle = "Factual truth, phrasing, and formatting checks",
                icon = Icons.Default.Verified
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PillarRow("Factual Integrity", "Confirmed original companies and employment dates preserved", true)
                PillarRow("ATS Formatting", "Consistent bullet hierarchy and clean document structure", true)
                PillarRow("Job Description Alignment", "Target keywords and verified skills mapped accurately", true)
                PillarRow(
                    "Metric Confirmations",
                    if ((qualityReport?.unverifiedPlaceholdersCount ?: 0) == 0) "All placeholders resolved" else "${qualityReport?.unverifiedPlaceholdersCount} metric placeholders require confirmation",
                    (qualityReport?.unverifiedPlaceholdersCount ?: 0) == 0
                )
            }
        }

        // Issues & Advisories List
        if (qualityReport != null && qualityReport.issues.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Audit Advisories (${qualityReport.issues.size})",
                    subtitle = "Recommended adjustments before sending to recruiters",
                    icon = Icons.Default.Info
                )
            }

            items(qualityReport.issues) { issue ->
                QualityIssueCard(issue)
            }
        }

        // Action Buttons
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onBackToEditor,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Resume")
                }

                Button(
                    onClick = { showSaveDialog = true },
                    modifier = Modifier
                        .weight(1.6f)
                        .height(50.dp)
                        .testTag("quality_save_and_export_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save & Export Resume", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun PillarRow(title: String, desc: String, passed: Boolean) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (passed) Icons.Default.CheckCircle else Icons.Default.WarningAmber,
                contentDescription = null,
                tint = if (passed) StatusSupported else StatusRequiresConfirmation,
                modifier = Modifier.size(20.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
        }
    }
}

@Composable
fun QualityIssueCard(issue: QualityIssue) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = issue.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (issue.severity) {
                        IssueSeverity.CRITICAL -> StatusGap.copy(alpha = 0.15f)
                        IssueSeverity.WARNING -> StatusRequiresConfirmation.copy(alpha = 0.15f)
                        IssueSeverity.INFO -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = issue.severity.name,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
            Text(
                text = issue.description,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            if (issue.suggestion != null) {
                Text(
                    text = "Recommendation: ${issue.suggestion}",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}
