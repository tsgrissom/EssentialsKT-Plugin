package io.github.tsgrissom.essentialskt.config

import BukkitChatColor
import BungeeChatColor
import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.misc.PluginLogger
import io.github.tsgrissom.pluginapi.extension.bukkit.convertToBungeeChatColor
import io.github.tsgrissom.pluginapi.extension.bukkit.getKeys
import io.github.tsgrissom.pluginapi.extension.bukkit.isInputAlias
import io.github.tsgrissom.pluginapi.extension.kt.equalsIc
import io.github.tsgrissom.pluginapi.extension.kt.resolveChatColor
import io.github.tsgrissom.pluginapi.func.NonFormattingChatColorPredicate
import org.bukkit.configuration.ConfigurationSection

class ConfigManager {

    private fun getPlugin() =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getFileConfiguration() = getPlugin().config
    private fun save() = getPlugin().saveConfig()

    // MARK: Bootstrapping

    init {
        processConfiguration()
    }

    private fun processConfiguration() {
        val conf = getFileConfiguration()
        val defColors = getDefaultColorMap()

        fun save() = getPlugin().saveConfig()
        fun createColorsSection() : ConfigurationSection {
            val new = conf.createSection("Colors", defColors)
            save()
            return new
        }

        fun isValidColorString(value: String) : Boolean {
            val isInFiltered = BukkitChatColor.entries
                .filter { NonFormattingChatColorPredicate().test(it) }
                .firstOrNull { it.isInputAlias(value) } != null
            if (isInFiltered)
                return true

            val isColorCode = value.resolveChatColor()
            return isColorCode != null
        }

        var colors = conf.getConfigurationSection("Colors")!!
        var keys = colors.getKeys()

        if (keys.isEmpty()) {
            colors = createColorsSection()
            keys = colors.getKeys()
            PluginLogger.warn("A new Colors section was created in the config.yml", withPrefix=true)
        }

        for (key in keys) {
            if (!defColors.contains(key)) {
                PluginLogger.warnD("Unknown key in Colors section \"$key\", skipping...")
                continue
            }
            val value = colors.getString(key) ?: "NOT_A_STR"
            if (!isValidColorString(value)) {
                PluginLogger.warnD("In config.yml: \"Colors.$key\": Color value \"$value\" is not a valid color reference")
                continue
            }
            // Key is valid, ChatColor value is valid
            PluginLogger.infoD("Configured key-color value validated: \"Colors.$key\"->\"$value\"")
        }
    }

    // MARK: Configuration Sections

    private fun getSection(key: String) : ConfigurationSection? =
        getFileConfiguration().getConfigurationSection(key)
    private fun getMessagesSection() =
        getSection("Messages")
    private fun getCommandsSection() =
        getSection("Commands")

    // TODO Reload command + logic here
    // MARK: Configurable ChatColor

    private fun getDefaultColorMap() =
        ChatColorKey.entries.associate {
            it.name to it.defaultBukkit.name
        }

    fun getKeyedChatColorByName(str: String) : ChatColorKey? =
        ChatColorKey.entries.firstOrNull { it.name.equalsIc(str) }

    // TODO Write in docs about unexpected colors white or magic text and their meaning
    fun getChatColor(key: ChatColorKey) : BukkitChatColor {
        val defStr = getDefaultColorMap()[key.name]!!
        val def = BukkitChatColor.valueOf(defStr)
        val conf = getFileConfiguration().getConfigurationSection("Colors")
            ?: return def // This does not happen
        val colorValue = conf.getString(key.name, defStr)!! // Non-null asserted because default provided
        return colorValue.resolveChatColor()
            ?: BukkitChatColor.WHITE // Might happen, therefore default to
    }

    fun getBungeeChatColor(key: ChatColorKey) : BungeeChatColor {
        val def = key.defaultBungee
        return getChatColor(key).convertToBungeeChatColor() ?: def
    }

    // MARK: Debugger

    fun isDebuggingActive() : Boolean =
        getFileConfiguration().getBoolean("IsDebuggingActive", false)

    fun setDebugging(b: Boolean) {
        getPlugin().config.set("IsDebuggingActive", b)
        save()
    }

    fun toggleDebugging() : Boolean {
        val inverse = !isDebuggingActive()
        setDebugging(inverse)
        return inverse
    }

    // MARK: Configurable Commands Features

    fun getClearChatRepeatBlankLineCount() : Int {
        val sect = getCommandsSection() ?: return 500
        return sect.getInt("ClearChatRepeatBlankLine", 500)
    }

    // MARK: Configurable Chat Messages

    private fun getDefaultJoinMessage() : String {
        val ccGreen = BukkitChatColor.GREEN.toString()
        val ccBold  = BukkitChatColor.BOLD.toString()
        val ccYell  = BukkitChatColor.YELLOW.toString()
        val ccGold  = BukkitChatColor.GOLD.toString()
        return "${ccGreen}${ccBold}+ ${ccYell}%pd% ${ccGold}has joined the server"
    }

    fun getJoinMessage() : String {
        val def = getDefaultJoinMessage()
        val sect = getMessagesSection() ?: return def
        return sect.getString("JoinEvent", def) ?: def
    }

    private fun getDefaultQuitMessage() : String {
        val ccRed  = BukkitChatColor.RED.toString()
        val ccBold = BukkitChatColor.BOLD.toString()
        val ccYell = BukkitChatColor.YELLOW.toString()
        val ccGold = BukkitChatColor.GOLD.toString()
        return "${ccRed}${ccBold}- ${ccYell}%pd% ${ccGold}has left the server"
    }

    fun getQuitMessage() : String {
        val def = getDefaultQuitMessage()
        val sect = getMessagesSection() ?: return def
        return sect.getString("QuitEvent", def) ?: def
    }
}