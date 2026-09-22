package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Editorial Chalkboard & Desk Palette (Dark)
val EditorialChalkboard = Color(0xFF0F1512)
val EditorialChalkboardSurface = Color(0xFF17201B)
val EditorialChalkboardElevated = Color(0xFF1F2B24)
val EditorialChalkboardBorder = Color(0xFF283830)

// Editorial Paper & Studio Palette (Light)
val EditorialPaperCream = Color(0xFFFBFBF9)
val EditorialPaperWhite = Color(0xFFFFFFFF)
val EditorialPaperWarm = Color(0xFFF3F5F3)
val EditorialPaperBorder = Color(0xFFE2E7E2)
val EditorialInk = Color(0xFF121B16)
val EditorialMutedInk = Color(0xFF506157)

// Editorial Accent Colors
val EditorialCitron = Color(0xFFD6F037) // Luminous high-contrast lime
val EditorialCitronHover = Color(0xFFE5FA50)
val EditorialOnCitron = Color(0xFF101613)

val EditorialStickyYellow = Color(0xFFFEF388) // Canary yellow sticky note
val EditorialStickyYellowBorder = Color(0xFFFACC15)
val EditorialStickyText = Color(0xFF322F1D)
val EditorialPinRed = Color(0xFFDC2626)

val EditorialMarkerHighlight = Color(0xFFE8FB66)

// Primary Slate tones
val BrandPrimaryLight = Color(0xFF183C2B)
val BrandOnPrimaryLight = Color(0xFFFFFFFF)
val BrandPrimaryContainerLight = Color(0xFFE3F5E7)
val BrandOnPrimaryContainerLight = Color(0xFF0D281C)

val BrandPrimaryDark = Color(0xFFD6F037)
val BrandOnPrimaryDark = Color(0xFF101613)
val BrandPrimaryContainerDark = Color(0xFF24361E)
val BrandOnPrimaryContainerDark = Color(0xFFE6FA88)

// Secondary Warm Forest tones
val BrandSecondaryLight = Color(0xFF255B40)
val BrandOnSecondaryLight = Color(0xFFFFFFFF)
val BrandSecondaryContainerLight = Color(0xFFD6EFE1)
val BrandOnSecondaryContainerLight = Color(0xFF082618)

val BrandSecondaryDark = Color(0xFF72D49E)
val BrandOnSecondaryDark = Color(0xFF042918)
val BrandSecondaryContainerDark = Color(0xFF16442E)
val BrandOnSecondaryContainerDark = Color(0xFFD9F6E5)

// Tertiary Amber/Warm Ochre tones
val BrandTertiaryLight = Color(0xFF8C430E)
val BrandOnTertiaryLight = Color(0xFFFFFFFF)
val BrandTertiaryContainerLight = Color(0xFFFEF3C7)
val BrandOnTertiaryContainerLight = Color(0xFF451A03)

val BrandTertiaryDark = Color(0xFFFDE68A)
val BrandOnTertiaryDark = Color(0xFF451A03)
val BrandTertiaryContainerDark = Color(0xFF78350F)
val BrandOnTertiaryContainerDark = Color(0xFFFEF3C7)

// Backgrounds & Surfaces
val BackgroundLight = Color(0xFFFAF9F6)
val OnBackgroundLight = EditorialInk
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = EditorialInk
val SurfaceVariantLight = Color(0xFFF1F4F1)
val OnSurfaceVariantLight = EditorialMutedInk
val OutlineLight = EditorialPaperBorder

val BackgroundDark = EditorialChalkboard
val OnBackgroundDark = Color(0xFFF2F6F3)
val SurfaceDark = EditorialChalkboardSurface
val OnSurfaceDark = Color(0xFFEAF0EC)
val SurfaceVariantDark = EditorialChalkboardElevated
val OnSurfaceVariantDark = Color(0xFFA1B3A7)
val OutlineDark = EditorialChalkboardBorder

// Domain-specific functional colors
val StatusSupported = Color(0xFF10B981) // Emerald
val StatusRequiresConfirmation = Color(0xFFF59E0B) // Amber
val StatusGap = Color(0xFFEF4444) // Coral Red
val StatusSuggestedHighlight = Color(0xFFD6F037) // Luminous Citron
val StatusAccepted = Color(0xFF059669)
val StatusRejected = Color(0xFF94A3B8)

val DiffRemovedBgLight = Color(0xFFFEE2E2)
val DiffRemovedTextLight = Color(0xFF991B1B)
val DiffAddedBgLight = Color(0xFFECFCCE)
val DiffAddedTextLight = Color(0xFF1D5114)

val DiffRemovedBgDark = Color(0xFF3E1A1D)
val DiffRemovedTextDark = Color(0xFFFCA5A5)
val DiffAddedBgDark = Color(0xFF18361C)
val DiffAddedTextDark = Color(0xFF86EFAC)

// Dynamic Theme-Aware Diff Colors
val DiffRemovedBg: Color
    @Composable get() = if (MaterialTheme.colorScheme.background == BackgroundDark) DiffRemovedBgDark else DiffRemovedBgLight

val DiffRemovedText: Color
    @Composable get() = if (MaterialTheme.colorScheme.background == BackgroundDark) DiffRemovedTextDark else DiffRemovedTextLight

val DiffAddedBg: Color
    @Composable get() = if (MaterialTheme.colorScheme.background == BackgroundDark) DiffAddedBgDark else DiffAddedBgLight

val DiffAddedText: Color
    @Composable get() = if (MaterialTheme.colorScheme.background == BackgroundDark) DiffAddedTextDark else DiffAddedTextLight


