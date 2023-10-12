package io.github.tsgrissom.pluginapi.extension

import net.md_5.bungee.api.ChatColor.GREEN
import net.md_5.bungee.api.ChatColor.RED

enum class BooleanFormat(val trueStr: String, val falseStr: String) {
    ENABLE_DISABLE("enable", "disable"),
    ENABLED_DISABLED("enabled", "disabled"),
    TRUE_FALSE("true", "false"),
    ON_OFF("on", "off"),
    YES_NO("yes", "no");
}

fun Boolean.palatable(withColor: Boolean = false) : String {
    val str = if (this) "Yes" else "No"
    val pre = if (withColor)
        if (this) GREEN.toString() else RED.toString()
    else
        String()
    return pre + str
}

fun Boolean.format(
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