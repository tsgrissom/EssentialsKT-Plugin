package io.github.tsgrissom.essentialskt.listener

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.pluginapi.extension.translateColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class QuitListener : Listener {

    private fun getPlugin() =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val p = e.player

        e.quitMessage = getConfig().getQuitMessage()
            .translateColor()
            .replace("%pd%", p.displayName)
            .replace("%pn%", p.name)
    }
}