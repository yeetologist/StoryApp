package com.github.yeetologist.storyapp.view.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.yeetologist.storyapp.data.StoryRepository
import com.github.yeetologist.storyapp.data.remote.response.ListStoryItem

class MainViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> =
        storyRepository.getAllStories(token).cachedIn(viewModelScope)
}