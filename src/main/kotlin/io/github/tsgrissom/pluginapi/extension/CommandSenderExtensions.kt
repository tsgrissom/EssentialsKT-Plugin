package io.github.tsgrissom.pluginapi.extension

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission

fun CommandSender.lacksPermission(p: String) = !this.hasPermission(p)
fun CommandSender.lacksPermission(p: Permission) = !this.hasPermission(p)

fun CommandSender.sendColored(vararg s: String) =
    s.forEach { this.sendMessage(it.translateColor()) }
fun CommandSender.sendChatComponents(arr: Array<BaseComponent>) =
    this.spigot().sendMessage(*arr)

fun CommandSender.getCurrentWorldOrDefault() : World {
    var w = Bukkit.getWorlds()[0]
    if (this is Player)
        w = this.world
    return w
}