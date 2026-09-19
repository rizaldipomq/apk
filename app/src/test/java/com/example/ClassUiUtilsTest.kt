package com.example

import com.example.ui.util.ClassUiUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClassUiUtilsTest {

    @Test
    fun testFormatRupiah() {
        val formatted = ClassUiUtils.formatRupiah(1430000L)
        assertTrue(formatted.contains("1.430.000") || formatted.contains("Rp"))
    }

    @Test
    fun testGetInitials() {
        assertEquals("MR", ClassUiUtils.getInitials("M. Raditya Pratama"))
        assertEquals("AB", ClassUiUtils.getInitials("Ananda Bagus"))
        assertEquals("AI", ClassUiUtils.getInitials("Aisyah"))
    }
}
