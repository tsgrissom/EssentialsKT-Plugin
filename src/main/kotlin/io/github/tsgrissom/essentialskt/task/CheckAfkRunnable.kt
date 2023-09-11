package io.github.tsgrissom.essentialskt.task

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class CheckAfkRunnable : BukkitRunnable() {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getAfkManager() = getPlugin().afkManager

    override fun run() {
        val triggerDifference = getAfkManager().getNotifyAfterDuration()
        val now = System.currentTimeMillis()
        val map = getAfkManager().lastMovementTrackingMap

        for ((k, v) in map) {
            val diff = now - v
            if (diff > triggerDifference) {
                val p = Bukkit.getPlayer(UUID.fromString(k))
                    ?: continue
                if (getAfkManager().isAfk(p))
                    continue

                getAfkManager().makeAfk(p)
            }
        }
    }
}