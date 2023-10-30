package io.github.tsgrissom.essentialskt.misc

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import org.bukkit.Bukkit

class PluginLogger {

    companion object {
        private fun getPlugin() : EssentialsKTPlugin =
            EssentialsKTPlugin.instance ?: error("plugin instance is null")

        private val isDebugging: Boolean = getPlugin().getConfigManager().isDebuggingActive()

        private fun getPrefix(debug: Boolean) = if (debug && isDebugging) "DEBUG: " else String()

        fun info(s: String, debug: Boolean = false) = Bukkit.getLogger().info("${getPrefix(debug)}$s")
        fun warn(s: String, debug: Boolean = false) = Bukkit.getLogger().warning("${getPrefix(debug)}$s")
        fun severe(s: String, debug: Boolean = false) = Bukkit.getLogger().severe("${getPrefix(debug)}$s")

        fun info(vararg s: String)   = s.forEach { info(it, isDebugging) }
        fun warn(vararg s: String)   = s.forEach { warn(it, isDebugging) }
        fun severe(vararg s: String) = s.forEach { severe(it, isDebugging) }
    }
}