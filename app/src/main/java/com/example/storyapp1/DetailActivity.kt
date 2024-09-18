package com.example.storyapp1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.storyapp1.databinding.ActivityDetailBinding
import com.example.storyapp1.utils.Result

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val receiveId = intent.getStringExtra("id_user").toString()
        mainViewModel.getSession().observe(this) { user ->
            viewModel.getDetail(user.token, receiveId)
        }

        viewModel.detailResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {

                }
                is Result.Success -> {
                    binding.apply {
                        userName.text = result.data.name
                        description.text = result.data.description
                    }
                    Glide.with(this).load(result.data.photoUrl).into(binding.avaUser)
                }
                is Result.Error -> {

                }
            }
        }

    }
    companion object {
        const val NAME = "name"
        const val DESC = "description"
        const val URL = "url"
        const val EXTRA_ID = "extra_id"
    }
}