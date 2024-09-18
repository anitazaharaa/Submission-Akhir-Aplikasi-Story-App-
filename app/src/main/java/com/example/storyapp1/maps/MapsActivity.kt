package com.example.storyapp1.maps

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.storyapp1.R
import com.example.storyapp1.ViewModelFactory
import com.example.storyapp1.utils.Result
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.storyapp1.databinding.ActivityMapsBinding
import com.example.storyapp1.welcome.WelcomeActivity
import com.google.android.gms.maps.model.LatLngBounds

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        supportActionBar?.hide()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        getMyLocation()
        addManyMarker()
    }

    private val boundsBuilder = LatLngBounds.Builder()
    private fun addManyMarker() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                viewModel.getStoriesWithLocation(user.token).observe(this) { maps ->
                    if (maps != null) {
                        when (maps) {
                            is Result.Loading -> {

                            }

                            is Result.Success -> {
                                maps.data.listStory.forEach { storyMaps ->
                                    Log.d("MapsActivity", "Story Lat: ${storyMaps.lat}, Lon: ${storyMaps.lon}")
                                    val latLng = LatLng(storyMaps.lat, storyMaps.lon)
                                    mMap.addMarker(
                                        MarkerOptions()
                                            .position(latLng)
                                            .title(storyMaps.name)
                                            .snippet(storyMaps.description)
                                    )
                                    boundsBuilder.include(latLng)
                                }

                                val bounds: LatLngBounds = boundsBuilder.build()
                                mMap.animateCamera(
                                    CameraUpdateFactory.newLatLngBounds(
                                        bounds,
                                        resources.displayMetrics.widthPixels,
                                        resources.displayMetrics.heightPixels,
                                        300
                                    )
                                )
                            }

                            is Result.Error -> {

                            }

                            else -> {}
                        }
                    }
                }

            }

        }


    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        }
    }
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )    {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

}