package io.github.tsgrissom.pluginapi.extension.kt

import org.bukkit.ChatColor

/**
 * Formats a Boolean to more pleasant user-facing output format.
 *
 * @param trueStr The String to display if the Boolean is true.
 * @param falseStr The String to display if the Boolean is false.
 * @param trueColor The ChatColor to prepend if <code>withColor</code> is true and the Boolean is true. Default is GREEN.
 * @param falseColor The ChatColor to prepend if <code>withColor</code> is true and the Boolean is false. Default is RED.
 * @param capitalize Whether to capitalize the String before prepending the ChatColor. Default is false.
 * @param withColor Whether to prepend a Boolean-corresponding ChatColor to the formatted String. Default is true.
 * @param invertedColor Whether to invert the true/false ChatColors where false->GREEN + true->RED.
 * @param invertedText Whether to invert the true/false Strings where false->trueStr + true->falseStr.
 * @return The resulting formatted String of the operation.
 */
fun Boolean.fmt(
    trueStr: String,
    falseStr: String,
    trueColor: ChatColor = ChatColor.GREEN,
    falseColor: ChatColor = ChatColor.RED,
    capitalize: Boolean = false,
    withColor: Boolean = true,
    invertedColor: Boolean = false,
    invertedText: Boolean = false
) : String {
    val notWithColor = withColor.not()
    val notInvertedColor = invertedColor.not()
    val notInvertedText = invertedText.not() // Not variables for expressive, readable logic below
    val trueColorStr = trueColor.toString()
    val falseColorStr = falseColor.toString()

    val color = if (notWithColor) "" else
        if (this) // if Boolean "this" is true
            if (notInvertedColor) trueColorStr else falseColorStr
        else
            if (notInvertedColor) falseColorStr else trueColorStr
    var text =
        if (this)
            if (notInvertedText) trueStr else falseStr
        else
            if (notInvertedText) falseStr else trueStr
    val reset = if (notWithColor) "" else ChatColor.RESET.toString()

    if (capitalize)
        text = text.capitalize()

    return color + text + reset
}

/**
 * Formats a Boolean to a more pleasant user-facing output format. Formats are enumerated in the BooleanFormat enum.
 * Uses default ChatColors of (true->GREEN, false->RED) more customizable <code>Boolean#fmt</code> method.
 *
 * @param fmt Which BooleanFormat to use in the place of specifying a trueStr and falseStr.
 * @param capitalize Whether to capitalize the String before prepending the ChatColor. Default is false.
 * @param withColor Whether to prepend a Boolean-corresponding ChatColor to the formatted String. Default is true.
 * @param invertedColor Whether to invert the true/false ChatColors where false->GREEN + true->RED.
 * @param invertedText Whether to invert the true/false Strings where false->trueStr + true->falseStr.
 * @return The resulting formatted String of the operation.
 */
fun Boolean.fmt(
    fmt: BooleanFormat,
    capitalize: Boolean = false,
    withColor: Boolean = true,
    invertedColor: Boolean = false,
    invertedText: Boolean = false
) : String =
    this.fmt(
        fmt.trueStr, fmt.falseStr,
        capitalize=capitalize, withColor=withColor,
        invertedColor=invertedColor, invertedText=invertedText
    )

/**
 * Formats a Boolean to the yes/no format, optionally with color or capitalization.
 *
 * @param capitalize Whether to capitalize the String before prepending the ChatColor. Default is false.
 * @param withColor Whether to prepend a Boolean-corresponding ChatColor to the formatted String. Default is true.
 * @return The resulting formatted String of the operation.
 */
fun Boolean.fmtYesNo(
    capitalize: Boolean = false,
    withColor: Boolean = true
) : String =
    this.fmt(BooleanFormat.YES_NO, capitalize=capitalize, withColor=withColor)

/**
 * Formats a Boolean to the enabled/disabled format, optionally with color or capitalization.
 *
 * @param capitalize Whether to capitalize the String before prepending the ChatColor. Default is false.
 * @param withColor Whether to prepend a Boolean-corresponding ChatColor to the formatted String. Default is true.
 * @return The resulting formatted String of the operation.
 */
fun Boolean.fmtEnabledDisabled(
    capitalize: Boolean = false,
    withColor: Boolean = true
) : String =
    this.fmt(BooleanFormat.ENABLED_DISABLED, capitalize=capitalize, withColor=withColor)