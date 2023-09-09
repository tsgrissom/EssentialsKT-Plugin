package io.github.tsgrissom.testpluginkt.listener

import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

import io.github.tsgrissom.testpluginkt.TestPluginKT

class ChatListener : Listener {

    private fun getConfiguration() : FileConfiguration {
        val plugin = TestPluginKT.instance ?: throw IllegalStateException("plugin instance is null")
        return plugin.config ?: throw IllegalStateException("config is null")
    }

    private fun getMessage() : String = getConfiguration().getString("messages.events.chat")

    private fun translateColorsAndMakeSubstitutions(e: AsyncPlayerChatEvent) : String {
        var new = ChatColor.translateAlternateColorCodes('&', getMessage())
        new = new.replace("%player%", e.player.name)
        new = new.replace("%message%", e.message)
        return new
    }

    @EventHandler
    fun onChat(e: AsyncPlayerChatEvent) {
        e.message = translateColorsAndMakeSubstitutions(e)
    }
}