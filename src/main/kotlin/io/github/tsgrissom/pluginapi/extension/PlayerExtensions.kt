package io.github.tsgrissom.pluginapi.extension

import org.bukkit.Sound
import org.bukkit.entity.Player

/**
 * Retrieves the Player's IP address as String and optionally formats it to omit the leading slash character.
 * @param fmt Whether to remove the leading slash character which is prepended to Bukkit Player#getAddress calls.
 * @return The Player's IP address as a String.
 */
fun Player.getIPString(fmt: Boolean = true) : String {
    var s = this.address.toString()
    if (fmt)
        s = s.substring(1, s.length)
    return s
}

/**
 * Retrieves the Player's UUID as a String. Reduces mental overhead from calling <code>Player#getUniqueId#toString</code>.
 * @return The Player's UUID as a String.
 */
fun Player.getUniqueString() : String =
    this.uniqueId.toString()

/**
 * Plays the requisite Sound for the Player at their location with the provided volume and pitch.
 * @param sound The enumerated Sound to play for the Player.
 * @param volume The volume to play the Sound at as a Float. Default=1.
 * @param pitch The pitch to play the Sound at as a Float. Default=1.
 */
fun Player.playSound(
    sound: Sound,
    volume: Float = 1F,
    pitch: Float = 1F
) =
    this.playSound(this.location, sound, volume, pitch)