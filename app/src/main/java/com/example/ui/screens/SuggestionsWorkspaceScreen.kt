package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.EvidenceStatus
import com.example.data.model.ResumeSection
import com.example.data.model.ResumeSuggestion
import com.example.data.model.ReviewStatus
import com.example.ui.components.EvidenceBadge
import com.example.ui.components.ReviewStatusBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun SuggestionsWorkspaceScreen(
    suggestions: List<ResumeSuggestion>,
    onAcceptSuggestion: (String) -> Unit,
    onRejectSuggestion: (String) -> Unit,
    onEditSuggestion: (String, String) -> Unit,
    onAcceptAllSupported: () -> Unit,
    onRejectAll: () -> Unit,
    onOpenComparisonEditor: () -> Unit
) {
    var selectedSectionFilter by remember { mutableStateOf<ResumeSection?>(null) }
    var selectedStatusFilter by remember { mutableStateOf<ReviewStatus?>(null) }
    var editingSuggestion by remember { mutableStateOf<ResumeSuggestion?>(null) }

    val filteredSuggestions = suggestions.filter { sug ->
        (selectedSectionFilter == null || sug.section == selectedSectionFilter) &&
        (selectedStatusFilter == null || sug.reviewStatus == selectedStatusFilter)
    }

    val acceptedCount = suggestions.count { it.reviewStatus == ReviewStatus.ACCEPTED || it.reviewStatus == ReviewStatus.EDITED }
    val rejectedCount = suggestions.count { it.reviewStatus == ReviewStatus.REJECTED }
    val pendingCount = suggestions.count { it.reviewStatus == ReviewStatus.PENDING }

    // Edit Suggestion Dialog
    if (editingSuggestion != null) {
        var editedDraft by remember(editingSuggestion) {
            mutableStateOf(editingSuggestion?.userEditedText ?: editingSuggestion?.suggestedText ?: "")
        }
        AlertDialog(
            onDismissRequest = { editingSuggestion = null },
            title = { Text("Edit Suggestion Manually", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Customize the proposed wording. Keep facts and numbers accurate to your experience.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    OutlinedTextField(
                        value = editedDraft,
                        onValueChange = { editedDraft = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        label = { Text("Custom Wording") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        editingSuggestion?.id?.let { id ->
                            onEditSuggestion(id, editedDraft)
                        }
                        editingSuggestion = null
                    }
                ) {
                    Text("Apply Custom Text")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSuggestion = null }) {
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
        // Workspace Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "AI Resume Modifier",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Review tailored, evidence-backed improvements. Accept, reject, or edit each modification individually.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        // Review Progress Tracker Bar
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Review Progress",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "$acceptedCount accepted • $rejectedCount rejected • $pendingCount pending",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    // Progress bar
                    val total = maxOf(1, suggestions.size)
                    val progressRatio = (acceptedCount + rejectedCount).toFloat() / total.toFloat()
                    LinearProgressIndicator(
                        progress = { progressRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    // Bulk Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onAcceptAllSupported,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("suggestions_accept_all_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Accept Supported", style = MaterialTheme.typography.labelMedium)
                        }

                        OutlinedButton(
                            onClick = onRejectAll,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("suggestions_reject_all_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Revert All", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // Filter Pills
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Filter by Section:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedSectionFilter == null,
                        onClick = { selectedSectionFilter = null },
                        label = { Text("All (${suggestions.size})") }
                    )
                    FilterChip(
                        selected = selectedSectionFilter == ResumeSection.SUMMARY,
                        onClick = { selectedSectionFilter = if (selectedSectionFilter == ResumeSection.SUMMARY) null else ResumeSection.SUMMARY },
                        label = { Text("Summary") }
                    )
                    FilterChip(
                        selected = selectedSectionFilter == ResumeSection.EXPERIENCE,
                        onClick = { selectedSectionFilter = if (selectedSectionFilter == ResumeSection.EXPERIENCE) null else ResumeSection.EXPERIENCE },
                        label = { Text("Experience") }
                    )
                    FilterChip(
                        selected = selectedSectionFilter == ResumeSection.SKILLS,
                        onClick = { selectedSectionFilter = if (selectedSectionFilter == ResumeSection.SKILLS) null else ResumeSection.SKILLS },
                        label = { Text("Skills") }
                    )
                }
            }
        }

        // Suggestion Cards List
        if (filteredSuggestions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No suggestions match current filter.")
                    }
                }
            }
        } else {
            items(filteredSuggestions, key = { it.id }) { sug ->
                SuggestionCard(
                    suggestion = sug,
                    onAccept = { onAcceptSuggestion(sug.id) },
                    onReject = { onRejectSuggestion(sug.id) },
                    onEdit = { editingSuggestion = sug }
                )
            }
        }

        // Bottom CTA
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onOpenComparisonEditor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("suggestions_open_editor_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Open Side-by-Side Comparison & Editor",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
fun SuggestionCard(
    suggestion: ResumeSuggestion,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (suggestion.reviewStatus) {
                ReviewStatus.ACCEPTED -> StatusAccepted.copy(alpha = 0.05f)
                ReviewStatus.REJECTED -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("suggestion_card_${suggestion.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = suggestion.section.displayName,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                    EvidenceBadge(suggestion.evidenceStatus)
                }

                ReviewStatusBadge(suggestion.reviewStatus)
            }

            // Original Text Box (Diff removed styling)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Original Content:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DiffRemovedBg.copy(alpha = 0.6f))
                        .padding(10.dp)
                ) {
                    Text(
                        text = suggestion.originalText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DiffRemovedText,
                            lineHeight = 18.sp
                        )
                    )
                }
            }

            // Proposed Suggested Text Box (Diff added styling)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (suggestion.reviewStatus == ReviewStatus.EDITED) "Your Custom Edited Content:" else "Proposed Modification:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DiffAddedBg.copy(alpha = 0.6f))
                        .border(1.dp, StatusAccepted.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = suggestion.activeText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DiffAddedText,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp
                        )
                    )
                }
            }

            // Reason & Related Job Requirement
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Reason for Change: ${suggestion.reasonForChange}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                    )
                    Text(
                        text = "Related Job Requirement: \"${suggestion.relatedJobRequirement}\"",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    if (suggestion.placeholderNote != null) {
                        Text(
                            text = "Confirmation Note: ${suggestion.placeholderNote}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = StatusRequiresConfirmation,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reject Button
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("sug_reject_${suggestion.id}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (suggestion.reviewStatus == ReviewStatus.REJECTED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reject", style = MaterialTheme.typography.labelSmall)
                }

                // Edit Button
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("sug_edit_${suggestion.id}"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", style = MaterialTheme.typography.labelSmall)
                }

                // Accept Button
                Button(
                    onClick = onAccept,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(38.dp)
                        .testTag("sug_accept_${suggestion.id}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (suggestion.reviewStatus == ReviewStatus.ACCEPTED) StatusAccepted else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (suggestion.reviewStatus == ReviewStatus.ACCEPTED) "Accepted" else "Accept",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
