package io.github.tsgrissom.pluginapi.extension

import org.bukkit.entity.Player

fun Player.getIPString() : String {
    val s = this.address.toString()
    return s.substring(1, s.length)
}

fun Player.getUniqueString() = this.uniqueId.toString()