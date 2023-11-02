package io.github.tsgrissom.pluginapi.data

import io.github.tsgrissom.pluginapi.extension.kt.dequoted
import io.github.tsgrissom.pluginapi.extension.kt.isQuoted

/**
 * Represents the results of a quoted String parsing operations within CommandContext.
 */
data class QuotedStringSearchResult(
    val quotedString: String,
    val startIndex: Int,
    val endIndex: Int
) {

    init {
        if (!quotedString.isQuoted())
            error("Cannot initialize QuotedStringSearchResults for non-quoted String(=${quotedString})")
    }

    /**
     * Fetches the contents inside the quotation marks via the String#dequoted method.
     * @return The String inside the quotation marks.
     */
    override fun toString(): String = this.quotedString.dequoted()

    /**
     * Fetches the character at the first index of the quoted String. This character is guaranteed to be either an
     * apostrophe or a quotation mark by the initializer of the type.
     * @return Either an apostrophe or a quotation mark character.
     */
    fun getQuotationMark() : Char = quotedString[0]

    /**
     * Checks if the unquoted String contains any floating quotation marks. This could be either an apostrophe or a
     * quotation mark, but is guaranteed to not be a pair of them.
     * @return Whether the contents of the quoted String contain a floating quotation mark.
     */
    fun containsFloatingQuotationMarks() : Boolean {
        val toStr = toString()
        return toStr.contains("'") || toStr.contains("\"")
    }
}