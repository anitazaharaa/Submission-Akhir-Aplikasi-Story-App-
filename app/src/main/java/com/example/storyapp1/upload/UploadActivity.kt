package com.example.storyapp1.upload

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.storyapp1.MainActivity
import com.example.storyapp1.R
import com.example.storyapp1.ViewModelFactory
import com.example.storyapp1.databinding.ActivityUploadBinding
import com.example.storyapp1.login.LoginActivity
import com.example.storyapp1.maps.MapsActivity
import com.example.storyapp1.utils.getImageUri
import com.example.storyapp1.welcome.WelcomeActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.example.storyapp1.utils.Result
import com.example.storyapp1.utils.reduceFileImage
import com.example.storyapp1.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.RequestBody.Companion.asRequestBody


class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private var currentImageUri: Uri? = null
    private val viewModel by viewModels<UploadViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var locationManager: LocationManager
    private var gpsStatus: Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private val requestCameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                binding.uploadButton.setOnClickListener {
                    getMyLastLocation()
                }
            }
            if (!allPermissionsGranted()) {
                requestCameraPermissionLauncher.launch(REQUIRED_PERMISSION)
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        binding.cameraButton.setOnClickListener {
            startCamera()
        }
        binding.galleryButton.setOnClickListener {
            startGallery()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ImageView.setImageURI(it)
        }
    }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }

                else -> {
                    // No location access granted.
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        viewModel.getSession().observe(this) { user ->


            val shareLocation = binding.switch1
            if (shareLocation.isChecked) {
                if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                ) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            uploadStory(
                                user.token,
                                location.latitude.toString(),
                                location.longitude.toString()
                            )
                            Log.d(
                                "koordinat",
                                "getMylastLocation: ${location.latitude}, ${location.longitude}  "
                            )
                        } else {

                            val intents = Intent(this, MapsActivity::class.java)
                            startActivity(intents)

                            gpsStatus =
                                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

                            Toast.makeText(
                                this,
                                "Please open turn on your gps and set your location",
                                Toast.LENGTH_SHORT
                            ).show()
                            if (!gpsStatus) {
                                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {
                                val intent = Intent(this, MapsActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                } else {
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            } else if (!shareLocation.isChecked) {
                uploadStory(user.token)
            }
        }

    }

    private fun uploadStory(token: String) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.description.text.toString()

            viewModel.getUpload(token, imageFile, description).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {

                        }

                        is Result.Success -> {
                            result.data.message.let { showToast(it!!) }

                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }

                        is Result.Error -> {
                            showToast(result.error)

                        }
                    }
                }

            }
        } ?: showToast("Image not found")
    }

    private fun uploadStory(token: String, lat: String, lon: String) {

        currentImageUri?.let { uri ->

            val file = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${file.path}")
            val description = binding.description.text.toString()


            viewModel.getUpload(
                token,
                file,
                description,
                lat,
                lon
            ).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {

                        }

                        is Result.Success -> {
                            result.data.message.let { showToast(it!!) }
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }

                        is Result.Error -> {
                            showToast(result.error)
                            // showLoading(false)
                        }
                    }
                }
            }

        } ?: showToast("Image not found")
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}