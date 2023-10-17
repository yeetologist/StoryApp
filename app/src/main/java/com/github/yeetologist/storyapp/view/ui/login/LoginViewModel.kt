package com.github.yeetologist.storyapp.view.ui.login

import androidx.lifecycle.ViewModel
import com.github.yeetologist.storyapp.data.StoryRepository

class LoginViewModel (private val storyRepository: StoryRepository) : ViewModel() {
    fun login(email: String, password: String) =
        storyRepository.postLogin(email, password)
}