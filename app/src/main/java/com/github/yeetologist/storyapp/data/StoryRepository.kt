package com.github.yeetologist.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.github.yeetologist.storyapp.data.remote.response.ListStoryItem
import com.github.yeetologist.storyapp.data.remote.response.LoginResponse
import com.github.yeetologist.storyapp.data.remote.response.RegisterResponse
import com.github.yeetologist.storyapp.data.remote.response.StoryResponse
import com.github.yeetologist.storyapp.data.remote.response.UploadResponse
import com.github.yeetologist.storyapp.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(private val apiService: ApiService) {
    fun postRegister(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e("RegisterViewModel", "postRegister: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun postLogin(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e("LoginViewModel", "postLogin: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }


    fun getAllStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = true,

                ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).liveData
    }

    fun getAllStoriesLocation(token: String): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesWithLocation("Bearer $token")
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e("MapsViewModel", "getAllStoriesLocation: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun postStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double?,
        lon: Double?,
        token: String
    ): LiveData<Result<UploadResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.postStory(file, description, lat, lon, "Bearer $token")
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e("CreateStoryViewModel", "postStory: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }
}