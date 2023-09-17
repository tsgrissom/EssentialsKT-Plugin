package io.github.tsgrissom.essentialskt.listener

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.pluginapi.extension.translateColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerChatEvent

class ChatListener : Listener {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfiguration() = getPlugin().config
    private fun getAfkManager() = getPlugin().afkManager

    @EventHandler
    fun onAsyncChat(e: AsyncPlayerChatEvent) {
        val p = e.player

        e.message = getConfiguration()
            .getString("Messages.ChatEvent", "&e%pd% &7: &f%message%")!!
            .translateColor()
            .replace("%pd%", p.displayName)
            .replace("%message%", e.message)
    }

    @EventHandler
    fun onSyncChat(e: PlayerChatEvent) {
        val p = e.player
        getAfkManager().storeMovement(p)

        Bukkit.getScheduler().runTaskLater(getPlugin(), Runnable {
            getAfkManager().removeAfk(p)
        }, 10)
    }
}