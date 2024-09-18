package com.example.storyapp1.signup

import androidx.lifecycle.ViewModel
import com.example.storyapp1.utils.UserRepository

class RegistViewModel (private val repository: UserRepository): ViewModel() {
    fun register(name: String, email: String, password: String) = repository.signup(name, email, password)
}