package com.bilocan.lingo

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LingoViewModelFactory(
    private val application: Application,
    private val letterCount: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LingoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LingoViewModel(application, letterCount) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 