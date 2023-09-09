package com.exponential_groth.notenlesetrainer.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.exponential_groth.notenlesetrainer.data.Repository
import com.exponential_groth.notenlesetrainer.home.MainViewModel

class MainViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}