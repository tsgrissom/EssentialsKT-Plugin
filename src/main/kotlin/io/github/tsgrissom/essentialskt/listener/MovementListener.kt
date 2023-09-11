package io.github.tsgrissom.essentialskt.listener

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class MovementListener : Listener {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getAfkManager() = getPlugin().afkManager

    @EventHandler
    fun onMove(e: PlayerMoveEvent) {
        fun isCameraMovement() =
            (e.from.x == e.to?.x && e.from.y == e.to?.y && e.from.z == e.to?.z)

        val afkTrackCamera = getAfkManager().shouldCameraMovementCountForAfkTracking()
        val p = e.player

        if ((isCameraMovement() && afkTrackCamera) || !isCameraMovement()) {
            getAfkManager().storeMovement(p)
            getAfkManager().removeAfk(p)
        }
    }
}