package io.github.tsgrissom.essentialskt.manager

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration

class ConfigManager {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")

    fun getFileConfiguration() : FileConfiguration = getPlugin().config
    fun getSection(key: String) : ConfigurationSection? =
        getFileConfiguration().getConfigurationSection(key)
    fun getMessagesSection() : ConfigurationSection? =
        getSection("Messages")

    fun isDebuggingActive() : Boolean =
        getFileConfiguration().getBoolean("IsDebuggingActive", false)
}