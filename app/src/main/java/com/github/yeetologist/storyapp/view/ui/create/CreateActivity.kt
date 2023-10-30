package com.github.yeetologist.storyapp.view.ui.create

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.github.yeetologist.storyapp.R
import com.github.yeetologist.storyapp.data.Result
import com.github.yeetologist.storyapp.data.remote.response.UploadResponse
import com.github.yeetologist.storyapp.databinding.ActivityCreateBinding
import com.github.yeetologist.storyapp.util.uriToFile
import com.github.yeetologist.storyapp.view.ui.ViewModelFactory
import com.github.yeetologist.storyapp.view.ui.create.CameraActivity.Companion.CAMERAX_RESULT
import com.github.yeetologist.storyapp.view.ui.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class CreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateBinding
    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    private val createViewModel: CreateViewModel by viewModels {
        ViewModelFactory(applicationContext)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showSnackbar(getString(R.string.permission_granted))
            } else {
                showSnackbar(getString(R.string.permission_denied))
            }
        }


    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!checkPermission(Manifest.permission.CAMERA)) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        getMyLastLocation()

        with(binding) {
            btnGallery.setOnClickListener { startGallery() }
            btnCamera.setOnClickListener { startCameraX() }
            buttonAdd.setOnClickListener { uploadImage() }
        }
    }

    private val requestPermissionMapLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                else -> {
                    showSnackbar(getString(R.string.permission_denied))
                }
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

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivUpload.setImageURI(it)
        }
    }


    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this)
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.edAddDescription.text.toString()
            showLoading(true)
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            val token = intent.getStringExtra(EXTRA_TOKEN)
            var lat = currentLocation?.latitude
            var lon = currentLocation?.longitude

            if (!binding.cbLocation.isChecked) {
                lat = null
                lon = null
            }

            if (token != null) {
                createViewModel.postStory(multipartBody, requestBody, lat, lon, token)
                    .observe(this) {
                        when (it) {
                            is Result.Loading -> {
                                showLoading(true)
                            }

                            is Result.Success -> {
                                showLoading(false)
                                processStory(it.data)
                            }

                            is Result.Error -> {
                                showLoading(false)
                                showSnackbar(it.error)
                            }
                        }
                    }
            }
        } ?: showSnackbar(getString(R.string.empty_image_warning))
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                } else {
                    showSnackbar(getString(R.string.location_not_found))
                }
            }
        } else {
            requestPermissionMapLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun processStory(body: UploadResponse) {
        showSnackbar(body.message)

        val token = intent.getStringExtra(EXTRA_TOKEN)
        val intent = Intent(this@CreateActivity, MainActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_TOKEN, token)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading(bool: Boolean) {
        binding.vLayer.visibility = if (bool) View.VISIBLE else View.GONE
        binding.progressBar.visibility = if (bool) View.VISIBLE else View.GONE
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}