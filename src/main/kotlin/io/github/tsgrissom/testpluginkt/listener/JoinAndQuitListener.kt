package io.github.tsgrissom.testpluginkt.listener

import io.github.tsgrissom.testpluginkt.TestPluginKT
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class JoinAndQuitListener : Listener {

    private fun getConfiguration() : FileConfiguration {
        val plugin = TestPluginKT.instance ?: throw IllegalStateException("plugin instance is null")
        return plugin.config ?: throw IllegalStateException("config is null")
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        var message = getConfiguration().getString("messages.events.join")

        message = ChatColor.translateAlternateColorCodes('&', message)
        message = message.replace("%player%", e.player.name)

        e.joinMessage = message
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        var message = getConfiguration().getString("messages.events.quit")

        message = ChatColor.translateAlternateColorCodes('&', message)
        message = message.replace("%player%", e.player.name)

        e.quitMessage = message
    }
}