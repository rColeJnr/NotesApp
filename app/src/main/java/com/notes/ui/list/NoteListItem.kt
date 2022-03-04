package com.notes.ui.list

import com.notes.data.NoteDbo
import java.time.LocalDateTime

data class NoteListItem(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime
) {
    fun toNoteDbo(): NoteDbo =
        NoteDbo(
            id = id,
            title = title,
            content = content,
            createdAt = createdAt,
            modifiedAt = modifiedAt
        )
}