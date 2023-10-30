package io.github.tsgrissom.pluginapi.data

import io.github.tsgrissom.pluginapi.extension.dequoted

data class QuotedStringSearchResult(
    val quotedString: String,
    val startIndex: Int,
    val endIndex: Int
) {

    init {
        if (!(quotedString.startsWith("'") && quotedString.endsWith("'")) && !(quotedString.startsWith("\"") && quotedString.endsWith("\"")))
            error("Cannot initialize QuotedStringSearchResults for non-quoted String(=${quotedString})")
    }

    override fun toString(): String = getContents()

    fun getContents() : String {
        return quotedString.dequoted()
    }

    fun getQuotationMark() : Char =
        quotedString[0]

    fun containsFloatingQuotationMarks() : Boolean {
        val contents = getContents()
        return contents.contains("'") || contents.contains("\"")
    }
}