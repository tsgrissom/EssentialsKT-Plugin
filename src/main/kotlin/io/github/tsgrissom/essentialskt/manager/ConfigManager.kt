package io.github.tsgrissom.essentialskt.manager

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import net.md_5.bungee.api.ChatColor.*
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration

class ConfigManager {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getFileConfiguration() : FileConfiguration = getPlugin().config
    private fun getSection(key: String) : ConfigurationSection? =
        getFileConfiguration().getConfigurationSection(key)

    /* Base Options */
    fun isDebuggingActive() : Boolean =
        getFileConfiguration().getBoolean("IsDebuggingActive", false)

    /* Command Options */
    private fun getCommandsSection() : ConfigurationSection? =
        getSection("Commands")

    fun getClearChatRepeatBlankLineCount() : Int {
        val sect = getCommandsSection() ?: return 500
        return sect.getInt("ClearChatRepeatBlankLine", 500)
    }

    /* Chat Messages */
    private fun getMessagesSection() : ConfigurationSection? =
        getSection("Messages")

    fun getJoinMessage() : String {
        val def = "${GREEN}${BOLD}+ ${YELLOW}%pd% ${GOLD}has joined the server"
        val sect = getMessagesSection() ?: return def
        return sect.getString("JoinEvent", def) ?: def
    }
    fun getQuitMessage() : String {
        val def = "${RED}${BOLD}- ${YELLOW}%pd% ${GOLD}has left the server"
        val sect = getMessagesSection() ?: return def
        return sect.getString("QuitEvent", def) ?: def
    }
}