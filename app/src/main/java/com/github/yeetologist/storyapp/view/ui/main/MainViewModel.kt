package com.github.yeetologist.storyapp.view.ui.main

import androidx.lifecycle.ViewModel
import com.github.yeetologist.storyapp.data.StoryRepository

class MainViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStories(token: String) = storyRepository.getAllStories(token)
}