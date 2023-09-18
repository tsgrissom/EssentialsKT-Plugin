package io.github.tsgrissom.pluginapi.extension

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.TextComponent

fun ComponentBuilder.appendc(str: String, color: ChatColor) : ComponentBuilder = this.append(str).color(color)
fun ComponentBuilder.appendc(text: TextComponent, color: ChatColor) : ComponentBuilder = this.append(text).color(color)