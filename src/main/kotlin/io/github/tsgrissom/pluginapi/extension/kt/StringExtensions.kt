package io.github.tsgrissom.pluginapi.extension.kt

import io.github.tsgrissom.pluginapi.extension.bukkit.getValidInputAliases
import io.github.tsgrissom.pluginapi.func.NonFormattingChatColorPredicate
import org.bukkit.Bukkit
import org.bukkit.ChatColor

/* MARK: Equality Checks */

/**
 * Checks for equality between two Strings with case-insensitivity.
 * @param other The String to compare against.
 * @return Whether the Strings are equal regardless of character case.
 */
fun String.equalsIc(other: String) : Boolean =
    this.equals(other, ignoreCase=true)

/**
 * Checks for case-insensitive equality between the String and any number of other Strings.
 * @param others Any number of Strings to compare against.
 * @return Whether a match was made regardless of character case.
 */
fun String.equalsIc(vararg others: String) : Boolean =
    others.firstOrNull { this.equalsIc(it) } != null

/**
 * Checks for case-insensitive equality between the String and any number of other Strings encapsulated in a List.
 * @param others The encapsulation of Strings within a List.
 * @return Whether a match was made regardless of character case.
 */
fun String.equalsIc(others: List<String>) : Boolean =
    others.firstOrNull { this.equalsIc(it) } != null

/**
 * Checks for exact equality between one String and any number of other Strings with case-sensitivity.
 * @param others Any number of Strings to compare against.
 * @return Whether exact equality was found.
 */
fun String.equalsAny(vararg others: String) : Boolean =
    others.firstOrNull { this == it } != null

/* MARK: Miscellaneous Checks */

/**
 * Checks if the String is surrounded by the requisite String, optionally ignoring case-sensitivity.
 * @param str The String to check if it is surrounding the String being operated on.
 * @return Whether the String is surrounded by the requisite String.
 */
fun String.startsAndEndsWith(
    str: String,
    ignoreCase: Boolean = false
) : Boolean =
    this.startsWith(str, ignoreCase=ignoreCase) && this.endsWith(str, ignoreCase=ignoreCase)

/**
 * Checks if the String is surrounded by single quotes. Determined by checking if
 * the String starts with a single quote and ends with a single quote.
 * @return Whether the String is surrounded by single quotes.
 */
fun String.isSingleQuoted() : Boolean =
    this.startsAndEndsWith("'")

/**
 * Checks if the String is surrounded by double quotes. Determined by checking if
 * the String starts with a double quote and ends with a double quote.
 * @return Whether the String is surrounded by double quotes.
 */
fun String.isDoubleQuoted() : Boolean =
    this.startsAndEndsWith("\"")

/**
 * Checks if the String is surrounded by either kind of quote (single or double.)
 * To return `true`, either `String#isSingleQuoted` or `String#isDoubleQuoted` must
 * be true. Mixed leading/trailing quote types will return `false`.
 * @return Whether the String is quoted.
 */
fun String.isQuoted() : Boolean =
    (this.isSingleQuoted() || this.isDoubleQuoted())

/**
 * Removes surrounding pairs of matching quote characters from the String. If the String is not quoted, returns the
 * String as-is.
 * @return The String without surrounding quotations.
 */
fun String.dequoted() : String {
    if (!this.isQuoted())
        return this

    var s = this

    if (s.isSingleQuoted()) {
        s = s.removePrefix("'")
        s = s.removeSuffix("'")
    } else if (s.isDoubleQuoted()) {
        s = s.removePrefix("\"")
        s = s.removeSuffix("\"")
    }

    return s
}

// TODO Whole number percentages, decimal percentages
/**
 * Checks if the String is in the format of a percentage input.
 * @return Whether the String is in percentage form.
 */
fun String.isPercentage() : Boolean =
    """^\d+(\.\d+)?%$""".toRegex().matches(this)

/* MARK: String Mutations */

/**
 * Capitalizes a String by only altering the first letter. Alternate method offered
 * by `String#capitalizeAllCaps()`.
 * @return The String capitalized.
 */
fun String.capitalize() : String {
    if (this.isEmpty())
        return this

    var str = this.substring(0, 1).uppercase()

    if (this.length == 1)
        return str

    str += (this.substring(1, this.length)).lowercase()
    return str
}

/**
 * Capitalizes the String where it is expected to already be in all capital letters.
 * Lowercases the entire String and then capitalizes it.
 * @return The all caps String lowercased with only the first letter capitalized.
 */
fun String.capitalizeAllCaps() : String =
    this.lowercase().capitalize()

/**
 * Capitalizes each word of the String where it is expected to be in all capital letters with each word delimited by a
 * specific String called the delimiter.
 * For example: Words of enum names are typically delimited by an underscore, so you can use this method to capitalize
 * each word of enum names by using the default delimiter "_".
 * @param delimiter The String to make each cleave at in the process of turning the String into words.
 * @return The processed String with each word capitalized.
 */
fun String.capitalizeEachWordAllCaps(delimiter: String = "_") : String {
    if (!this.contains(delimiter))
        return this.capitalizeAllCaps()

    val split = this.split(delimiter)
    var build = String()

    for ((i, s) in split.withIndex()) {
        build += s.capitalize()

        if (i != (split.size - 1))
            build += " "
    }

    return build
}

/**
 * Replaces placeholders (map keys surrounded by percent signs) with their corresponding
 * map value to allow the user to access a variety of info at configuration time.
 * @param replacements The Map of replacements to substitute into the String.
 * @return The String with substitutions made.
 */
fun String.replaceMap(replacements: Map<String, String>) : String {
    var str = this
    replacements.entries.forEach { (key, value) ->
        str = str.replace("%${key}", value)
    }
    return str
}

/**
 * Replaces placeholders (map keys surrounded by percent signs) with their corresponding
 * map value to allow the user to access a variety of info at configuration time.
 * @param replacements The Map of replacements to substitute into the String.
 * @return The List of Strings with substitutions made.
 */
fun MutableList<String>.replaceMap(replacements: Map<String, String>) : MutableList<String> {
    for ((i, line) in this.withIndex()) {
        val replaced = line.replaceMap(replacements)
        this[i] = replaced
    }
    return this
}

/* MARK: ChatColor Related */

/**
 * Translates the standard alternate ChatColor code (&) into valid color codes.
 * @return The String with properly translated color codes.
 */
fun String.translateColor() : String =
    ChatColor.translateAlternateColorCodes('&', this)

/**
 * Removes ChatColor codes from the String.
 * @return The String sans ChatColor color codes.
 */
fun String.stripColor() : String =
    ChatColor.stripColor(this)!!

/**
 * Sequentially translates and strips ChatColor codes from the String. Effectively sanitizes the String of possible
 * color codes to avoid exploitation.
 * @return The String sans both valid color codes in addition to untranslated color codes.
 */
fun String.translateAndStripColorCodes() : String =
    this.translateColor().stripColor()

/**
 * Whether the String contains either valid ChatColor color codes or untranslated color codes.
 * This is determined by translating and stripping color codes and comparing the given String to the new String.
 * @return Whether the String contains color codes.
 */
fun String.containsChatColor() : Boolean {
    val tas = this.translateAndStripColorCodes()
    return this != tas
}

/**
 * Translates the standard alternate ChatColor code (&) into valid color codes.
 * @return The `List<String>`, each line with properly translated color codes.
 */
fun List<String>.translateColor() : List<String> {
    val ls = mutableListOf<String>()
    this.forEach { ls.add(it.translateColor()) }
    return ls
}

/**
 * Checks if the String consists of only ChatColors and no other text.
 * Determined by translating alternate codes, stripping the colors, and
 * comparing the resulting String to an empty String.
 * @return Whether the String consists of only ChatColors.
 */
fun String.isOnlyColorCodes() : Boolean {
    val stripped = this.translateAndStripColorCodes().trim()
    return stripped == ""
}

/**
 * Resolves the String to a ChatColor with a few techniques. First, method tries to match the String to an ampersand
 * followed by a valid chat color character. Next, it tries to match the string to a valid chat color character if the
 * String is only one character long. Finally, the method tries to match the String to the valid input aliases of each
 * enumerated ChatColor. This could be the regular spelling ("DARK_GRAY"), spelling without underscores ("DARKGRAY"),
 * or even special cases of alternative spellings ("DARKGREY" or "DARK_GREY", invalid but common mistake.)
 * @return A ChatColor or null.
 */
fun String.resolveChatColor() : ChatColor? {
    val bLog = Bukkit.getLogger()
    fun warnAndNull(str: String) : ChatColor? {
        bLog.warning(str)
        return null
    }

    if (this.length == 2 && this.startsWith("&")) { // TODO Check if color code is contained in the String of valid chars
        val colorCode = this.removePrefix("&")

        return ChatColor.getByChar(colorCode)
            ?: return warnAndNull("Thought \"$this\" was a ampersand + color code but getByChar resolved to null")
    } else if (this.length == 1) {
        return ChatColor.getByChar(this)
            ?: return warnAndNull("Thought \"$this\" was a single-character color code but getByChar resolved to null")
    } else {
        // TODO Something to support hex codes

        for (c in ChatColor.entries.filter { NonFormattingChatColorPredicate().test(it) }) {
            val aliases = c.getValidInputAliases().toList()

            if (this.equalsIc(aliases))
                return c
        }

        return warnAndNull("Could not determine what ChatColor \"$this\" is supposed to be")
    }
}