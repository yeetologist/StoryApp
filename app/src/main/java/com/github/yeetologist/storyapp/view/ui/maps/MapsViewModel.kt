package com.github.yeetologist.storyapp.view.ui.maps

import androidx.lifecycle.ViewModel
import com.github.yeetologist.storyapp.data.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStoriesLocation(token: String) = storyRepository.getAllStoriesLocation(token)
}
