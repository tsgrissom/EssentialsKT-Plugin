package io.github.tsgrissom.pluginapi.extension.bukkit

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.TextComponent

/**
 * Shorthand method for appending a String and then a ChatColor to a ComponentBuilder. Reduces mental overhead and
 * increases readability of the construction of complex ComponentBuilder chains.
 *
 * @param str The String to append to the ComponentBuilder before appending a ChatColor.
 * @param color The ChatColor to append to the ComponentBuilder after appending the String.
 * @return The ComponentBuilder instance to continue method chaining.
 */
fun ComponentBuilder.appendc(str: String, color: ChatColor) : ComponentBuilder =
    this.append(str).color(color)

/**
 * Shorthand method for appending a TextComponent and then a ChatColor to a ComponentBuilder. Reduces mental overhead
 * and increases readability of the construction of complex ComponentBuilder chains.
 *
 * @param text The TextComponent to append to the ComponentBuilder before appending a ChatColor.
 * @param color The ChatColor to append to the ComponentBuilder after appending the text.
 * @return The ComponentBuilder instance to continue method chaining.
 */
fun ComponentBuilder.appendc(text: TextComponent, color: ChatColor) : ComponentBuilder =
    this.append(text).color(color)