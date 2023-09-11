package io.github.tsgrissom.essentialskt.manager

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.pluginapi.extension.translateColor
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class AfkManager {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getAFKManager() = getPlugin().afkManager
    private fun getConfiguration() : FileConfiguration = getPlugin().config

    fun getCheckAfkInterval() : Long = getConfiguration().getLong("Features.CheckAfkInterval", 100)
    fun getNotifyAfterDuration() : Long = getConfiguration().getLong("Features.NotifyAfkAfter", 600)

    val lastMovementTrackingMap: MutableMap<String, Long> = mutableMapOf()
    val afkSet: MutableSet<String> = mutableSetOf()

    fun makeAfk(p: Player) {
        if (!afkSet.contains(p.uniqueId.toString())) {
            afkSet.add(p.uniqueId.toString())
            Bukkit.broadcastMessage("&c${p.name} &6is AFK...".translateColor())
        }
        // TODO Fire custom event
    }

    fun removeAfk(p: Player) {
        val isAfk = isAfk(p)
        afkSet.remove(p.uniqueId.toString())
        if (isAfk)
            Bukkit.broadcastMessage("&c${p.name} &6is no longer AFK".translateColor())
    }

    fun isAfk(p: Player) = afkSet.contains(p.uniqueId.toString())

    fun storeMovement(p: Player) {
        lastMovementTrackingMap[p.uniqueId.toString()] = System.currentTimeMillis()
    }

    fun fetchLastMovementMillis(p: Player) : Long {
        if (!p.isOnline)
            error("Cannot fetch last movement time for offline player")
        return lastMovementTrackingMap[p.uniqueId.toString()] ?: System.currentTimeMillis()
    }
}