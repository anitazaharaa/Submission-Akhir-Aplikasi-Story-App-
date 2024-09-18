package com.example.storyapp1.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp1.Prefrence.UserModel
import com.example.storyapp1.utils.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel (private val repository: UserRepository) : ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun login(email: String, password: String) = repository.login(email, password)
}