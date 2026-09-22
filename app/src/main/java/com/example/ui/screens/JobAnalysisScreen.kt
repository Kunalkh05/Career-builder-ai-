package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.JobAnalysis
import com.example.data.model.JobTarget
import com.example.data.model.RequirementClassification
import com.example.data.model.RequirementItem
import com.example.ui.components.SectionHeader
import com.example.ui.theme.StatusRequiresConfirmation
import com.example.ui.theme.StatusSupported

@Composable
fun JobAnalysisScreen(
    jobTarget: JobTarget,
    jobAnalysis: JobAnalysis?,
    onProceedToMatch: () -> Unit
) {
    if (jobAnalysis == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Tools & Tech", "Responsibilities", "Required Skills", "Qualifications", "Soft Skills")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Job Description Analysis",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Extracted requirements for ${jobTarget.jobTitle} at ${jobTarget.companyName.ifEmpty { "Target Company" }}.",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }

        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 0.dp,
            divider = {}
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // Tools & Tech
                    item {
                        SectionHeader(
                            title = "Tools & Technologies (${jobAnalysis.toolsAndTechnologies.size})",
                            subtitle = "Technologies explicitly listed in posting",
                            icon = Icons.Default.Code
                        )
                    }
                    if (jobAnalysis.toolsAndTechnologies.isEmpty()) {
                        item { Text("No specific tools detected.", style = MaterialTheme.typography.bodySmall) }
                    } else {
                        items(jobAnalysis.toolsAndTechnologies.size) { idx ->
                            RequirementRow(jobAnalysis.toolsAndTechnologies[idx])
                        }
                    }
                }
                1 -> {
                    // Responsibilities
                    item {
                        SectionHeader(
                            title = "Core Responsibilities (${jobAnalysis.responsibilities.size})",
                            subtitle = "Key tasks and technical expectations",
                            icon = Icons.Default.TaskAlt
                        )
                    }
                    items(jobAnalysis.responsibilities.size) { idx ->
                        RequirementRow(jobAnalysis.responsibilities[idx])
                    }
                }
                2 -> {
                    // Required & Preferred Skills
                    item {
                        SectionHeader(
                            title = "Skill Requirements (${jobAnalysis.requiredSkills.size + jobAnalysis.preferredSkills.size})",
                            subtitle = "Explicit requirements vs preferred qualifications",
                            icon = Icons.Default.StarBorder
                        )
                    }
                    items(jobAnalysis.requiredSkills.size) { idx ->
                        RequirementRow(jobAnalysis.requiredSkills[idx])
                    }
                    items(jobAnalysis.preferredSkills.size) { idx ->
                        RequirementRow(jobAnalysis.preferredSkills[idx])
                    }
                }
                3 -> {
                    // Education & Experience
                    item {
                        SectionHeader(
                            title = "Education & Experience",
                            subtitle = "Degrees, graduation timeframe, and stated years",
                            icon = Icons.Default.School
                        )
                    }
                    items(jobAnalysis.educationRequirements.size) { idx ->
                        RequirementRow(jobAnalysis.educationRequirements[idx])
                    }
                    items(jobAnalysis.experienceRequirements.size) { idx ->
                        RequirementRow(jobAnalysis.experienceRequirements[idx])
                    }
                }
                4 -> {
                    // Soft Skills
                    item {
                        SectionHeader(
                            title = "Behavioral & Collaborative Expectations",
                            subtitle = "Communication, team dynamics, ownership",
                            icon = Icons.Default.PeopleOutline
                        )
                    }
                    items(jobAnalysis.behavioralAndSoftSkills.size) { idx ->
                        RequirementRow(jobAnalysis.behavioralAndSoftSkills[idx])
                    }
                }
            }
        }

        Button(
            onClick = onProceedToMatch,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("job_analysis_next_btn"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Proceed to Resume Match Analysis", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
fun RequirementRow(item: RequirementItem) {
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.text,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.weight(1f)
            )

            val (bgColor, textColor, label) = when (item.classification) {
                RequirementClassification.EXPLICITLY_REQUIRED -> Triple(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    MaterialTheme.colorScheme.primary,
                    "Required"
                )
                RequirementClassification.PREFERRED -> Triple(
                    StatusSupported.copy(alpha = 0.15f),
                    StatusSupported,
                    "Preferred"
                )
                RequirementClassification.OPTIONAL -> Triple(
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    "Optional"
                )
                RequirementClassification.UNCLEAR -> Triple(
                    StatusRequiresConfirmation.copy(alpha = 0.15f),
                    StatusRequiresConfirmation,
                    "Contextual"
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = bgColor,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                )
            }
        }
    }
}
