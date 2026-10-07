package dev.fbaldhagen.tectonic.designsystem.theme

import androidx.compose.ui.graphics.Color

// Brand seed (softened to match the pastel region palette)
internal val BrandViolet = Color(0xFF6B5BA6)

// === Light scheme (pastel-aligned) ===
internal val LightPrimary = BrandViolet
internal val LightOnPrimary = Color(0xFFFFFFFF)
internal val LightPrimaryContainer = Color(0xFFE8E0F8)
internal val LightOnPrimaryContainer = Color(0xFF2A1F5A)

internal val LightSecondary = Color(0xFF6B657A)
internal val LightOnSecondary = Color(0xFFFFFFFF)
internal val LightSecondaryContainer = Color(0xFFEDE8F5)
internal val LightOnSecondaryContainer = Color(0xFF2B2838)

internal val LightTertiary = Color(0xFF8B6FCF)
internal val LightOnTertiary = Color(0xFFFFFFFF)
internal val LightTertiaryContainer = Color(0xFFEDE4F8)
internal val LightOnTertiaryContainer = Color(0xFF2F1F5E)

internal val LightBackground = Color(0xFFFAF8FC)
internal val LightOnBackground = Color(0xFF1E1B2E)
internal val LightSurface = Color(0xFFFFFFFF)
internal val LightOnSurface = Color(0xFF1E1B2E)
internal val LightSurfaceVariant = Color(0xFFF0ECF8)
internal val LightOnSurfaceVariant = Color(0xFF6E6A80)
internal val LightOutline = Color(0xFFE0DCEE)
internal val LightOutlineVariant = Color(0xFFEDE9F5)

internal val LightError = Color(0xFFBA1A1A)
internal val LightOnError = Color(0xFFFFFFFF)
internal val LightErrorContainer = Color(0xFFFFDAD6)
internal val LightOnErrorContainer = Color(0xFF410002)

// === Dark scheme (kept very close to original, minor pastel tweak) ===
internal val DarkPrimary = Color(0xFF9B86F5)
internal val DarkOnPrimary = Color(0xFFFFFFFF)
internal val DarkPrimaryContainer = Color(0xFF2E2555)
internal val DarkOnPrimaryContainer = Color(0xFFE4DBFB)

internal val DarkSecondary = Color(0xFFB8B3D6)
internal val DarkOnSecondary = Color(0xFF1E1B2E)
internal val DarkSecondaryContainer = Color(0xFF2A2640)
internal val DarkOnSecondaryContainer = Color(0xFFE7E4F5)

internal val DarkTertiary = Color(0xFFC7A8FF)
internal val DarkOnTertiary = Color(0xFF3A1B7A)
internal val DarkTertiaryContainer = Color(0xFF4B2E96)
internal val DarkOnTertiaryContainer = Color(0xFFF1E6FE)

internal val DarkBackground = Color(0xFF120F1C)
internal val DarkOnBackground = Color(0xFFEDEBF5)
internal val DarkSurface = Color(0xFF1C1830)
internal val DarkOnSurface = Color(0xFFEDEBF5)
internal val DarkSurfaceVariant = Color(0xFF262140)
internal val DarkOnSurfaceVariant = Color(0xFFA8A3C0)
internal val DarkOutline = Color(0xFF3A3555)
internal val DarkOutlineVariant = Color(0xFF2A2540)

internal val DarkError = Color(0xFFFFB4AB)
internal val DarkOnError = Color(0xFF690005)
internal val DarkErrorContainer = Color(0xFF93000A)
internal val DarkOnErrorContainer = Color(0xFFFFDAD6)

// Region hues (light mode - opaque)
internal val RegionHue0 = Color(0xFFD4E6F1)   // sky
internal val RegionHue1 = Color(0xFFD5F5E3)   // emerald
internal val RegionHue2 = Color(0xFFFCF3CF)   // amber
internal val RegionHue3 = Color(0xFFFADBD8)   // rose
internal val RegionHue4 = Color(0xFFE8DAEF)   // violet
internal val RegionHue5 = Color(0xFFD1F2EB)   // cyan
internal val RegionHue6 = Color(0xFFFCEDD9)   // orange
internal val RegionHue7 = Color(0xFFE8F8D8)   // lime
internal val RegionHue8 = Color(0xFFFADCE8)   // pink
internal val RegionHue9 = Color(0xFFD6E0F8)   // indigo

// Region hues (dark mode - 50%)
internal val RegionHueDark0 = Color(0x800C4A6E)  // sky-900/50
internal val RegionHueDark1 = Color(0x80064E3B)  // emerald-900/50
internal val RegionHueDark2 = Color(0x8078350F)  // amber-900/50
internal val RegionHueDark3 = Color(0x80881337)  // rose-900/50
internal val RegionHueDark4 = Color(0x804C1D95)  // violet-900/50
internal val RegionHueDark5 = Color(0x80164E63)  // cyan-900/50
internal val RegionHueDark6 = Color(0x807C2D12)  // orange-900/50
internal val RegionHueDark7 = Color(0x80365314)  // lime-900/50
internal val RegionHueDark8 = Color(0x80831843)  // pink-900/50
internal val RegionHueDark9 = Color(0x80312E81)  // indigo-900/50

/**
 * Extra semantic colors that don't map cleanly onto Material 3's ColorScheme roles,
 * e.g. the colors for the tectonic regions.
 */
data class TecExtendedColors(
    val regionHues: List<Color> = emptyList()
)

internal val LightExtendedColors = TecExtendedColors(
    regionHues = listOf(
        RegionHue0, RegionHue1, RegionHue2, RegionHue3, RegionHue4,
        RegionHue5, RegionHue6, RegionHue7, RegionHue8, RegionHue9
    )
)

internal val DarkExtendedColors = TecExtendedColors(
    regionHues = listOf(
        RegionHueDark0, RegionHueDark1, RegionHueDark2, RegionHueDark3, RegionHueDark4,
        RegionHueDark5, RegionHueDark6, RegionHueDark7, RegionHueDark8, RegionHueDark9
    )
)