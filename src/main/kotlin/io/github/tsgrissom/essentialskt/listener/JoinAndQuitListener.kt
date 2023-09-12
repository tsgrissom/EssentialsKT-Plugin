package io.github.tsgrissom.essentialskt.listener

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.pluginapi.extension.translateColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class JoinAndQuitListener : Listener {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getAFKManager() = getPlugin().afkManager
    private fun getConfiguration() : FileConfiguration = getPlugin().config

    private fun getJoinMessage() = getConfiguration().getString("Messages.JoinEvent", "&a&l+ &e%pd% &6has joined the server")!!
    private fun getQuitMessage() = getConfiguration().getString("Messages.QuitEvent", "&c&l- &e%pd% &6has left the server")!!

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val p = e.player

        e.joinMessage = getJoinMessage()
            .translateColor()
            .replace("%pd%", p.displayName)
            .replace("%pn%", p.name)

        getAFKManager().storeMovement(p)
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val p = e.player

        e.quitMessage = getQuitMessage()
            .translateColor()
            .replace("%pd%", p.displayName)
            .replace("%pn%", p.name)

        getAFKManager().lastMovementTrackingMap.remove(p.uniqueId.toString())
    }
}