package com.example.storyapp1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.storyapp1.Prefrence.UserModel
import com.example.storyapp1.response.Story
import com.example.storyapp1.utils.UserRepository
import kotlinx.coroutines.launch

class MainViewModel (private val repository: UserRepository) : ViewModel() {
    val listUser = MutableLiveData<ArrayList<Story>>()


    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun story(token: String) :LiveData<PagingData<Story>> = repository.getPaging(token)

}