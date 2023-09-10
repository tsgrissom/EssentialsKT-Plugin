package io.github.tsgrissom.testpluginkt.listener

import io.github.tsgrissom.pluginapi.extension.translateColor
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

    @EventHandler
    fun onChat(e: AsyncPlayerChatEvent) {
        e.message = getConfiguration()
            .getString("Messages.ChatEvent")
            .translateColor()
            .replace("%player%", e.player.displayName)
            .replace("%message%", e.message)
    }
}