package ua.acclorite.book_story.data.parser.html

import org.jsoup.Jsoup
import ua.acclorite.book_story.R
import ua.acclorite.book_story.data.parser.FileParser
import ua.acclorite.book_story.domain.model.Book
import ua.acclorite.book_story.domain.model.Category
import ua.acclorite.book_story.domain.util.CoverImage
import ua.acclorite.book_story.domain.util.UIText
import java.io.File
import javax.inject.Inject

class HtmlFileParser @Inject constructor() : FileParser {

    override suspend fun parse(file: File): Pair<Book, CoverImage?>? {
        if (!file.name.endsWith(".html", true) || !file.exists()) {
            return null
        }

        try {
            val document = Jsoup.parse(file)

            val title = document.select("head > title").text().trim().run {
                ifBlank {
                    file.nameWithoutExtension.trim()
                }
            }

            return Book(
                title = title,
                author = UIText.StringResource(R.string.unknown_author),
                description = null,
                textPath = "",
                scrollIndex = 0,
                scrollOffset = 0,
                progress = 0f,
                filePath = file.path,
                lastOpened = null,
                category = Category.entries[0],
                coverImage = null
            ) to null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}