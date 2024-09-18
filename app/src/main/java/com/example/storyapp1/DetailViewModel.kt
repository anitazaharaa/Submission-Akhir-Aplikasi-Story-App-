package com.example.storyapp1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp1.response.Story
import com.example.storyapp1.utils.UserRepository
import kotlinx.coroutines.launch
import com.example.storyapp1.utils.Result

class DetailViewModel(private val repository: UserRepository) : ViewModel() {

    private val _detailResult = MutableLiveData<Result<Story>>()
    val detailResult: LiveData<Result<Story>> = _detailResult

    fun getSession(){
        repository.getSession().asLiveData()
    }

    fun getDetail(token: String, id: String) {
        viewModelScope.launch {
            repository.detail(token, id).collect { result ->
                _detailResult.value = result
            }
        }
    }
}