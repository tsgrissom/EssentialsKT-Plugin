package io.github.tsgrissom.pluginapi.extension

import org.bukkit.Sound
import org.bukkit.entity.Player

fun Player.getIPString() : String {
    val s = this.address.toString()
    return s.substring(1, s.length)
}

fun Player.getUniqueString() =
    this.uniqueId.toString()

fun Player.playSound(sound: Sound) =
    this.playSound(this.location, sound, 1F, 1F)