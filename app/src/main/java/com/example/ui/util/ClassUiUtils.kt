package com.example.ui.util

import androidx.compose.ui.graphics.Color
import java.text.NumberFormat
import java.util.Locale

object ClassUiUtils {

    fun formatRupiah(amount: Long): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0
        return format.format(amount)
    }

    val avatarPalette = listOf(
        Color(0xFF3B82F6), // Blue
        Color(0xFF10B981), // Emerald
        Color(0xFF8B5CF6), // Purple
        Color(0xFFF59E0B), // Amber
        Color(0xFFEC4899), // Pink
        Color(0xFF06B6D4), // Cyan
        Color(0xFFEF4444), // Red
        Color(0xFF6366F1), // Indigo
        Color(0xFF14B8A6)  // Teal
    )

    fun getAvatarColor(index: Int): Color {
        return avatarPalette[index.coerceAtLeast(0) % avatarPalette.size]
    }

    fun getInitials(name: String): String {
        val parts = name.trim().split("\\s+".toRegex())
        return when {
            parts.isEmpty() -> "?"
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
        }
    }
}
