package com.github.yeetologist.storyapp.view.ui.register

import androidx.lifecycle.ViewModel
import com.github.yeetologist.storyapp.data.StoryRepository

class RegisterViewModel (private val storyRepository: StoryRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) =
        storyRepository.postRegister(name, email, password)
}