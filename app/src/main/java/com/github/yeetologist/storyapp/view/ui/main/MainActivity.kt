package com.github.yeetologist.storyapp.view.ui.main

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.yeetologist.storyapp.R
import com.github.yeetologist.storyapp.data.remote.response.ListStoryItem
import com.github.yeetologist.storyapp.databinding.ActivityMainBinding
import com.github.yeetologist.storyapp.util.Preference
import com.github.yeetologist.storyapp.view.ui.ViewModelFactory
import com.github.yeetologist.storyapp.view.ui.create.CreateActivity
import com.github.yeetologist.storyapp.view.ui.detail.DetailActivity
import com.github.yeetologist.storyapp.view.ui.maps.MapsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory(applicationContext)
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tokenDataStore")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListUsers()
        setupRecyclerView()
        setupMenu()
        setupFab()
    }

    private fun setupFab() {
        binding.actionAdd.setOnClickListener {
            val token = intent.getStringExtra(EXTRA_TOKEN)
            val intent = Intent(this@MainActivity, CreateActivity::class.java)
            intent.putExtra(CreateActivity.EXTRA_TOKEN, token)
            startActivity(intent)
        }
    }

    private fun setupMenu() {
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.map_option_menu -> {
                    val mapIntent = Intent(this@MainActivity, MapsActivity::class.java)
                    intent.getStringExtra(EXTRA_TOKEN)?.let { str ->
                        mapIntent.putExtra(MapsActivity.EXTRA_TOKEN, str)
                    }
                    startActivity(mapIntent)
                    true
                }

                R.id.action_logout -> {
                    val preferences = Preference.getInstance(dataStore)
                    CoroutineScope(Main).launch {
                        preferences.saveToken("")
                    }
                    finish()
                    true
                }

                R.id.settings_option_menu -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }

                else -> super.onOptionsItemSelected(it)
            }
        }
    }

    private fun setupListUsers() {
        intent.getStringExtra(EXTRA_TOKEN)?.let { str ->
            mainViewModel.getStories(str).observe(this) {
                val adapter = MainAdapter()
                binding.rvListStories.adapter = adapter
                    .withLoadStateFooter(
                        footer = LoadingStateAdapter {
                            adapter.retry()
                        }
                    )
                adapter.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                adapter.submitData(lifecycle, it)
                adapter.setOnItemClickListener(object : MainAdapter.OnItemClickListener {
                    override fun onItemClick(detailResult: ListStoryItem) {
                        val intent = Intent(this@MainActivity, DetailActivity::class.java)
                        intent.putExtra(DetailActivity.EXTRA_STORY, detailResult)
                        startActivity(intent)
                    }
                })
            }
        }
    }


    private fun setupRecyclerView() {
        val layoutManager =
            if (applicationContext.resources.configuration.orientation
                == Configuration.ORIENTATION_LANDSCAPE
            ) {
                GridLayoutManager(this, 2)
            } else {
                LinearLayoutManager(this)
            }
        binding.rvListStories.layoutManager = layoutManager
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}