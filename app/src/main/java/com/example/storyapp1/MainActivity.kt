package com.example.storyapp1

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp1.adapter.LoadingStateAdapter
import com.example.storyapp1.adapter.StoryAdapter
import com.example.storyapp1.databinding.ActivityMainBinding
import com.example.storyapp1.maps.MapsActivity
import com.example.storyapp1.response.Story
import com.example.storyapp1.upload.UploadActivity
import com.example.storyapp1.welcome.WelcomeActivity
import com.example.storyapp1.utils.Result

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                setupAction(user.token)
            }
        }
//        setupView()
        supportActionBar?.show()

        adapter = StoryAdapter()
        adapter.notifyDataSetChanged()
        adapter.setOnClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Story) {
                Intent(this@MainActivity, DetailActivity::class.java).also {
                    it.putExtra(DetailActivity.NAME, data.name)
                    it.putExtra(DetailActivity.DESC, data.description)
                    it.putExtra(DetailActivity.URL, data.photoUrl)
                    startActivity(it)
                }
            }
        })
        binding.apply {
            rvStory.layoutManager = LinearLayoutManager(this@MainActivity)
            rvStory.setHasFixedSize(true)
            rvStory.adapter = adapter
        }
    }

//    private fun setupView() {
//        @Suppress("DEPRECATION")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.insetsController?.hide(WindowInsets.Type.statusBars())
//        } else {
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//            )
//        }
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.btn_logout -> {
                viewModel.logout()
            }

            R.id.btn_add -> {
                startActivity(Intent(this@MainActivity, UploadActivity::class.java))
            }
            R.id.btn_map -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    private fun setupAction(token: String) {
        viewModel.story(token).observe(this) { user ->
            if (user != null) {
                val adapter = StoryAdapter()
                binding.rvStory.adapter = adapter
                binding.rvStory.layoutManager = LinearLayoutManager(this)

                binding.rvStory.adapter = adapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        adapter.retry()
                    }
                )

                viewModel.story(token).observe(this) { result ->
                    binding.progressBar.visibility = View.GONE
                    adapter.submitData(lifecycle, result)
                }
            }
        }
    }


}