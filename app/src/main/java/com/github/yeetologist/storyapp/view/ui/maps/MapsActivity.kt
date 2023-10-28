package com.github.yeetologist.storyapp.view.ui.maps

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.yeetologist.storyapp.R
import com.github.yeetologist.storyapp.data.Result
import com.github.yeetologist.storyapp.data.remote.response.ListStoryItem
import com.github.yeetologist.storyapp.data.remote.response.StoryResponse
import com.github.yeetologist.storyapp.databinding.ActivityMapsBinding
import com.github.yeetologist.storyapp.view.ui.ViewModelFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val mapsViewModel: MapsViewModel by viewModels {
        ViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setupListUsers()
    }

    private fun setupListUsers() {
        intent.getStringExtra(EXTRA_TOKEN)?.let { str ->
            mapsViewModel.getStoriesLocation(str).observe(this) {
                if (it != null) {
                    when (it) {
                        is Result.Loading -> {
                            showLoading(true)
                        }

                        is Result.Success -> {
                            showLoading(false)
                            processStories(it.data)
                        }

                        is Result.Error -> {
                            showLoading(false)
                            showSnackbar(it.error)
                        }
                    }
                }
            }
        }
    }

    private fun processStories(body: StoryResponse) {
        if (body.error) {
            showSnackbar(body.message)
        } else {
            setListStories(body.listStory)
        }
    }

    private fun setListStories(items: List<ListStoryItem>) {
        items.forEach { data ->
            val latLng = data.lat?.let { data.lon?.let { it1 -> LatLng(it, it1) } }
            latLng?.let {
                MarkerOptions()
                    .position(it)
                    .title(data.name)
                    .snippet(data.description)
            }?.let {
                mMap.addMarker(
                    it
                )
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoading(bool: Boolean) {
//        binding.vLayer.visibility = if (bool) View.VISIBLE else View.GONE
//        binding.progressBar.visibility = if (bool) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }

}