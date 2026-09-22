package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ResumeVersion
import com.example.ui.components.SectionHeader
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun VersionManagementScreen(
    versions: List<ResumeVersion>,
    onOpenVersion: (ResumeVersion) -> Unit,
    onDeleteVersion: (String) -> Unit,
    onStartNewOptimization: () -> Unit
) {
    var versionToCompareA by remember { mutableStateOf<ResumeVersion?>(null) }
    var versionToCompareB by remember { mutableStateOf<ResumeVersion?>(null) }
    var isComparing by remember { mutableStateOf(false) }

    if (isComparing && versionToCompareA != null && versionToCompareB != null) {
        val a = versionToCompareA!!
        val b = versionToCompareB!!
        AlertDialog(
            onDismissRequest = { isComparing = false },
            title = { Text("Compare Resume Versions", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Comparing \"${a.versionName}\" vs \"${b.versionName}\"",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Divider()
                    Text("Target A: ${a.targetRole} @ ${a.targetCompany}", style = MaterialTheme.typography.bodySmall)
                    Text("Target B: ${b.targetRole} @ ${b.targetCompany}", style = MaterialTheme.typography.bodySmall)
                    Divider()
                    Text("Summary A:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Text(a.modifiedResume.summary.take(160) + "...", style = MaterialTheme.typography.bodySmall)
                    Text("Summary B:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Text(b.modifiedResume.summary.take(160) + "...", style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                Button(onClick = { isComparing = false }) {
                    Text("Done")
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
                    text = "Saved Resume Versions",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Manage company-tailored resumes. Open any version for review, export, or further refinement.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        if (versions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "No Saved Versions Found",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Once you review and approve AI suggestions, save your tailored resume version here.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(
                            onClick = onStartNewOptimization,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Start New Optimization")
                        }
                    }
                }
            }
        } else {
            items(versions) { version ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("version_item_${version.id}")
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = version.versionName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Target: ${version.targetRole} • ${version.targetCompany.ifEmpty { "General Role" }}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = "${version.acceptedCount} tailored changes",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                )
                            }
                            Text(
                                text = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(version.lastModifiedAt)),
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (versionToCompareA == null) {
                                        versionToCompareA = version
                                    } else {
                                        versionToCompareB = version
                                        isComparing = true
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Compare", style = MaterialTheme.typography.labelSmall)
                            }

                            Button(
                                onClick = { onOpenVersion(version) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Open & Export", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
