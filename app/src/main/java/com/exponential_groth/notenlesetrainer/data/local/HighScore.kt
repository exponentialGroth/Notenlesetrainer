package com.exponential_groth.notenlesetrainer.data.local

import androidx.room.Entity

@Entity(primaryKeys = ["hsKey", "hsLevel", "hsMinTone", "hsMaxTone", "hsIsWithRhythm"])
data class HighScore(
    val hsNotesPlayed: Int,
    val hsDate: Int,
    val hsMinTone: Int,
    val hsMaxTone: Int,
    val hsKey: Int, // ..., -1=F, 0=C, 1=G, ...
    val hsLevel: Int,
    val hsIsWithRhythm: Boolean,
)