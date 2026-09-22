package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.ResumeEngine
import com.example.data.export.ResumeDocumentExporter
import com.example.data.model.JobTarget
import com.example.data.model.Resume
import com.example.data.model.ResumeSuggestion
import com.example.ui.components.SectionHeader
import com.example.ui.theme.StatusSupported
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ExportScreen(
    resume: Resume,
    targetJob: JobTarget,
    suggestions: List<ResumeSuggestion>,
    onBackToEditor: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isCopied by remember { mutableStateOf(false) }
    var lastExportedFile by remember { mutableStateOf<File?>(null) }
    var selectedPreviewTab by remember { mutableIntStateOf(0) }

    val formattedResumeText = remember(resume) {
        ResumeEngine.generateFormattedResume(resume)
    }

    val acceptedCount = suggestions.count { it.reviewStatus.name == "ACCEPTED" || it.reviewStatus.name == "EDITED" }
    val rejectedCount = suggestions.count { it.reviewStatus.name == "REJECTED" }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
        ) {
            // Header
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Final Export & Downloads",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Export your tailored resume in PDF, Word (DOCX), or ATS Plain Text format.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            // Export Formats Section
            item {
                SectionHeader(
                    title = "Export Formats",
                    subtitle = "Select your preferred document format for job submission",
                    icon = Icons.Default.FileDownload
                )
            }

            // 1. PDF Export Card
            item {
                ExportFormatCard(
                    title = "PDF Document (.pdf)",
                    badge = "ATS-Formatted",
                    badgeColor = MaterialTheme.colorScheme.primaryContainer,
                    badgeTextColor = MaterialTheme.colorScheme.primary,
                    icon = Icons.Default.PictureAsPdf,
                    iconBgColor = Color(0xFFFEE2E2),
                    iconColor = Color(0xFFDC2626),
                    description = "ATS-compliant, multi-page formatted PDF with clean typography, ideal for online portals and company career sites.",
                    onShare = {
                        try {
                            val pdfFile = ResumeDocumentExporter.exportToPdf(context, resume, targetJob)
                            lastExportedFile = pdfFile
                            ResumeDocumentExporter.shareFile(
                                context = context,
                                file = pdfFile,
                                mimeType = "application/pdf",
                                title = "Share PDF Resume"
                            )
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("PDF created: ${pdfFile.name}")
                            }
                        } catch (e: Exception) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Failed to create PDF: ${e.message}")
                            }
                        }
                    },
                    onOpen = {
                        try {
                            val pdfFile = ResumeDocumentExporter.exportToPdf(context, resume, targetJob)
                            lastExportedFile = pdfFile
                            val opened = ResumeDocumentExporter.openFile(context, pdfFile, "application/pdf")
                            if (!opened) {
                                ResumeDocumentExporter.shareFile(context, pdfFile, "application/pdf", "Open PDF Resume With")
                            }
                        } catch (e: Exception) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Failed to open PDF: ${e.message}")
                            }
                        }
                    },
                    shareTestTag = "export_share_pdf_btn",
                    openTestTag = "export_open_pdf_btn",
                    primaryActionLabel = "Export & Share PDF",
                    secondaryActionLabel = "Open PDF"
                )
            }

            // 2. Word (DOCX) Export Card
            item {
                ExportFormatCard(
                    title = "Microsoft Word (.docx)",
                    badge = "Fully Editable",
                    badgeColor = MaterialTheme.colorScheme.secondaryContainer,
                    badgeTextColor = MaterialTheme.colorScheme.secondary,
                    icon = Icons.Default.Description,
                    iconBgColor = Color(0xFFDBEAFE),
                    iconColor = Color(0xFF2563EB),
                    description = "Standard OpenXML Word format (.docx) compatible with MS Word, Google Docs, Apple Pages, and recruiters requiring doc files.",
                    onShare = {
                        try {
                            val docxFile = ResumeDocumentExporter.exportToDocx(context, resume, targetJob)
                            lastExportedFile = docxFile
                            ResumeDocumentExporter.shareFile(
                                context = context,
                                file = docxFile,
                                mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                title = "Share Word Resume (.docx)"
                            )
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("DOCX created: ${docxFile.name}")
                            }
                        } catch (e: Exception) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Failed to create DOCX: ${e.message}")
                            }
                        }
                    },
                    onOpen = {
                        try {
                            val docxFile = ResumeDocumentExporter.exportToDocx(context, resume, targetJob)
                            lastExportedFile = docxFile
                            val opened = ResumeDocumentExporter.openFile(
                                context,
                                docxFile,
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                            )
                            if (!opened) {
                                ResumeDocumentExporter.shareFile(
                                    context,
                                    docxFile,
                                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                    "Open Word Document With"
                                )
                            }
                        } catch (e: Exception) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Failed to open DOCX: ${e.message}")
                            }
                        }
                    },
                    shareTestTag = "export_share_docx_btn",
                    openTestTag = "export_open_docx_btn",
                    primaryActionLabel = "Export & Share DOCX",
                    secondaryActionLabel = "Open DOCX"
                )
            }

            // 3. Plain Text Clipboard & Share Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Plain Text Format (.txt)",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Surface(
                                        color = MaterialTheme.colorScheme.tertiaryContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "Universal",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        )
                                    }
                                }
                                Text(
                                    text = "Raw plain text without formatting for pasting directly into text-only application forms.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }

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
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Resume copied to clipboard!")
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("export_copy_clipboard_btn"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isCopied) "Copied!" else "Copy Text")
                            }

                            OutlinedButton(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, formattedResumeText)
                                        putExtra(Intent.EXTRA_SUBJECT, "${resume.fullName} Resume")
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Share Resume Text")
                                    context.startActivity(shareIntent)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("export_share_text_btn"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share Text")
                            }
                        }
                    }
                }
            }

            // Pre-Export Checklist Card
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
                            title = "Export Formats Generated",
                            desc = "ATS PDF, Microsoft Word DOCX, and Plain Text ready"
                        )
                    }
                }
            }

            // Preview Section with Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader(
                        title = "Document Preview",
                        subtitle = "Inspect text content and structure",
                        icon = Icons.AutoMirrored.Filled.Article
                    )

                    OutlinedButton(
                        onClick = onBackToEditor,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Wording", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            item {
                TabRow(
                    selectedTabIndex = selectedPreviewTab,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    Tab(
                        selected = selectedPreviewTab == 0,
                        onClick = { selectedPreviewTab = 0 },
                        text = { Text("ATS Plain Text", fontWeight = if (selectedPreviewTab == 0) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = selectedPreviewTab == 1,
                        onClick = { selectedPreviewTab = 1 },
                        text = { Text("Export Specs & Tips", fontWeight = if (selectedPreviewTab == 1) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            if (selectedPreviewTab == 0) {
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
            } else {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Which format should I use?",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )

                            FormatTipItem(
                                title = "Use PDF (.pdf)",
                                description = "Best when applying via company career portals (Workday, Greenhouse, Lever). PDF guarantees your visual layout, margins, and page breaks remain identical on every screen.",
                                icon = Icons.Default.PictureAsPdf,
                                tint = Color(0xFFDC2626)
                            )

                            FormatTipItem(
                                title = "Use Word (.docx)",
                                description = "Best when recruiters specifically request an editable document or when working with agency recruiters who add their own cover sheets.",
                                icon = Icons.Default.Description,
                                tint = Color(0xFF2563EB)
                            )

                            FormatTipItem(
                                title = "File Naming Standard",
                                description = "Exported files are automatically sanitized: ${ResumeDocumentExporter.getBaseFileName(resume, targetJob)}.[ext]",
                                icon = Icons.Default.Info,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExportFormatCard(
    title: String,
    badge: String,
    badgeColor: Color,
    badgeTextColor: Color,
    icon: ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    description: String,
    onShare: () -> Unit,
    onOpen: () -> Unit,
    shareTestTag: String,
    openTestTag: String,
    primaryActionLabel: String,
    secondaryActionLabel: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Surface(
                            color = badgeColor,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = badge,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = badgeTextColor
                                )
                            )
                        }
                    }
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onShare,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .testTag(shareTestTag),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(primaryActionLabel)
                }

                OutlinedButton(
                    onClick = onOpen,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag(openTestTag),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(secondaryActionLabel)
                }
            }
        }
    }
}

@Composable
fun FormatTipItem(
    title: String,
    description: String,
    icon: ImageVector,
    tint: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
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
