package io.github.tsgrissom.pluginapi.extension

import org.bukkit.ChatColor

/* Equality */

/**
 * Checks for equality between two Strings with case-insensitivity
 * @param s The String to compare against
 * @return Whether the Strings are equal regardless of character case
 */
fun String.equalsIc(s: String) : Boolean =
    this.equals(s, ignoreCase=true)

/**
 * Checks for equality between one String and any number of other Strings with case-insensitivity
 * @param matches Any number of Strings to compare against
 * @return Whether a match was made regardless of character case
 */
fun String.equalsIc(vararg matches: String) : Boolean =
    matches.firstOrNull { this.equalsIc(it) } != null

/**
 * Checks for exact equality between one String and any number of other Strings with case-sensitivity
 * @param matches Any number of Strings to compare against
 * @return Whether exact equality was found
 */
fun String.equalsAny(vararg matches: String) : Boolean =
    matches.firstOrNull { this == it } != null

fun String.translateColor() : String = ChatColor.translateAlternateColorCodes('&', this)
fun String.stripColor() : String = ChatColor.stripColor(this)
fun String.translateAndStripColorCodes() : String = this.translateColor().stripColor()