package io.github.tsgrissom.essentialskt.misc

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import org.bukkit.Bukkit
import java.util.logging.Level

class PluginLogger {

    companion object {
        private fun getPlugin() =
            EssentialsKTPlugin.instance ?: error("plugin instance is null")
        private fun isDebuggingActive() =
            getPlugin().config.getBoolean("IsDebuggingActive", false)

        private fun getPrefix(withPrefix: Boolean, withDebugPrefix: Boolean) =
            if (withDebugPrefix && isDebuggingActive())
                "[EssKT:DEBUG] "
            else if (withPrefix)
                "[EssKT] "
            else
                ""

        private fun bLog(level: Level, withPrefix: Boolean, withDebugPrefix: Boolean, vararg text: String) {
            if (text.isEmpty())
                return

            val logger = Bukkit.getLogger()
            val prefix = getPrefix(withPrefix=withPrefix, withDebugPrefix=withDebugPrefix)

            if (text.size == 1) {
                var line = prefix
                line += text[0]

                logger.log(level, line)

                return
            }

            for ((i, str) in text.withIndex()) {
                if (i == 0) {
                    logger.log(level, prefix)
                }

                logger.log(level, str)
            }
        }

        fun info(vararg text: String, withPrefix: Boolean = false) =
            bLog(Level.INFO, withPrefix=withPrefix, withDebugPrefix=false, *text)
        fun warn(vararg text: String, withPrefix: Boolean = false) =
            bLog(Level.WARNING, withPrefix=withPrefix, withDebugPrefix=false, *text)
        fun severe(vararg text: String, withPrefix: Boolean = false) =
            bLog(Level.SEVERE, withPrefix=withPrefix, withDebugPrefix=false, *text)
        fun infoD(vararg text: String, withPrefix: Boolean = false) =
            bLog(Level.INFO, withPrefix=withPrefix, withDebugPrefix=true, *text)
        fun warnD(vararg text: String, withPrefix: Boolean = false) =
            bLog(Level.WARNING, withPrefix=withPrefix, withDebugPrefix=true, *text)
        fun severeD(vararg text: String, withPrefix: Boolean = false) =
            bLog(Level.SEVERE, withPrefix=withPrefix, withDebugPrefix=true, *text)
    }
}