package io.github.tsgrissom.pluginapi.extension

import io.github.tsgrissom.pluginapi.enum.BooleanFormat
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
 * @return The resulting formatted String of the operation.
 */
fun Boolean.fmt(
    trueStr: String,
    falseStr: String,
    trueColor: ChatColor = ChatColor.GREEN,
    falseColor: ChatColor = ChatColor.RED,
    capitalize: Boolean = false,
    withColor: Boolean = true,
) : String {
    val pre = if (withColor)
        if (this) trueColor.toString() else falseColor.toString()
    else
        String()
    var str = if (this) trueStr else falseStr

    if (capitalize)
        str = str.capitalize()

    return pre + str
}

/**
 * Formats a Boolean to a more pleasant user-facing output format. Formats are enumerated in the BooleanFormat enum.
 * Uses default ChatColors of (true->GREEN, false->RED) more customizable <code>Boolean#fmt</code> method.
 *
 * @param format Which BooleanFormat to use in the place of specifying a trueStr and falseStr.
 * @param capitalize Whether to capitalize the String before prepending the ChatColor. Default is false.
 * @param withColor Whether to prepend a Boolean-corresponding ChatColor to the formatted String. Default is true.
 * @return The resulting formatted String of the operation.
 */
fun Boolean.fmt(
    format: BooleanFormat,
    capitalize: Boolean = false,
    withColor: Boolean = true
) : String =
    this.fmt(
        format.trueStr, format.falseStr,
        capitalize=capitalize, withColor=withColor
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