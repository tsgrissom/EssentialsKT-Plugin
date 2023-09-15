package io.github.tsgrissom.pluginapi.extension

import org.bukkit.ChatColor

fun Boolean.palatable(withColor: Boolean = false) : String {
    val s = if (this) "Yes" else "No"
    val pre = if (withColor)
        if (this)
            ChatColor.GREEN.toString()
        else
            ChatColor.RED.toString()
    else
        ""
    return pre + s
}

fun Boolean.format(
    format: String,
    capitalize: Boolean = false,
    withColor: Boolean = true
) : String {
    val validFormats = arrayOf("enabledisable", "enableddisabled", "truefalse", "yesno")
    fun appendValidFormats() : String {
        var s = String()
        validFormats.forEach { s += " $it" }
        return s.trim()
    }

    val tStr: String
    val fStr: String

    when (format) {
        "enabledisable", "disableenable" -> {
            tStr = "enable"
            fStr = "disable"
        }
        "enableddisabled", "disabledenabled" -> {
            tStr = "enabled"
            fStr = "disabled"
        }
        "truefalse", "falsetrue" -> {
            tStr = "true"
            fStr = "false"
        }
        "yesno", "noyes" -> {
            tStr = "yes"
            fStr = "no"
        }
        else -> error("Valid formats are: ${appendValidFormats()}")
    }

    val prefix = if (withColor)
        if (this) "&a" else "&c"
    else
        String()
    var str = if (this) tStr else fStr

    if (capitalize)
        str = str.capitalize()

    return "$prefix$str"
}