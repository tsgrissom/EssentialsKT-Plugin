package io.github.tsgrissom.essentialskt.manager

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import org.bukkit.configuration.file.FileConfiguration

class NicknameManager {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfiguration() : FileConfiguration = getPlugin().config

    // TODO Manage nickname storage, setting, resetting, etc.
}