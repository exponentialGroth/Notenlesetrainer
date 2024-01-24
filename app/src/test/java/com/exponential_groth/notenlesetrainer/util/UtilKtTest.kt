package com.exponential_groth.notenlesetrainer.util

import org.junit.Assert.*
import java.time.LocalDate

class UtilKtTest {

    @org.junit.Test
    fun toLocalDate() {
        val date = LocalDate.of(2045, 10, 29)
        val extractedDate = date.hashCode().toLocalDate()
        assertEquals(date.year, extractedDate.year)
        assertEquals(date.monthValue, extractedDate.monthValue)
        assertEquals(date.dayOfMonth, extractedDate.dayOfMonth)
    }
}