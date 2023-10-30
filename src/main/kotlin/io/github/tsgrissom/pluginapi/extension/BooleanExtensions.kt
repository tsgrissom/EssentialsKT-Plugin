package io.github.tsgrissom.pluginapi.extension

import io.github.tsgrissom.pluginapi.enum.BooleanFormat
import net.md_5.bungee.api.ChatColor.GREEN
import net.md_5.bungee.api.ChatColor.RED

fun Boolean.fmt(
    format: BooleanFormat,
    capitalize: Boolean = false,
    withColor: Boolean = true
) : String {
    val tStr = format.trueStr
    val fStr = format.falseStr

    val pre = if (withColor)
        if (this) GREEN.toString() else RED.toString()
    else
        String()
    var str = if (this) tStr else fStr

    if (capitalize)
        str = str.capitalize()

    return pre + str
}

fun Boolean.fmtYesNo(
    capitalize: Boolean = false,
    withColor: Boolean = false
) : String =
    this.fmt(BooleanFormat.YES_NO, capitalize=capitalize, withColor=withColor)

fun Boolean.fmtEnabledDisabled(
    capitalize: Boolean = false,
    withColor: Boolean = false
) : String =
    this.fmt(BooleanFormat.ENABLED_DISABLED, capitalize=capitalize, withColor=withColor)