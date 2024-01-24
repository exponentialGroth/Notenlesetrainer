package com.exponential_groth.notenlesetrainer.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exponential_groth.notenlesetrainer.data.Repository
import com.exponential_groth.notenlesetrainer.data.local.HighScore
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(private val repository: Repository): ViewModel() {
    val highScores = MutableLiveData<List<HighScore>>()

    fun loadHighScores() {
        if (highScores.value != null) return
        viewModelScope.launch {
            highScores.value = repository.getHighScores()
        }
    }

    fun updateHighScore(notes: Int, key: Int, level: Int, minTone: Int, maxTone: Int, isWithRhythm: Boolean) {
        viewModelScope.launch {
            val oldHighScore = repository.getHighScore(key, level, minTone, maxTone, isWithRhythm)
            val oldHighScoreValue = oldHighScore?.hsNotesPlayed
            if (oldHighScoreValue == null || notes >= oldHighScoreValue) {
                val newHighScore = HighScore(
                    notes, LocalDate.now().hashCode(), minTone, maxTone, key, level, isWithRhythm
                )
                repository.addHighScore(newHighScore)

                highScores.value = if (highScores.value == null)
                    listOf(newHighScore)
                else if (oldHighScore == null)
                    highScores.value!!.plus(newHighScore)
                else
                    highScores.value!!.minus(oldHighScore).plus(newHighScore)
            }
        }
    }
}