package com.example.storyapp1.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp1.response.StoryResponse
import com.example.storyapp1.utils.UserRepository
import kotlinx.coroutines.launch

class MapsViewModel (private val repository: UserRepository) : ViewModel() {

    private val _location = MutableLiveData<Result<StoryResponse>>()
    val location: LiveData<Result<StoryResponse>> = _location

    fun getStoriesWithLocation(token: String) = repository.getStoriesWithLocation(token)

    fun getSession() = repository.getSession().asLiveData()
}