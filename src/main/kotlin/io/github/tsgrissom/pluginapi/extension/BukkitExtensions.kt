package io.github.tsgrissom.pluginapi.extension

import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission

fun CommandSender.lacksPermission(p: String) = !this.hasPermission(p)
fun CommandSender.lacksPermission(p: Permission) = !this.hasPermission(p)
fun CommandSender.sendColored(s: String) = this.sendMessage(s.translateColor())