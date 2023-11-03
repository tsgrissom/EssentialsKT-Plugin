package io.github.tsgrissom.pluginapi.extension.bukkit

import io.github.tsgrissom.pluginapi.extension.kt.resolveChatColor
import org.bukkit.ChatColor
import org.bukkit.configuration.ConfigurationSection

/**
 * Re-implements ConfigurationSection#getKeys with a default deep value of false. Much cleaner usage when you want
 * single-depth key scanning.
 * @return A MutableSet of configuration keys directly descendant from the receiver ConfigurationSection.
 */
fun ConfigurationSection.getKeys() : MutableSet<String> =
    this.getKeys(false)

/**
 * Attempt to parse a Bukkit ChatColor from the String value of the requisite key within the receiver
 * ConfigurationSection.
 * @param key ConfigurationSection key.
 * @return A Bukkit ChatColor or null.
 */
fun ConfigurationSection.getChatColor(key: String) : ChatColor? {
    val notSet = !this.isSet(key)

    if (notSet)
        return null

    val str = this.getString(key)!!
    return str.resolveChatColor()
}

/**
 * Parse a Bukkit ChatColor from the String value of the requisite key within the receiver ConfigurationSection. If this
 * returns null, the default ChatColor value is returned to ensure null-safety.
 * @param key ConfigurationSection key.
 * @param def The fallback Bukkit ChatColor.
 * @return A BukkitChatColor. Either the value for the given key or the default argument.
 */
fun ConfigurationSection.getChatColor(key: String, def: ChatColor) : ChatColor {
    val notSet = !this.isSet(key)

    if (notSet)
        return def

    val str = this.getString(key)!!
    return str.resolveChatColor() ?: def
}