package com.github.yeetologist.storyapp.view.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.yeetologist.storyapp.di.Injection
import com.github.yeetologist.storyapp.view.ui.create.CreateViewModel
import com.github.yeetologist.storyapp.view.ui.login.LoginViewModel
import com.github.yeetologist.storyapp.view.ui.main.MainViewModel
import com.github.yeetologist.storyapp.view.ui.maps.MapsViewModel
import com.github.yeetologist.storyapp.view.ui.register.RegisterViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(Injection.provideRepository(context)) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(Injection.provideRepository(context)) as T
            }

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(Injection.provideRepository(context)) as T
            }

            modelClass.isAssignableFrom(CreateViewModel::class.java) -> {
                CreateViewModel(Injection.provideRepository(context)) as T
            }

            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(Injection.provideRepository(context)) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}