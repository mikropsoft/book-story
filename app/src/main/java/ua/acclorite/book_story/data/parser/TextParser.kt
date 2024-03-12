package ua.acclorite.book_story.data.parser

import ua.acclorite.book_story.domain.model.StringWithId
import ua.acclorite.book_story.util.Resource
import java.io.File

interface TextParser {
    suspend fun parse(file: File): Resource<List<StringWithId>>
}