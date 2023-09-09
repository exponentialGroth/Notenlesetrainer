package com.exponential_groth.notenlesetrainer.data

import com.exponential_groth.notenlesetrainer.data.local.HighScore

interface IRepository {
    suspend fun addHighScore(highScore: HighScore): Long
    suspend fun getHighScore(key: Int, level: Int, minTone: Int, maxTone: Int, isWithRhythm: Boolean): HighScore?
    suspend fun getHighScores(): List<HighScore>
}