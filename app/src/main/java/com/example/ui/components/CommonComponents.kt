package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontStyle
import com.example.data.model.EvidenceStatus
import com.example.data.model.ReviewStatus
import com.example.ui.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onBack: (() -> Unit)? = null,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        navigationIcon = {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("nav_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate back"
                    )
                }
            } else {
                IconButton(
                    onClick = { onNavigate(Screen.LANDING) },
                    modifier = Modifier.testTag("nav_home_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDarkTheme) EditorialChalkboardElevated else MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "CareerFit Home",
                            tint = if (isDarkTheme) EditorialCitron else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        title = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "CareerFit",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EditorialCitron
                    ) {
                        Text(
                            text = "AI",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = EditorialOnCitron
                            )
                        )
                    }
                }
                Text(
                    text = "Same You. A Stronger Resume.",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                )
            }
        },
        actions = {
            // Navigation pills
            if (currentScreen != Screen.LANDING) {
                IconButton(
                    onClick = { onNavigate(Screen.DASHBOARD) },
                    modifier = Modifier.testTag("header_dashboard_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Dashboard,
                        contentDescription = "Dashboard",
                        tint = if (currentScreen == Screen.DASHBOARD) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = { onNavigate(Screen.VERSION_MANAGEMENT) },
                    modifier = Modifier.testTag("header_versions_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Saved Versions",
                        tint = if (currentScreen == Screen.VERSION_MANAGEMENT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier.testTag("header_theme_toggle")
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isDarkTheme) EditorialChalkboardElevated else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, if (isDarkTheme) EditorialChalkboardBorder else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                            tint = if (isDarkTheme) EditorialCitron else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    )
}

/**
 * Editorial Yellow Marker highlight behind text
 */
@Composable
fun MarkerHighlightText(
    text: String,
    modifier: Modifier = Modifier,
    highlightColor: Color = EditorialMarkerHighlight,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleLarge
) {
    val isDark = MaterialTheme.colorScheme.background == BackgroundDark
    val actualHighlight = if (isDark) EditorialCitron.copy(alpha = 0.28f) else highlightColor
    val textColor = if (isDark) MaterialTheme.colorScheme.onSurface else EditorialInk

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 8.dp, bottom = 2.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(actualHighlight)
        )
        Text(
            text = text,
            style = textStyle.copy(color = textColor),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

/**
 * Pinned Sticky Note with authentic slight tilt, pin shadow, and warm yellow tone
 */
@Composable
fun StickyNote(
    text: String,
    modifier: Modifier = Modifier,
    rotation: Float = -3f,
    onClick: (() -> Unit)? = null
) {
    val isDark = MaterialTheme.colorScheme.background == BackgroundDark
    val noteBg = if (isDark) Color(0xFF2A2510) else EditorialStickyYellow
    val noteBorder = if (isDark) Color(0xFF5A4D1A) else EditorialStickyYellowBorder
    val noteTextColor = if (isDark) Color(0xFFFEF388) else EditorialStickyText

    val baseModifier = modifier
        .rotate(rotation)
        .shadow(
            elevation = 6.dp,
            shape = RoundedCornerShape(2.dp),
            ambientColor = Color.Black.copy(alpha = if (isDark) 0.5f else 0.15f),
            spotColor = Color.Black.copy(alpha = if (isDark) 0.6f else 0.25f)
        )
        .clip(RoundedCornerShape(2.dp))
        .background(noteBg)
        .border(1.dp, noteBorder, RoundedCornerShape(2.dp))

    val interactiveModifier = if (onClick != null) {
        baseModifier.clickable { onClick() }
    } else {
        baseModifier
    }

    Box(
        modifier = interactiveModifier
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Little red pin at the top
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(EditorialPinRed)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = noteTextColor,
                    lineHeight = 18.sp
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}


@Composable
fun WorkflowStepper(
    currentScreen: Screen,
    onStepClick: (Screen) -> Unit
) {
    val steps = listOf(
        Triple(Screen.RESUME_UPLOAD, "Upload", Icons.Default.UploadFile),
        Triple(Screen.TARGET_JOB_SETUP, "Target Job", Icons.Default.WorkOutline),
        Triple(Screen.JOB_ANALYSIS, "Analysis", Icons.Default.Insights),
        Triple(Screen.MATCH_ANALYSIS, "Match", Icons.Default.FactCheck),
        Triple(Screen.SUGGESTIONS_WORKSPACE, "AI Modifier", Icons.Default.AutoFixHigh),
        Triple(Screen.COMPARISON_EDITOR, "Editor", Icons.Default.EditNote),
        Triple(Screen.QUALITY_CHECK, "Quality", Icons.Default.VerifiedUser),
        Triple(Screen.EXPORT_PREVIEW, "Export", Icons.Default.Download)
    )

    val activeIndex = steps.indexOfFirst { it.first == currentScreen }.let { if (it == -1) 0 else it }

    ScrollableTabRow(
        selectedTabIndex = activeIndex,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        contentColor = MaterialTheme.colorScheme.primary,
        divider = {}
    ) {
        steps.forEachIndexed { index, (screen, title, icon) ->
            val isSelected = screen == currentScreen
            val isPast = index <= activeIndex

            Tab(
                selected = isSelected,
                onClick = { onStepClick(screen) },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .testTag("stepper_step_${screen.name.lowercase()}"),
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> EditorialCitron
                                        isPast -> MaterialTheme.colorScheme.primaryContainer
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) EditorialOnCitron else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun EvidenceBadge(status: EvidenceStatus) {
    val (bgColor, textColor, icon) = when (status) {
        EvidenceStatus.SUPPORTED_BY_RESUME -> Triple(
            StatusSupported.copy(alpha = 0.15f),
            StatusSupported,
            Icons.Default.CheckCircle
        )
        EvidenceStatus.SUPPORTED_BY_USER_INFO -> Triple(
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.secondary,
            Icons.Default.Verified
        )
        EvidenceStatus.REQUIRES_CONFIRMATION -> Triple(
            StatusRequiresConfirmation.copy(alpha = 0.15f),
            StatusRequiresConfirmation,
            Icons.Default.HelpOutline
        )
        EvidenceStatus.NOT_SUFFICIENTLY_SUPPORTED -> Triple(
            StatusGap.copy(alpha = 0.15f),
            StatusGap,
            Icons.Default.WarningAmber
        )
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = status.label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
            )
        }
    }
}

@Composable
fun ReviewStatusBadge(status: ReviewStatus) {
    val (bgColor, textColor, text) = when (status) {
        ReviewStatus.PENDING -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, "Pending Review")
        ReviewStatus.ACCEPTED -> Triple(StatusAccepted.copy(alpha = 0.2f), StatusAccepted, "Accepted")
        ReviewStatus.REJECTED -> Triple(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), MaterialTheme.colorScheme.outline, "Rejected")
        ReviewStatus.EDITED -> Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer, "Custom Edited")
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    action: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f, fill = false)
        ) {
            if (icon != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(6.dp)
                            .size(20.dp)
                    )
                }
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }
        if (action != null) {
            action()
        }
    }
}
