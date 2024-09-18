package com.example.storyapp1.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp1.Prefrence.StoryPagingSource
import com.example.storyapp1.Prefrence.UserModel
import com.example.storyapp1.Prefrence.UserPreference
import com.example.storyapp1.response.Story
import com.example.storyapp1.response.StoryResponse
import com.example.storyapp1.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import com.example.storyapp1.utils.Result

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun login(email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("${e.message}"))
        }
    }

    fun signup(name: String, email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("${e.message}"))
        }
    }

    fun story(token: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStory("Bearer $token")
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("${e.message}"))
        }
    }

    suspend fun detail(token: String, id: String): Flow<Result<Story>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getDetail("Bearer $token", id)
            val story = response.story
            emit(Result.Success(story))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred"))
        }
    }

    fun getUpload(token: String, file: File, description: String) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestFile = file.asRequestBody("text/plain".toMediaType())
        val multiBody = MultipartBody.Part.createFormData("photo",file.name,requestFile)
        try {
            val successResponse = apiService.postStory("Bearer $token",multiBody,requestBody)
            emit(Result.Success(successResponse))
        } catch (e: Exception) {
            emit(Result.Error("${e.message}"))
        }
    }

    fun getUpload(token: String, file: File, description: String, lat: String, lon: String) = liveData {
        emit(Result.Loading)
        val lat = lat.toRequestBody("text/plain".toMediaType())
        val lon = lon.toRequestBody("text/plain".toMediaType())
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestFile = file.asRequestBody("text/plain".toMediaType())
        val multiBody = MultipartBody.Part.createFormData("photo",file.name,requestFile)
        try {
            val successResponse = apiService.postStory("Bearer $token",multiBody,requestBody,lat,lon)
            emit(Result.Success(successResponse))
        } catch (e: Exception) {
            emit(Result.Error("${e.message}"))
        }
    }

    fun getStoriesWithLocation(token: String): LiveData<Result<StoryResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val successResponse = apiService.getStoriesLocation("Bearer $token")
                emit(Result.Success(successResponse))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, StoryResponse::class.java)
                emit(errorResponse.message.let { Result.Error(it) })
            } catch (e: Exception) {
                emit(Result.Error("Error : ${e.message.toString()}"))
            }
        }

    fun getPaging(token: String): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).liveData
    }



    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
    }
}