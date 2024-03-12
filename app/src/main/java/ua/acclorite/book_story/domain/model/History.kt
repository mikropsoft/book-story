package ua.acclorite.book_story.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class History(
    val id: Int?,
    val bookId: Int,
    val time: Long
)