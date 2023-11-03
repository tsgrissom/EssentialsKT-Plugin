package io.github.tsgrissom.pluginapi.extension.bukkit

import io.github.tsgrissom.pluginapi.extension.kt.translateColor
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Sends the variable Strings to the CommandSender after processing them with ChatColor#translateAlternateColorCodes
 * for the standard alt-code ampersand.
 *
 * @param text The text to send to the CommandSender with alt-codes translated.
 */
fun CommandSender.sendColored(vararg text: String) =
    text.forEach { this.sendMessage(it.translateColor()) }

/**
 * Sends the CommandSender an Array of ChatComponentAPI BaseComponents via Kotlin's spread operator. Such an Array
 * is the result of calling ComponentBuilder#create therefore this method reduces mental overhead.
 *
 * @param arr The Array of BaseComponents to send to the CommandSender.
 */
fun CommandSender.sendChatComponents(arr: Array<BaseComponent>) =
    this.spigot().sendMessage(*arr)

/**
 * If the CommandSender is a Player, returns their current world. If the CommandSender is not a Player, fetches the
 * World at the first index of <code>Bukkit#getWorlds</code>.
 *
 * @return Either the Player's World, or the first World of Bukkit's getWorlds() Collection.
 */
fun CommandSender.getCurrentWorldOrDefault() : World {
    var w = Bukkit.getWorlds()[0]
    if (this is Player)
        w = this.world
    return w
}