package io.github.tsgrissom.pluginapi.extension

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission

fun CommandSender.lacksPermission(p: String) = !this.hasPermission(p)
fun CommandSender.lacksPermission(p: Permission) = !this.hasPermission(p)
fun CommandSender.sendColored(vararg s: String) = s.forEach { this.sendMessage(it.translateColor()) }

fun Player.getIPString() : String {
    val s = this.address.toString()
    return s.substring(1, s.length)
}