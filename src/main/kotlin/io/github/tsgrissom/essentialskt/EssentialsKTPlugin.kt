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
import org.bukkit.Bukkit.getPluginManager
import org.bukkit.Bukkit.getScheduler
import org.bukkit.plugin.java.JavaPlugin

fun EssentialsKTPlugin.registerCommand(label: String, impl: CommandBase) {
    val bukkitCommand = this.getCommand(label)!!
    bukkitCommand.setExecutor(impl)
    bukkitCommand.tabCompleter = impl
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
        registerCommand("damage", DamageCommand())
        registerCommand("clearchat", ClearChatCommand())
        registerCommand("feed", FeedCommand())
        registerCommand("gamemode", GameModeCommand())
        registerCommand("heal", HealCommand())
        registerCommand("ipaddress", IPAddressCommand())
        registerCommand("list", ListCommand())
        registerCommand("nickname", NicknameCommand())
        registerCommand("realname", RealNameCommand())
        registerCommand("remove", RemoveCommand())
        registerCommand("renameitem", RenameItemCommand())
        registerCommand("whois", WhoIsCommand())
    }

    override fun onEnable() {
        /* Bootstrapping */
        instance = this
        afkManager = AfkManager()
        configManager = ConfigManager()
        entityUtility = EntityUtility()

        config.options().copyDefaults(true)
        saveDefaultConfig()

        registerCommands()

        /* WIP Commands */
        getCommand("ntime")?.setExecutor(TimeCommand())
        getCommand("uniqueid")?.setExecutor(UniqueIdCommand())

        /* Command Setup (General; A->Z) */
        getCommand("afk")?.setExecutor(AfkCommand())
        getCommand("clearweather")?.setExecutor(ClearWeatherCommand())
        getCommand("ping")?.setExecutor(PingCommand())
        getCommand("rain")?.setExecutor(RainCommand())
        getCommand("suicide")?.setExecutor(SuicideCommand())
        getCommand("toggledownfall")?.setExecutor(ToggleDownfallCommand())

        /* Time Setting (Early->Late) */
        getCommand("day")?.setExecutor(DayCommand())
        getCommand("midnight")?.setExecutor(MidnightCommand())
        getCommand("night")?.setExecutor(NightCommand())
        getCommand("noon")?.setExecutor(NoonCommand())
        getCommand("sunset")?.setExecutor(SunsetCommand())
        getCommand("sunrise")?.setExecutor(SunriseCommand())

        /* Listener Registration */
        getPluginManager().registerEvents(ChatListener(), this)
        getPluginManager().registerEvents(MovementListener(), this)
        getPluginManager().registerEvents(JoinAndQuitListener(), this)

        scheduleCheckAfkTask()
    }

    override fun onDisable() {
        getScheduler().cancelTask(checkAfkTaskId)
    }
}