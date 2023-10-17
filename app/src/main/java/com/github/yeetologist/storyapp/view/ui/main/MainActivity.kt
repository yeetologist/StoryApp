package com.github.yeetologist.storyapp.view.ui.main

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.yeetologist.storyapp.R
import com.github.yeetologist.storyapp.data.Result
import com.github.yeetologist.storyapp.data.remote.response.ListStoryItem
import com.github.yeetologist.storyapp.data.remote.response.StoryResponse
import com.github.yeetologist.storyapp.databinding.ActivityMainBinding
import com.github.yeetologist.storyapp.util.Preference
import com.github.yeetologist.storyapp.view.ui.ViewModelFactory
import com.github.yeetologist.storyapp.view.ui.create.CreateActivity
import com.github.yeetologist.storyapp.view.ui.detail.DetailActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels{
        ViewModelFactory(applicationContext)
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tokenDataStore")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        setupListUsers()
        setupMenu()
        setupFab()
    }

    private fun setupFab() {
        val preferences = Preference.getInstance(dataStore)
        binding.actionLogout.setOnClickListener {
            CoroutineScope(Main).launch {
                preferences.saveToken("")
            }
            finish()
        }
    }

    private fun setupMenu() {
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add_option_menu -> {
                    val token = intent.getStringExtra(EXTRA_TOKEN)
                    val intent = Intent(this@MainActivity,CreateActivity::class.java)
                    intent.putExtra(CreateActivity.EXTRA_TOKEN, token)
                    startActivity(intent)

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

    private fun processStories(body : StoryResponse) {
        if (body.error) {
            showSnackbar(body.message)
        } else {
            setListStories(body.listStory)
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoading(bool: Boolean) {
        if(bool) {
            binding.vLayer.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else{
            binding.vLayer.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        val layoutManager =
            if (applicationContext.resources.configuration.orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(this, 2)
            } else {
                LinearLayoutManager(this)
            }
        binding.rvListStories.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvListStories.addItemDecoration(itemDecoration)
    }

    private fun setListStories(items: List<ListStoryItem>?) {
        val adapter = MainAdapter()
        adapter.submitList(items)
        binding.rvListStories.adapter = adapter
        adapter.setOnItemClickListener(object : MainAdapter.OnItemClickListener {
            override fun onItemClick(detailResult: ListStoryItem) {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_STORY, detailResult)
                startActivity(intent)
            }
        })
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}