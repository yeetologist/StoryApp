package com.github.yeetologist.storyapp.view.ui.create

import androidx.lifecycle.ViewModel
import com.github.yeetologist.storyapp.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CreateViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun postStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double?,
        lon: Double?,
        token: String
    ) =
        storyRepository.postStory(file, description, lat, lon, token)
}