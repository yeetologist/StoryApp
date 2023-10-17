package com.github.yeetologist.storyapp.view.ui.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.github.yeetologist.storyapp.data.Result
import com.github.yeetologist.storyapp.data.remote.response.ListStoryItem
import com.github.yeetologist.storyapp.databinding.ActivityDetailBinding
import com.github.yeetologist.storyapp.view.ui.ViewModelFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupDetail()
    }

    private fun setupDetail() {
        val story = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_STORY, ListStoryItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_STORY)
        }

        with(binding){
            if (story != null) {
                tvDetailTitle.text = story.name
                Glide.with(root)
                    .load(story.photoUrl)
                    .into(ivDetail)
                tvDetailDescription.text = story.description
            }
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}