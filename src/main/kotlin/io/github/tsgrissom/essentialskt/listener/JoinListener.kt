package io.github.tsgrissom.essentialskt.listener

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.pluginapi.extension.translateColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinListener : Listener {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val p = e.player

        e.joinMessage = getConfig().getJoinMessage()
            .translateColor()
            .replace("%pd%", p.displayName)
            .replace("%pn%", p.name)
    }
}