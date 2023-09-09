package com.exponential_groth.notenlesetrainer.util

import android.content.Context
import com.exponential_groth.notenlesetrainer.data.Repository
import com.exponential_groth.notenlesetrainer.data.local.Database

object Injector {
    private fun getRepo(context: Context): Repository {
        val db = Database.getInstance(context)
        return Repository.getInstance(db.getHighScoreDao())
    }

    fun provideMainViewModelFactory(context: Context) = MainViewModelFactory(getRepo(context))
}