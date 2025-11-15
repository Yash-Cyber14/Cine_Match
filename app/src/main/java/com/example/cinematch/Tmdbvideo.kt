package com.example.cinematch

data class TrailerResponse(
    val id: Int,
    val results: List<Trailer>
)

data class Trailer(
    val id: String,
    val key: String,     // YouTube key or direct URL
    val name: String,
    val site: String,    // "YouTube", "Vimeo", "External"
    val size: Int?,
    val type: String     // "Trailer", "Teaser", "Clip", etc.
)
