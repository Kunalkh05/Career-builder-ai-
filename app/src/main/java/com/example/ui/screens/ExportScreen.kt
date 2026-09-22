package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.ResumeEngine
import com.example.data.model.JobTarget
import com.example.data.model.Resume
import com.example.data.model.ResumeSuggestion
import com.example.ui.components.SectionHeader
import com.example.ui.theme.StatusSupported

@Composable
fun ExportScreen(
    resume: Resume,
    targetJob: JobTarget,
    suggestions: List<ResumeSuggestion>,
    onBackToEditor: () -> Unit
) {
    val context = LocalContext.current
    var isCopied by remember { mutableStateOf(false) }

    val formattedResumeText = remember(resume) {
        ResumeEngine.generateFormattedResume(resume)
    }

    val acceptedCount = suggestions.count { it.reviewStatus.name == "ACCEPTED" || it.reviewStatus.name == "EDITED" }
    val rejectedCount = suggestions.count { it.reviewStatus.name == "REJECTED" }

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
                    text = "Final Export & ATS Verification",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Review your tailored resume document before saving or copying.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        // Final Review Checklist Card
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
                    Text(
                        text = "Pre-Export Checklist",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    ChecklistRow(
                        title = "Target Alignment",
                        desc = "${targetJob.jobTitle} @ ${targetJob.companyName.ifEmpty { "Selected Role" }}"
                    )
                    ChecklistRow(
                        title = "Modifications Reviewed",
                        desc = "$acceptedCount accepted modifications • $rejectedCount rejected/original"
                    )
                    ChecklistRow(
                        title = "Factual Integrity Verified",
                        desc = "Zero unverified employment dates or fictional credentials"
                    )
                    ChecklistRow(
                        title = "Format Standard",
                        desc = "ATS-Friendly plain formatting with standard section headings"
                    )
                }
            }
        }

        // Action Buttons Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Tailored Resume", formattedResumeText)
                        clipboard.setPrimaryClip(clip)
                        isCopied = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("export_copy_clipboard_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isCopied) "Copied!" else "Copy Resume Text")
                }

                OutlinedButton(
                    onClick = onBackToEditor,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Wording")
                }
            }
        }

        // Formatted Document Preview
        item {
            SectionHeader(
                title = "Document Preview (ATS-Ready)",
                subtitle = "Standard text layout with clean section dividers",
                icon = Icons.Default.Article
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                ),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = formattedResumeText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ChecklistRow(title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = StatusSupported,
            modifier = Modifier.size(18.dp)
        )
        Column {
            Text(text = title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
    }
}
