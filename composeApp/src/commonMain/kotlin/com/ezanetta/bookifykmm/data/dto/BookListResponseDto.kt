package com.ezanetta.bookifykmm.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookListResponseDto(
    @SerialName("works") val works: List<WorkDto> = emptyList()
)

@Serializable
data class WorkDto(
    @SerialName("title") val title: String? = null,
    @SerialName("authors") val authors: List<AuthorDto>? = null,
    @SerialName("cover_id") val coverId: Long? = null,
    @SerialName("subject") val subject: List<String>? = null
)

@Serializable
data class AuthorDto(
    @SerialName("name") val name: String? = null
)
