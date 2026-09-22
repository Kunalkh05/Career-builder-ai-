package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.JobTarget
import com.example.data.sample.SampleData

@Composable
fun TargetJobSetupScreen(
    jobTarget: JobTarget,
    isAnalyzing: Boolean,
    statusMessage: String,
    onUpdateJob: (JobTarget) -> Unit,
    onRunOptimization: () -> Unit
) {
    var companyName by remember { mutableStateOf(jobTarget.companyName) }
    var jobTitle by remember { mutableStateOf(jobTarget.jobTitle) }
    var experienceLevel by remember { mutableStateOf(jobTarget.experienceLevel) }
    var location by remember { mutableStateOf(jobTarget.location) }
    var jobDescription by remember { mutableStateOf(jobTarget.jobDescription) }
    var additionalInstructions by remember { mutableStateOf(jobTarget.additionalInstructions) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Target Company & Job Role Setup",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Provide the job description you want to optimize for. CareerFit AI extracts explicit expectations without making speculative assumptions.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        // 1-Tap Preset Job Templates
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Quick-Load Example Job Descriptions:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = {
                            val j = SampleData.sampleJobGoogle
                            companyName = j.companyName
                            jobTitle = j.jobTitle
                            experienceLevel = j.experienceLevel
                            location = j.location
                            jobDescription = j.jobDescription
                            additionalInstructions = j.additionalInstructions
                            onUpdateJob(j)
                        },
                        label = { Text("Google Android SWE") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.testTag("job_preset_google")
                    )

                    AssistChip(
                        onClick = {
                            val j = SampleData.sampleJobStripe
                            companyName = j.companyName
                            jobTitle = j.jobTitle
                            experienceLevel = j.experienceLevel
                            location = j.location
                            jobDescription = j.jobDescription
                            additionalInstructions = j.additionalInstructions
                            onUpdateJob(j)
                        },
                        label = { Text("Stripe Mobile SWE") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.testTag("job_preset_stripe")
                    )
                }
            }
        }

        // Target Company (Optional)
        OutlinedTextField(
            value = companyName,
            onValueChange = {
                companyName = it
                onUpdateJob(jobTarget.copy(companyName = it))
            },
            label = { Text("Company Name (Optional)") },
            placeholder = { Text("e.g. Google, Stripe, Microsoft") },
            leadingIcon = { Icon(Icons.Default.Domain, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("target_company_input"),
            singleLine = true
        )

        // Target Role Title (Required/Recommended)
        OutlinedTextField(
            value = jobTitle,
            onValueChange = {
                jobTitle = it
                onUpdateJob(jobTarget.copy(jobTitle = it))
            },
            label = { Text("Target Job Title") },
            placeholder = { Text("e.g. Software Engineer, Android Developer") },
            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("target_title_input"),
            singleLine = true
        )

        // Experience Level & Location
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = experienceLevel,
                onValueChange = {
                    experienceLevel = it
                    onUpdateJob(jobTarget.copy(experienceLevel = it))
                },
                label = { Text("Level / Seniority") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = location,
                onValueChange = {
                    location = it
                    onUpdateJob(jobTarget.copy(location = it))
                },
                label = { Text("Location (Optional)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        // Job Description Text Area
        OutlinedTextField(
            value = jobDescription,
            onValueChange = {
                jobDescription = it
                onUpdateJob(jobTarget.copy(jobDescription = it))
            },
            label = { Text("Complete Job Description *") },
            placeholder = { Text("Paste responsibilities, required skills, and qualification details from the employer's posting...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .testTag("target_jd_textarea")
        )

        // Custom Candidate Instructions
        OutlinedTextField(
            value = additionalInstructions,
            onValueChange = {
                additionalInstructions = it
                onUpdateJob(jobTarget.copy(additionalInstructions = it))
            },
            label = { Text("Optional Custom Instructions / Constraints") },
            placeholder = { Text("e.g. Prioritize Kotlin Coroutines and offline database experience") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Progress or Action Button
        if (isAnalyzing) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    CircularProgressIndicator(strokeWidth = 3.dp)
                    Column {
                        Text(
                            text = "Analyzing Compatibility...",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = statusMessage.ifEmpty { "Evaluating requirements against verified resume..." },
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        } else {
            Button(
                onClick = {
                    onUpdateJob(
                        JobTarget(
                            companyName = companyName,
                            jobTitle = jobTitle,
                            experienceLevel = experienceLevel,
                            location = location,
                            jobDescription = jobDescription,
                            additionalInstructions = additionalInstructions
                        )
                    )
                    onRunOptimization()
                },
                enabled = jobDescription.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("target_run_analysis_btn")
            ) {
                Icon(Icons.Default.AutoFixHigh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Analyze & Generate AI Improvements",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
