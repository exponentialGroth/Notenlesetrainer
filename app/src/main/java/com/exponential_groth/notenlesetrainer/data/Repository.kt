package com.exponential_groth.notenlesetrainer.data

import com.exponential_groth.notenlesetrainer.data.local.HighScore
import com.exponential_groth.notenlesetrainer.data.local.HighScoreDao

class Repository private constructor(private val hsDao: HighScoreDao): IRepository {

    override suspend fun addHighScore(highScore: HighScore) = hsDao.addHighScore(highScore)
    override suspend fun getHighScore(key: Int, level: Int, minTone: Int, maxTone: Int, isWithRhythm: Boolean) = hsDao.getHS(key, level, minTone, maxTone, isWithRhythm)
    override suspend fun getHighScores() = hsDao.getAllHighScores()

    companion object {
        @Volatile private var instance: Repository? = null

        fun getInstance(hsDao: HighScoreDao) =
            instance ?: synchronized(this) {
                instance ?: Repository(hsDao).also { instance = it }
            }

    }
}