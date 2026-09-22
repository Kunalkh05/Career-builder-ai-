package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Resume
import com.example.data.sample.SampleData
import com.example.ui.components.MarkerHighlightText
import com.example.ui.components.StickyNote
import com.example.ui.theme.*

@Composable
fun LandingScreen(
    onStartOptimizing: () -> Unit,
    onExploreDashboard: () -> Unit,
    onLoadSampleDemo: () -> Unit,
    onLoadProfile: ((Resume) -> Unit)? = null,
    isDarkTheme: Boolean = false
) {
    val isDark = isDarkTheme || MaterialTheme.colorScheme.background == BackgroundDark
    val scrollState = rememberScrollState()
    var showHowItWorksDialog by remember { mutableStateOf(false) }
    var selectedBeforeAfterTab by remember { mutableIntStateOf(0) } // 0: After, 1: Before, 2: Split
    var stickyAffirmationIndex by remember { mutableIntStateOf(0) }

    val affirmations = listOf(
        "Better Resume, Brighter Future :)",
        "Same You. Stronger Story.",
        "Not just keywords. Real guidance.",
        "Good Resumes Open Doors ✦"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // =========================================================================
        // HERO SECTION: Editorial Chalkboard Desk
        // =========================================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = if (isDark) {
                            listOf(EditorialChalkboard, EditorialChalkboardSurface)
                        } else {
                            listOf(Color(0xFFF4F7F4), Color(0xFFE8EFEA))
                        }
                    )
                )
                .padding(horizontal = 20.dp, vertical = 28.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top Trust Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isDark) EditorialChalkboardElevated else Color(0xFFE0EAE3),
                    border = BorderStroke(1.dp, if (isDark) EditorialChalkboardBorder else Color(0xFFC8D7CD))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (isDark) EditorialCitron else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "EDITORIAL RESUME INTELLIGENCE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) EditorialCitron else MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }

                // Eyebrow
                Text(
                    text = "YOUR STORY. A BRIGHTER TOMORROW.",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color(0xFFA1B3A8) else Color(0xFF4A6054),
                        letterSpacing = 2.sp
                    )
                )

                // Editorial Serif Headline with highlighted text
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "More",
                            style = MaterialTheme.typography.displayMedium.copy(
                                color = if (isDark) Color.White else EditorialInk,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Opportunities",
                            style = MaterialTheme.typography.displayMedium.copy(
                                color = if (isDark) EditorialCitron else BrandSecondaryLight,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        text = "Start with a Smarter Resume.",
                        style = MaterialTheme.typography.displayMedium.copy(
                            color = if (isDark) Color.White else EditorialInk,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    // Yellow decorative brush stroke
                    Box(
                        modifier = Modifier
                            .width(220.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(EditorialCitron)
                    )
                }

                // Interactive Sticky Note pinned to desk
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    StickyNote(
                        text = affirmations[stickyAffirmationIndex],
                        rotation = -3f,
                        onClick = {
                            stickyAffirmationIndex = (stickyAffirmationIndex + 1) % affirmations.size
                        },
                        modifier = Modifier.testTag("hero_sticky_note")
                    )
                }

                // Subtitle
                Text(
                    text = "CareerFit AI helps you tailor your resume for real companies and roles — with honest suggestions, clear insights, and complete control.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = if (isDark) Color(0xFFC7D3CB) else Color(0xFF47584D),
                        lineHeight = 24.sp
                    )
                )

                // Primary & Secondary CTAs
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onStartOptimizing,
                            modifier = Modifier
                                .weight(1.3f)
                                .height(52.dp)
                                .testTag("landing_start_button"),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EditorialCitron,
                                contentColor = EditorialOnCitron
                            )
                        ) {
                            Text(
                                text = "Start for Free",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        OutlinedButton(
                            onClick = { showHowItWorksDialog = true },
                            modifier = Modifier
                                .weight(1.1f)
                                .height(52.dp)
                                .testTag("landing_watch_video"),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isDark) Color.White else EditorialInk
                            ),
                            border = BorderStroke(1.dp, if (isDark) EditorialChalkboardBorder else Color(0xFFC8D7CD))
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircleOutline,
                                contentDescription = null,
                                tint = if (isDark) EditorialCitron else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "How It Works",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    // Instant Demo Pill
                    FilledTonalButton(
                        onClick = onLoadSampleDemo,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("landing_sample_demo_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isDark) EditorialChalkboardElevated else Color(0xFFE2EBE5),
                            contentColor = if (isDark) EditorialCitron else Color(0xFF153F2B)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Try Demo with Instant Sample (Alex Chen)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                // Social Proof Strip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDark) EditorialChalkboardElevated.copy(alpha = 0.8f) else Color(0xFFE5EDE8),
                    border = BorderStroke(1.dp, if (isDark) EditorialChalkboardBorder else Color(0xFFCAD8CF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Stacked avatar initials
                        Row(
                            horizontalArrangement = Arrangement.spacedBy((-6).dp)
                        ) {
                            listOf("SV" to Color(0xFF0284C7), "AK" to Color(0xFF0D9488), "RS" to Color(0xFFE11D48), "NP" to Color(0xFFD97706)).forEach { (initials, color) ->
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(2.dp, if (isDark) EditorialChalkboard else Color(0xFFE5EDE8), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initials,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Trusted by students and job seekers building their next big step.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isDark) Color(0xFFB4C5BC) else Color(0xFF42564A),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Realistic Paper Resume Sheet on Desk
                EditorialDeskResumePreview(
                    isDark = isDark,
                    onLoadSneha = {
                        if (onLoadProfile != null) {
                            onLoadProfile(SampleData.sampleResumeSneha)
                        } else {
                            onLoadSampleDemo()
                        }
                    }
                )
            }
        }

        // =========================================================================
        // FEATURES SECTION: "Everything you need to stand out, in one place."
        // =========================================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isDark) EditorialChalkboard else EditorialPaperCream)
                .padding(horizontal = 20.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "FEATURES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) EditorialCitron else MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.5.sp
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Everything you need to",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = if (isDark) Color.White else EditorialInk,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MarkerHighlightText(
                        text = "stand out",
                        highlightColor = if (isDark) EditorialCitron.copy(alpha = 0.35f) else EditorialMarkerHighlight,
                        textStyle = MaterialTheme.typography.headlineLarge.copy(
                            color = if (isDark) Color.White else EditorialInk,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = ", in one place.",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = if (isDark) Color.White else EditorialInk,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Text(
                    text = "✍ Not just keywords. Real guidance.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontStyle = FontStyle.Italic,
                        color = if (isDark) Color(0xFFA1B3A8) else EditorialMutedInk,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 4 Distinctive Editorial Feature Cards
            val features = listOf(
                EditorialFeatureItem(
                    icon = Icons.Default.FactCheck,
                    iconBg = Color(0xFFE0F2FE),
                    iconTint = Color(0xFF0369A1),
                    title = "Role-Focused Suggestions",
                    desc = "Get tailored improvements based on the company and role you're targeting."
                ),
                EditorialFeatureItem(
                    icon = Icons.Default.Insights,
                    iconBg = Color(0xFFEDE9FE),
                    iconTint = Color(0xFF6D28D9),
                    title = "Skill Gap Analysis",
                    desc = "See what matches, what's missing, and how to improve with respectful citations."
                ),
                EditorialFeatureItem(
                    icon = Icons.Default.Edit,
                    iconBg = Color(0xFFDCFCE7),
                    iconTint = Color(0xFF15803D),
                    title = "Editable Results",
                    desc = "Accept, edit, or reject changes — you're always in complete human control."
                ),
                EditorialFeatureItem(
                    icon = Icons.Default.Apartment,
                    iconBg = Color(0xFFFEF3C7),
                    iconTint = Color(0xFFB45309),
                    title = "Company Insights",
                    desc = "Understand common tools, architecture patterns, and technologies used by your target employer."
                )
            )

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                features.forEach { item ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) EditorialChalkboardSurface else EditorialPaperWhite
                        ),
                        border = BorderStroke(1.dp, if (isDark) EditorialChalkboardBorder else EditorialPaperBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDark) item.iconBg.copy(alpha = 0.25f) else item.iconBg,
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null,
                                        tint = if (isDark) EditorialCitron else item.iconTint,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else EditorialInk
                                    )
                                )
                                Text(
                                    text = item.desc,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isDark) Color(0xFFC7D3CB) else EditorialMutedInk,
                                        lineHeight = 20.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // INTERACTIVE BEFORE / AFTER STUDIO
        // =========================================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isDark) EditorialChalkboardSurface else Color(0xFFEFF5F1))
                .padding(horizontal = 20.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "From your current resume",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = if (isDark) Color.White else EditorialInk,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "to your",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = if (isDark) Color.White else EditorialInk,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = EditorialCitron
                    ) {
                        Text(
                            text = "target role.",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = EditorialOnCitron,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Text(
                    text = "Upload. Analyze. Improve. Apply.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isDark) Color(0xFFA1B3A8) else EditorialMutedInk,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            // Interactive Tab Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) EditorialChalkboardElevated else Color(0xFFDFE7E1))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("After (Suggested)", "Before", "Side-by-Side").forEachIndexed { index, label ->
                    val isSelected = selectedBeforeAfterTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    isSelected && index == 0 -> EditorialCitron
                                    isSelected -> if (isDark) Color.White else MaterialTheme.colorScheme.primary
                                    else -> Color.Transparent
                                }
                            )
                            .clickable { selectedBeforeAfterTab = index }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isSelected && index == 0 -> EditorialOnCitron
                                    isSelected -> if (isDark) EditorialInk else Color.White
                                    else -> if (isDark) Color(0xFFCBD5E1) else Color(0xFF4A5C51)
                                }
                            )
                        )
                    }
                }
            }

            // Before / After Display Box
            Crossfade(
                targetState = selectedBeforeAfterTab,
                modifier = Modifier.animateContentSize(),
                label = "before_after_crossfade"
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> AfterCardView(isDark = isDark)
                    1 -> BeforeCardView(isDark = isDark)
                    else -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            BeforeCardView(isDark = isDark)
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = EditorialCitron,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDownward,
                                            contentDescription = null,
                                            tint = EditorialOnCitron,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                            AfterCardView(isDark = isDark)
                        }
                    }
                }
            }

            // Editorial Annotations & CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✦ Same You. Stronger Story.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isDark) EditorialCitron else MaterialTheme.colorScheme.primary,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold
                    )
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isDark) EditorialChalkboardElevated else Color(0xFFE2EBE5),
                    border = BorderStroke(1.dp, if (isDark) EditorialCitron.copy(alpha = 0.4f) else Color(0xFFBDD2C4))
                ) {
                    Text(
                        text = "Built for Real Careers ↗",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isDark) EditorialCitron else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Button(
                onClick = onStartOptimizing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("landing_see_in_action_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) EditorialPaperWarm else MaterialTheme.colorScheme.primary,
                    contentColor = if (isDark) EditorialInk else Color.White
                )
            ) {
                Text(
                    text = "See It In Action",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // =========================================================================
        // METRICS BAR: 100K+ • 4.8/5 • 80%
        // =========================================================================
        Surface(
            color = if (isDark) Color(0xFF0B0F0D) else Color(0xFFE5EDE8),
            border = BorderStroke(1.dp, if (isDark) EditorialChalkboardBorder else Color(0xFFCCD9D0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricColumn(number = "100K+", label = "Job seekers supported", isDark = isDark)
                MetricColumn(number = "4.8/5", label = "Average user rating", hasSparkle = true, isDark = isDark)
                MetricColumn(number = "80%", label = "Better interview calls", hasSparkle = true, isDark = isDark)
            }
        }

        // =========================================================================
        // REAL STORIES: "From learners to achievers."
        // =========================================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isDark) EditorialChalkboard else EditorialPaperCream)
                .padding(horizontal = 20.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "REAL STORIES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) EditorialCitron else MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.5.sp
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "From learners to achievers.",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else EditorialInk
                        )
                    )
                }
            }

            val testimonials = listOf(
                Triple(
                    "Aman K.",
                    "Final Year Student",
                    "\"CareerFit helped me present my projects in a much better way. I started getting more interview calls!\""
                ),
                Triple(
                    "Ritika S.",
                    "Aspiring Data Analyst",
                    "\"The suggestions were practical and relevant. I loved that I could review and tweak every change myself.\""
                ),
                Triple(
                    "Neeraj P.",
                    "Software Developer",
                    "\"Simple, clean, and actually helpful. It feels like it's built for candidates who want honest feedback.\""
                )
            )

            testimonials.forEach { (name, role, quote) ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) EditorialChalkboardSurface else EditorialPaperWhite
                    ),
                    border = BorderStroke(1.dp, if (isDark) EditorialChalkboardBorder else EditorialPaperBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = quote,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isDark) Color(0xFFE2E8E4) else EditorialInk,
                                lineHeight = 21.sp,
                                fontStyle = FontStyle.Italic
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isDark) EditorialChalkboardElevated else Color(0xFFE0EAE3)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name.take(1),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) EditorialCitron else MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                            Column {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else EditorialInk
                                    )
                                )
                                Text(
                                    text = role,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isDark) Color(0xFFA1B3A8) else EditorialMutedInk
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // TRUST & SAFETY POLICY
        // =========================================================================
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) EditorialChalkboardElevated else EditorialPaperWarm
            ),
            border = BorderStroke(1.dp, if (isDark) EditorialChalkboardBorder else EditorialPaperBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GppGood,
                        contentDescription = null,
                        tint = StatusSupported,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Factual Integrity & Candidate Trust",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else EditorialInk
                        )
                    )
                }
                Text(
                    text = "• Zero Hallucinations: We never invent employers, degree dates, or unconfirmed capabilities.\n" +
                            "• Neutral Gap Language: Missing qualifications are flagged respectfully for candidate confirmation.\n" +
                            "• Human-in-the-Loop: No AI suggestion overwrites your resume without explicit approval.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isDark) Color(0xFFC7D3CB) else EditorialMutedInk,
                        lineHeight = 19.sp
                    )
                )
            }
        }

        // =========================================================================
        // EDITORIAL FOOTER
        // =========================================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isDark) EditorialChalkboard else Color(0xFF14241B))
                .padding(horizontal = 20.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = EditorialCitron,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "CareerFit AI",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Text(
                text = "Better Resumes. Brighter Futures.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFFA1B3A8)
                )
            )

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = EditorialStickyYellow,
                modifier = Modifier.rotate(1.5f)
            ) {
                Text(
                    text = "✦ Good Resumes Open Doors",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = EditorialStickyText
                    )
                )
            }

            HorizontalDivider(
                color = if (isDark) EditorialChalkboardBorder else Color(0xFF27382E),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = "© 2026 CareerFit AI. Built with Gemini Intelligence & Android Jetpack Compose.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isDark) Color(0xFF6B7F74) else Color(0xFF8DA396),
                    fontSize = 11.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }

    // "How It Works" Walkthrough Modal Dialog
    if (showHowItWorksDialog) {
        Dialog(onDismissRequest = { showHowItWorksDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isDark) EditorialChalkboardSurface else EditorialPaperWhite,
                border = BorderStroke(1.dp, if (isDark) EditorialChalkboardBorder else EditorialPaperBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "How CareerFit Works",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else EditorialInk
                            )
                        )
                        IconButton(onClick = { showHowItWorksDialog = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = if (isDark) Color.White else EditorialInk
                            )
                        }
                    }

                    val walkthroughSteps = listOf(
                        "1. Upload Your Resume" to "Your existing background is parsed and permanently locked as the source of truth.",
                        "2. Add Target Job Description" to "We extract core technical stacks, required qualifications, and employer priorities.",
                        "3. Compatibility & Gap Check" to "Transparent match score with careful, respectful notices for unconfirmed requirements.",
                        "4. Interactive Modifier Review" to "Examine proposed rewording cards with reason-for-change and cited job requirements.",
                        "5. Safety Audit & Clean Export" to "Zero hallucinations check and 1-tap plain ATS-formatted export."
                    )

                    walkthroughSteps.forEachIndexed { idx, (title, desc) ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(EditorialCitron),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = EditorialOnCitron
                                    )
                                )
                            }
                            Column {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else EditorialInk
                                    )
                                )
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isDark) Color(0xFFC7D3CB) else EditorialMutedInk
                                    )
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            showHowItWorksDialog = false
                            onStartOptimizing()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EditorialCitron,
                            contentColor = EditorialOnCitron
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Get Started Now",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// HELPER COMPOSABLES
// -----------------------------------------------------------------------------

data class EditorialFeatureItem(
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val title: String,
    val desc: String
)

@Composable
private fun EditorialDeskResumePreview(
    isDark: Boolean,
    onLoadSneha: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) EditorialChalkboardElevated else EditorialPaperWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 2.dp else 8.dp),
        border = BorderStroke(1.dp, if (isDark) EditorialChalkboardBorder else EditorialPaperBorder),
        modifier = Modifier
            .fillMaxWidth()
            .rotate(0.8f)
            .padding(top = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Sneha Verma",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else EditorialInk
                        )
                    )
                    Text(
                        text = "Software Developer • Bengaluru, India",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isDark) Color(0xFFA1B3A8) else EditorialMutedInk
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = EditorialCitron
                ) {
                    Text(
                        text = "Same Skills, New Possibilities ↗",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = EditorialOnCitron,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            HorizontalDivider(
                color = if (isDark) EditorialChalkboardBorder else EditorialPaperBorder
            )

            Text(
                text = "EDUCATION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) EditorialCitron else MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
            )
            Text(
                text = "B.Tech in Computer Science — XYZ University (2021 – 2025)",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isDark) Color(0xFFE2E8E4) else EditorialInk
                )
            )

            Text(
                text = "PROJECTS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) EditorialCitron else MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "• StudentHub — Web platform for college communities (React, Node.js)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isDark) Color(0xFFE2E8E4) else EditorialInk
                    )
                )
                Text(
                    text = "• FocusMate — Productivity tracker using AI nudges",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isDark) Color(0xFFE2E8E4) else EditorialInk
                    )
                )
            }

            Text(
                text = "SKILLS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) EditorialCitron else MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
            )
            Text(
                text = "Python, JavaScript, React, Node.js, SQL",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Color(0xFFE2E8E4) else EditorialInk
                )
            )

            HorizontalDivider(
                color = if (isDark) EditorialChalkboardBorder else EditorialPaperBorder
            )

            Button(
                onClick = onLoadSneha,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EditorialCitron,
                    contentColor = EditorialOnCitron
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("landing_load_sneha_button")
            ) {
                Text(
                    text = "Tailor This Sample Resume →",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun BeforeCardView(isDark: Boolean) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) EditorialChalkboardElevated else Color(0xFFF7FAF8)
        ),
        border = BorderStroke(1.dp, if (isDark) EditorialChalkboardBorder else Color(0xFFD4DFD7)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isDark) Color(0xFF475569) else Color(0xFF64748B)
            ) {
                Text(
                    text = "Before",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Text(
                text = "Project: Task Manager",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else EditorialInk
                )
            )
            Text(
                text = "Developed a task management app using React and Node.js.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155),
                    lineHeight = 18.sp
                )
            )

            Text(
                text = "Skills: JavaScript, React, Node.js",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            )
            Text(
                text = "Education: B.Tech, XYZ University",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            )
        }
    }
}

@Composable
private fun AfterCardView(isDark: Boolean) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) EditorialChalkboardSurface else EditorialPaperWhite
        ),
        border = BorderStroke(1.dp, EditorialCitron),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 2.dp else 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = EditorialCitron
            ) {
                Text(
                    text = "After (Suggested)",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = EditorialOnCitron
                    )
                )
            }

            Text(
                text = "Project: Task Management App",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else EditorialInk
                )
            )
            Text(
                text = "Developed a full-stack task management application using React and Node.js, implementing REST APIs and user authentication.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isDark) Color(0xFFE2E8F0) else EditorialInk,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Skills:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else EditorialInk
                    )
                )
                Text(
                    text = "JavaScript, React, Node.js, REST APIs, MongoDB",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFF86EFAC) else Color(0xFF15803D)
                    )
                )
            }

            Text(
                text = "Education: B.Tech, XYZ University",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isDark) Color(0xFFA1B3A7) else EditorialMutedInk
                )
            )
        }
    }
}

@Composable
private fun MetricColumn(
    number: String,
    label: String,
    hasSparkle: Boolean = false,
    isDark: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = number,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = if (hasSparkle) EditorialCitron else if (isDark) Color.White else EditorialInk,
                    fontSize = 24.sp
                )
            )
            if (hasSparkle) {
                Text(
                    text = " 彡",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = EditorialCitron,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF4A5D52),
                fontSize = 11.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}
