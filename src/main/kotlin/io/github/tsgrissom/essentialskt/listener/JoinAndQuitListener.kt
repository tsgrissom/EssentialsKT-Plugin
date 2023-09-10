package io.github.tsgrissom.essentialskt.listener

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.pluginapi.extension.translateColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class JoinAndQuitListener : Listener {

    private fun getConfiguration() : FileConfiguration {
        val plugin = EssentialsKTPlugin.instance ?: throw IllegalStateException("plugin instance is null")
        return plugin.config ?: throw IllegalStateException("config is null")
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        e.joinMessage = getConfiguration()
            .getString("Messages.JoinEvent")
            ?.translateColor()
            ?.replace("%player%", e.player.displayName)
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        e.quitMessage = getConfiguration()
            .getString("Messages.QuitEvent")
            ?.translateColor()
            ?.replace("%player%", e.player.displayName)
    }
}