package com.exponential_groth.notenlesetrainer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HighScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addHighScore(highScore: HighScore): Long

    @Query("SELECT * FROM HighScore WHERE hsKey=:key AND hsLevel=:level AND hsMinTone=:minTone AND hsMaxTone=:maxTone AND hsIsWithRhythm=:isWithRhythm LIMIT 1")
    suspend fun getHS(key: Int, level: Int, minTone: Int, maxTone: Int, isWithRhythm: Boolean): HighScore?

    @Query("SELECT * FROM HighScore")
    suspend fun getAllHighScores(): List<HighScore>
}