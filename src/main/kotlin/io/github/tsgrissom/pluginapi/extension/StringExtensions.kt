package io.github.tsgrissom.pluginapi.extension

import org.bukkit.ChatColor

/* Equality Checks */

/**
 * Checks for equality between two Strings with case-insensitivity
 * @param s The String to compare against
 * @return Whether the Strings are equal regardless of character case
 */
fun String.equalsIc(s: String) : Boolean =
    this.equals(s, ignoreCase=true)

/**
 * Checks for case-insensitive equality between the String and any number of other Strings
 * @param matches Any number of Strings to compare against
 * @return Whether a match was made regardless of character case
 */
fun String.equalsIc(vararg matches: String) : Boolean =
    matches.firstOrNull { this.equalsIc(it) } != null

/**
 * Checks for case-insensitive equality between the String and any number of other Strings encapsulated in a List
 * @param matches The encapsulation of Strings within a List
 * @return Whether a match was made regardless of character case
 */
fun String.equalsIc(matches: List<String>) : Boolean =
    matches.firstOrNull { this.equalsIc(it) } != null

/**
 * Checks for exact equality between one String and any number of other Strings with case-sensitivity
 * @param matches Any number of Strings to compare against
 * @return Whether exact equality was found
 */
fun String.equalsAny(vararg matches: String) : Boolean =
    matches.firstOrNull { this == it } != null

/* Miscellaneous Checks */

/**
 * Checks if the String is surrounded by single quotes. Determined by checking if
 * the String starts with a single quote and ends with a single quote.
 * @return Whether the String is surrounded by single quotes
 */
fun String.isSingleQuoted() : Boolean =
    this.startsWith("'") && this.endsWith("'")

/**
 * Checks if the String is surrounded by double quotes. Determined by checking if
 * the String starts with a double quote and ends with a double quote.
 * @return Whether the String is surrounded by double quotes
 */
fun String.isDoubleQuoted() : Boolean =
    this.startsWith("\"") && this.endsWith("\"")

/**
 * Checks if the String is surrounded by either kind of quote (single or double.)
 * To return `true`, either `String#isSingleQuoted` or `String#isDoubleQuoted` must
 * be true. Mixed-quote types will return `false`.
 * @return Whether the String is quoted
 */
fun String.isQuoted() : Boolean =
    (this.isSingleQuoted() || this.isDoubleQuoted())

fun String.dequoted() : String {
    if (!this.isQuoted())
        error("Cannot remove quotes from non-quoted String=${this}. Check with String#isQuoted")
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

fun String.isPercentage() : Boolean {
    val percentagePattern = """^\d+(\.\d+)?%$""".toRegex()
    return percentagePattern.matches(this)
}

/* String Mutations */

/**
 * Capitalizes a String by only altering the first letter. Alternate method offered
 * by `String#capitalizeAllCaps()`.
 * @return The String capitalized
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
 * @return The all caps String lowercased with only the first letter capitalized
 */
fun String.capitalizeAllCaps() : String = this.lowercase().capitalize()

fun String.capitalizeEachWordAllCaps() : String {
    if (!this.contains("_"))
        return this.capitalizeAllCaps()

    val split = this.split("_")
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
 * @param replacements The Map of replacements to substitute into the String
 * @return The String with substitutions made
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
 * @param replacements The Map of replacements to substitute into the String
 * @return The List of Strings with substitutions made
 */
fun MutableList<String>.replaceMap(replacements: Map<String, String>) : MutableList<String> {
    for ((i, line) in this.withIndex()) {
        val replaced = line.replaceMap(replacements)
        this[i] = replaced
    }
    return this
}

/* ChatColor Related */

/**
 * Translates the standard alternate ChatColor code (&) into valid color codes
 * @return The String with properly translated color codes
 */
fun String.translateColor() : String =
    ChatColor.translateAlternateColorCodes('&', this)

/**
 * Removes ChatColor codes from the String
 * @return The String sans ChatColor color codes
 */
fun String.stripColor() : String =
    ChatColor.stripColor(this)!!

/**
 * Sequentially translates and strips ChatColor codes from the String
 * @return The String sans both valid color codes in addition to untranslated color codes
 */
fun String.translateAndStripColorCodes() : String =
    this.translateColor().stripColor()

/**
 * Whether the String contains either valid ChatColor color codes or untranslated color codes.
 * This is determined by translating and stripping color codes and comparing the given String to the new String.
 * @return Whether the String contains color codes
 */
fun String.containsChatColor() : Boolean {
    val tas = this.translateAndStripColorCodes()
    return this != tas
}

/**
 * Translates the standard alternate ChatColor code (&) into valid color codes
 * @return The `List<String>`, each line with properly translated color codes
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
 * @return Whether the String consists of only ChatColors
 */
fun String.isOnlyColorCodes() : Boolean {
    val stripped = this.translateAndStripColorCodes().trim()
    return stripped == ""
}