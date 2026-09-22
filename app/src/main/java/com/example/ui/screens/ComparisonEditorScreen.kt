package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.Resume
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun ComparisonEditorScreen(
    originalResume: Resume,
    modifiedResume: Resume,
    onUpdateModifiedResume: (Resume) -> Unit,
    onRestoreOriginal: () -> Unit,
    onProceedToQualityCheck: () -> Unit
) {
    var viewMode by remember { mutableStateOf("split") } // "split", "original", "modified"
    var isEditMode by remember { mutableStateOf(false) }

    // Editable copies
    var editableSummary by remember(modifiedResume) { mutableStateOf(modifiedResume.summary) }
    var editableSkills by remember(modifiedResume) { mutableStateOf(modifiedResume.skills.technical.joinToString(", ")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Resume Comparison & Editor",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Verify changes side-by-side or edit final wording directly.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            IconButton(
                onClick = { isEditMode = !isEditMode },
                modifier = Modifier.testTag("editor_toggle_edit_mode")
            ) {
                Icon(
                    imageVector = if (isEditMode) Icons.Default.Visibility else Icons.Default.Edit,
                    contentDescription = "Toggle edit mode",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // View Mode Selector
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = viewMode == "split",
                onClick = { viewMode = "split" },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
            ) {
                Text("Side-by-Side")
            }
            SegmentedButton(
                selected = viewMode == "original",
                onClick = { viewMode = "original" },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
            ) {
                Text("Original")
            }
            SegmentedButton(
                selected = viewMode == "modified",
                onClick = { viewMode = "modified" },
                shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
            ) {
                Text("Tailored")
            }
        }

        // Content Area
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Summary Section
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Professional Summary",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        if (viewMode == "split" || viewMode == "original") {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "Original:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(DiffRemovedBg.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = originalResume.summary.ifEmpty { "No summary provided in original resume." },
                                        style = MaterialTheme.typography.bodySmall.copy(color = DiffRemovedText)
                                    )
                                }
                            }
                        }

                        if (viewMode == "split" || viewMode == "modified") {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "Tailored & Modified:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                if (isEditMode) {
                                    OutlinedTextField(
                                        value = editableSummary,
                                        onValueChange = {
                                            editableSummary = it
                                            onUpdateModifiedResume(modifiedResume.copy(summary = it))
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        textStyle = MaterialTheme.typography.bodySmall
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(DiffAddedBg.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = modifiedResume.summary.ifEmpty { "No tailored summary." },
                                            style = MaterialTheme.typography.bodySmall.copy(color = DiffAddedText, fontWeight = FontWeight.Medium)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Experience Bullets Section
            item {
                SectionHeader(
                    title = "Experience Bullet Points",
                    subtitle = "Changes verified against target job description",
                    icon = Icons.Default.WorkHistory
                )
            }

            items(modifiedResume.experiences.size) { expIdx ->
                val origExp = originalResume.experiences.getOrNull(expIdx)
                val modExp = modifiedResume.experiences[expIdx]

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "${modExp.company} — ${modExp.role}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )

                        modExp.bullets.forEachIndexed { bIdx, modBullet ->
                            val origBullet = origExp?.bullets?.getOrNull(bIdx) ?: ""
                            val isDifferent = origBullet != modBullet

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                if ((viewMode == "split" || viewMode == "original") && isDifferent) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(DiffRemovedBg.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                            .padding(6.dp)
                                    ) {
                                        Text(
                                            text = "• $origBullet",
                                            style = MaterialTheme.typography.bodySmall.copy(color = DiffRemovedText)
                                        )
                                    }
                                }

                                if (viewMode == "split" || viewMode == "modified") {
                                    if (isEditMode) {
                                        var draftBullet by remember(modBullet) { mutableStateOf(modBullet) }
                                        OutlinedTextField(
                                            value = draftBullet,
                                            onValueChange = {
                                                draftBullet = it
                                                val newBullets = modExp.bullets.toMutableList()
                                                newBullets[bIdx] = it
                                                val newExps = modifiedResume.experiences.toMutableList()
                                                newExps[expIdx] = modExp.copy(bullets = newBullets)
                                                onUpdateModifiedResume(modifiedResume.copy(experiences = newExps))
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            textStyle = MaterialTheme.typography.bodySmall
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    if (isDifferent) DiffAddedBg.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .padding(6.dp)
                                        ) {
                                            Text(
                                                text = "• $modBullet",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = if (isDifferent) DiffAddedText else MaterialTheme.colorScheme.onSurface,
                                                    fontWeight = if (isDifferent) FontWeight.Medium else FontWeight.Normal
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Skills Section
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Technical Skills Organization",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        if (isEditMode) {
                            OutlinedTextField(
                                value = editableSkills,
                                onValueChange = {
                                    editableSkills = it
                                    val list = it.split(",").map { s -> s.trim() }.filter { s -> s.isNotEmpty() }
                                    onUpdateModifiedResume(
                                        modifiedResume.copy(skills = modifiedResume.skills.copy(technical = list))
                                    )
                                },
                                label = { Text("Comma-separated skills") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                text = modifiedResume.skills.technical.joinToString(" • "),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onRestoreOriginal,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Restore Original", style = MaterialTheme.typography.labelSmall)
            }

            Button(
                onClick = onProceedToQualityCheck,
                modifier = Modifier
                    .weight(1.5f)
                    .height(48.dp)
                    .testTag("editor_proceed_to_quality_btn"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Run Quality Check", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}
