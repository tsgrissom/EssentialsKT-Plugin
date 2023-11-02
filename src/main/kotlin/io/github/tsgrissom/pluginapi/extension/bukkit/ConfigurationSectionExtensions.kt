package io.github.tsgrissom.pluginapi.extension.bukkit

import io.github.tsgrissom.pluginapi.extension.kt.resolveChatColor
import org.bukkit.ChatColor
import org.bukkit.configuration.ConfigurationSection

fun ConfigurationSection.getKeys() : MutableSet<String> =
    this.getKeys(false)

fun ConfigurationSection.getChatColor(key: String) : ChatColor? {
    val notSet = !this.isSet(key)

    if (notSet)
        return null

    val str = this.getString(key)!!
    return str.resolveChatColor()
}

fun ConfigurationSection.getChatColor(key: String, def: ChatColor) : ChatColor {
    val notSet = !this.isSet(key)

    if (notSet)
        return def

    val str = this.getString(key)!!
    return str.resolveChatColor() ?: def
}