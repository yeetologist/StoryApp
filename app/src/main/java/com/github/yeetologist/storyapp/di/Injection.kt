package com.github.yeetologist.storyapp.di

import android.content.Context
import com.github.yeetologist.storyapp.data.StoryRepository
import com.github.yeetologist.storyapp.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
//        val pref = UserPreference.getInstance(context.dataStore)
//        val user = runBlocking { pref.getUser().first() }
        val apiService = ApiConfig.getApiService(context)
        return StoryRepository(apiService)
    }
}
