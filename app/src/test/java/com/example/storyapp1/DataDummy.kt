package com.example.storyapp1

import com.example.storyapp1.response.Story

object DataDummy {

    fun generateDummyQuoteResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                id = i.toString(),
                name = "author $i",
                description = "quote $i",
                photoUrl = "https://example.com/photo$i.jpg",
                createdAt = "2021-09-29T13:34:08Z",
                lat = 0.0,
                lon = 0.0
            )
            items.add(story)
        }
        return items
    }
}