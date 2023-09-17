package io.github.tsgrissom.essentialskt.manager

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.event.PlayerAfkChangeEvent
import io.github.tsgrissom.pluginapi.extension.getUniqueString
import io.github.tsgrissom.pluginapi.extension.translateColor
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class AfkManager {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfiguration() : FileConfiguration = getPlugin().config

    fun getCheckAfkInterval() : Long =
        getConfiguration().getLong("Features.CheckAfkInterval", 100)
    fun getNotifyAfterDuration() : Long =
        getConfiguration().getLong("Features.NotifyAfkAfter", 600)
    fun shouldCameraMovementCountForAfkTracking() : Boolean =
        getConfiguration().getBoolean("Features.CameraMovementCountsForAfk", false)
    private fun getAfkMessage() : String =
        getConfiguration().getString("Messages.Afk", "&c%pd% &6is AFK...")!!
    private fun getNotAfkMessage() : String =
        getConfiguration().getString("Messages.NotAfk", "&c%pd% &6is no longer AFK")!!

    val lastMovementTrackingMap: MutableMap<String, Long> = mutableMapOf()
    private val afkSet: MutableSet<String> = mutableSetOf()

    fun toggleAfk(p: Player) {
        if (isAfk(p)) {
            removeAfk(p)
        } else {
            makeAfk(p)
        }
    }

    fun makeAfk(p: Player) {
        if (afkSet.contains(p.getUniqueString()))
            return

        val message = getAfkMessage()
            .replace("%pn%", p.name)
            .replace("%pd%", p.displayName)
            .translateColor()
        val event = PlayerAfkChangeEvent(p, goneAfk=true)
        Bukkit.getPluginManager().callEvent(event)

        if (!event.isCancelled()) {
            afkSet.add(p.uniqueId.toString())
            Bukkit.broadcastMessage(message)
        }
    }

    fun removeAfk(p: Player) {
        val isAfk = isAfk(p)
        val event = PlayerAfkChangeEvent(p, goneAfk=false)

        Bukkit.getPluginManager().callEvent(event)

        if (isAfk && !event.isCancelled()) {
            afkSet.remove(p.getUniqueString())
            val message = getNotAfkMessage()
                .replace("%pn%", p.name)
                .replace("%pd%", p.displayName)
                .translateColor()
            Bukkit.broadcastMessage(message)
        }
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