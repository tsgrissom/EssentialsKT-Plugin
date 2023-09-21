package io.github.tsgrissom.essentialskt

import com.earth2me.essentials.Essentials
import io.github.tsgrissom.essentialskt.command.*
import io.github.tsgrissom.essentialskt.listener.ChatListener
import io.github.tsgrissom.essentialskt.listener.JoinAndQuitListener
import io.github.tsgrissom.essentialskt.manager.ConfigManager
import io.github.tsgrissom.essentialskt.misc.PluginLogger
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.utility.EntityUtility
import org.bukkit.Bukkit.getPluginManager
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

private fun EssentialsKTPlugin.registerCommand(label: String, impl: CommandBase) {
    val bukkitCommand = this.getCommand(label)!!
    bukkitCommand.setExecutor(impl)
    bukkitCommand.tabCompleter = impl
}

private fun EssentialsKTPlugin.registerListeners(vararg listeners: Listener) {
    for (l in listeners) {
        getPluginManager().registerEvents(l, this)
    }
}

class EssentialsKTPlugin : JavaPlugin() {

    private lateinit var configManager: ConfigManager
    private lateinit var entityUtility: EntityUtility
    private lateinit var essentialsPlugin: Essentials

    fun getConfigManager() = this.configManager
    fun getEntityUtility() = this.entityUtility
    fun getEssentials() = this.essentialsPlugin

    /* Static Instance */
    companion object {
        var instance: EssentialsKTPlugin? = null
        private set
    }

    private fun registerCommands() {
        /* Register General Commands (A->Z) */
        registerCommand("damage", DamageCommand())
        registerCommand("clearchat", ClearChatCommand())
        registerCommand("clearweather", ClearWeatherCommand())
        registerCommand("feed", FeedCommand())
        registerCommand("gamemode", GameModeCommand())
        registerCommand("heal", HealCommand())
        registerCommand("ipaddress", IPAddressCommand())
        registerCommand("list", ListCommand())
        registerCommand("ping", PingCommand())
        registerCommand("rain", RainCommand())
        registerCommand("remove", RemoveCommand())
        registerCommand("renameitem", RenameItemCommand())
        registerCommand("setfoodlevel", SetFoodLevelCommand())
        registerCommand("sethealth", SetHealthCommand())
        registerCommand("suicide", SuicideCommand())
        registerCommand("time", TimeCommand())
        registerCommand("toggledownfall", ToggleDownfallCommand())
        registerCommand("uniqueid", UniqueIdCommand())
        registerCommand("whois", WhoIsCommand())

        /* Quick Time Commands (Early->Late) */
        registerCommand("day", DayCommand())
        registerCommand("midnight", MidnightCommand())
        registerCommand("night", NightCommand())
        registerCommand("noon", NoonCommand())
        registerCommand("sunset", SunsetCommand())
        registerCommand("sunrise", SunriseCommand())
    }

    override fun onEnable() {
        /* Bootstrapping */
        instance = this
        configManager = ConfigManager()
        entityUtility = EntityUtility()

        val essPlugin: Plugin? = getPluginManager().getPlugin("Essentials")

        if (essPlugin == null) {
            PluginLogger.severe("[EssentialsKT] Essentials not found! Please install EssentialsX to use EssentialsKT.")
            return getPluginManager().disablePlugin(this)
        }

        this.essentialsPlugin = essPlugin as Essentials

        PluginLogger.info("[EssentialsKT] Essentials hook created.")

        config.options().copyDefaults(true)
        saveDefaultConfig()

        this.registerCommands()
        this.registerListeners(
            ChatListener(),
            JoinAndQuitListener()
        )
    }
}