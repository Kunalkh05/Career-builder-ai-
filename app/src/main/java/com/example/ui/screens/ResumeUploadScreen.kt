package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.data.sample.SampleData
import com.example.ui.components.SectionHeader
import com.example.ui.theme.StatusSupported

@Composable
fun ResumeUploadScreen(
    currentResume: Resume,
    onUploadText: (String, String) -> Unit,
    onLoadSample: () -> Unit,
    onProceedToJobSetup: () -> Unit
) {
    var resumeInputText by remember { mutableStateOf(currentResume.rawText.ifEmpty { SampleData.sampleResumeSoftware.rawText }) }
    var candidateName by remember { mutableStateOf(currentResume.fullName.ifEmpty { "Alex Chen" }) }
    var isSimulatingUpload by remember { mutableStateOf(false) }
    var uploadStatusMessage by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Upload or Paste Your Resume",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Your original resume is your source of truth. We extract sections without altering your words, dates, or experience.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        // Preloaded Sample Quick-Start
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Use Standard Sample Resume",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Alex Chen • UC Berkeley CS Graduate • Android Engineering Intern",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
                Button(
                    onClick = {
                        resumeInputText = SampleData.sampleResumeSoftware.rawText
                        candidateName = "Alex Chen"
                        onLoadSample()
                    },
                    modifier = Modifier.testTag("upload_sample_resume_btn"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Load Sample")
                }
            }
        }

        // Drag & Drop / File Drop Emulator Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .clickable {
                    // Simulate file upload
                    isSimulatingUpload = true
                    uploadStatusMessage = "Processing document (PDF/DOCX)..."
                }
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(50.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Text(
                    text = "Tap to choose a file (.pdf, .docx, .txt)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Standard resume formats up to 10MB supported",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        if (isSimulatingUpload) {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Text(
                        text = uploadStatusMessage.ifEmpty { "Extracting structured text from document..." },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Candidate Name Input
        OutlinedTextField(
            value = candidateName,
            onValueChange = { candidateName = it },
            label = { Text("Candidate Full Name") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("upload_candidate_name_input"),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
        )

        // Resume Text Area
        OutlinedTextField(
            value = resumeInputText,
            onValueChange = { resumeInputText = it },
            label = { Text("Resume Content (Extracted Text)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .testTag("upload_resume_textarea"),
            placeholder = { Text("Paste your full resume text here...") }
        )

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    resumeInputText = ""
                    candidateName = ""
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text("Clear")
            }

            Button(
                onClick = {
                    onUploadText(resumeInputText, candidateName)
                    onProceedToJobSetup()
                },
                enabled = resumeInputText.isNotBlank(),
                modifier = Modifier
                    .weight(2f)
                    .height(48.dp)
                    .testTag("upload_confirm_and_next_btn"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Confirm Extracted Content", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }
}
