package com.github.yeetologist.storyapp.util

import com.github.yeetologist.storyapp.data.remote.response.ListStoryItem
import com.github.yeetologist.storyapp.data.remote.response.StoryResponse

object DataDummy {
    fun generateDummyStoriesResponse(): StoryResponse {
        val listStory = ArrayList<ListStoryItem>()
        for (i in 1..10) {
            val story = ListStoryItem(
                createdAt = "2023-10-30T13:10:00Z",
                description = "description-$i",
                id = "story-$i",
                lat = i.toDouble() + 0.0264194,
                lon = i.toDouble() + 109.3188503,
                name = "name-$i",
                photoUrl = "https://static.wikia.nocookie.net/darksouls/images/a/a9/Chosen_undead.png/revision/latest/scale-to-width-down/250?cb=20180122030242"
            )
            listStory.add(story)
        }

        return StoryResponse(
            error = false,
            message = "Stories fetched successfully",
            listStory = listStory
        )
    }
}