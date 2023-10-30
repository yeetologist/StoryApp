package com.github.yeetologist.storyapp.view.ui.maps

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.maps.model.MapStyleOptions
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

        setMapStyle()
        setupListUsers()
    }

    private fun setupListUsers() {
        intent.getStringExtra(EXTRA_TOKEN)?.let { str ->
            mapsViewModel.getStoriesLocation(str).observe(this) {
                if (it != null) {
                    when (it) {
                        is Result.Loading -> {
                        }

                        is Result.Success -> {
                            processStories(it.data)
                        }

                        is Result.Error -> {
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

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MapsActivity"
        const val EXTRA_TOKEN = "extra_token"
    }

}