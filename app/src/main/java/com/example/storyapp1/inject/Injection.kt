package com.example.storyapp1.inject

import android.content.Context
import com.example.storyapp1.Prefrence.UserPreference
import com.example.storyapp1.Prefrence.dataStore
import com.example.storyapp1.retrofit.ApiConfig
import com.example.storyapp1.utils.UserRepository

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService, pref)
    }
}