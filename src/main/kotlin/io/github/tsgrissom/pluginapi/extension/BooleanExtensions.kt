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