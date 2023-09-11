package io.github.tsgrissom.essentialskt.listener

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.pluginapi.extension.translateColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatListener : Listener {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfiguration() = getPlugin().config
    private fun getAfkManager() = getPlugin().afkManager

    @EventHandler
    fun onChat(e: AsyncPlayerChatEvent) {
        val p = e.player

        e.message = getConfiguration()
            .getString("Messages.ChatEvent", "&e%player% &7: &f%message%")!!
            .translateColor()
            .replace("%player%", p.displayName)
            .replace("%message%", e.message)

        getAfkManager().storeMovement(p)
        getAfkManager().removeAfk(p)
    }
}