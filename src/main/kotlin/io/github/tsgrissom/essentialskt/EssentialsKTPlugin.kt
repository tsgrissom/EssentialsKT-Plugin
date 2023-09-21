package io.github.tsgrissom.essentialskt

import io.github.tsgrissom.essentialskt.command.*
import io.github.tsgrissom.essentialskt.listener.ChatListener
import io.github.tsgrissom.essentialskt.listener.JoinAndQuitListener
import io.github.tsgrissom.essentialskt.listener.MovementListener
import io.github.tsgrissom.essentialskt.manager.AfkManager
import io.github.tsgrissom.essentialskt.manager.ConfigManager
import io.github.tsgrissom.essentialskt.task.CheckAfkRunnable
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.utility.EntityUtility
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getPluginManager
import org.bukkit.Bukkit.getScheduler
import org.bukkit.event.Listener
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

    lateinit var afkManager: AfkManager
    lateinit var configManager: ConfigManager
    lateinit var entityUtility: EntityUtility
    private var checkAfkTaskId: Int = -1

    /* Static Instance */
    companion object {
        var instance: EssentialsKTPlugin? = null
        private set
    }

    private fun scheduleCheckAfkTask() {
        val checkAfkInterval = afkManager.getCheckAfkInterval()
        val checkAfkTask = CheckAfkRunnable()
            .runTaskTimer(this, 0, checkAfkInterval)
        checkAfkTaskId = checkAfkTask.taskId
    }

    private fun registerCommands() {
        /* Register General Commands (A->Z) */
        registerCommand("afk", AfkCommand())
        registerCommand("damage", DamageCommand())
        registerCommand("clearchat", ClearChatCommand())
        registerCommand("clearweather", ClearWeatherCommand())
        registerCommand("feed", FeedCommand())
        registerCommand("gamemode", GameModeCommand())
        registerCommand("heal", HealCommand())
        registerCommand("ipaddress", IPAddressCommand())
        registerCommand("list", ListCommand())
        registerCommand("nickname", NicknameCommand())
        registerCommand("ping", PingCommand())
        registerCommand("rain", RainCommand())
        registerCommand("realname", RealNameCommand())
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
        afkManager = AfkManager()
        configManager = ConfigManager()
        entityUtility = EntityUtility()

        config.options().copyDefaults(true)
        saveDefaultConfig()

        this.registerCommands()
        this.registerListeners(
            ChatListener(),
            MovementListener(),
            JoinAndQuitListener()
        )

        scheduleCheckAfkTask()
    }

    override fun onDisable() {
        getScheduler().cancelTask(checkAfkTaskId)
    }
}