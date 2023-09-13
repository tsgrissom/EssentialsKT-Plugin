package io.github.tsgrissom.essentialskt

import io.github.tsgrissom.essentialskt.command.*
import io.github.tsgrissom.essentialskt.listener.ChatListener
import io.github.tsgrissom.essentialskt.listener.JoinAndQuitListener
import io.github.tsgrissom.essentialskt.listener.MovementListener
import io.github.tsgrissom.essentialskt.manager.AfkManager
import io.github.tsgrissom.essentialskt.task.CheckAfkRunnable
import io.github.tsgrissom.pluginapi.command.CommandBase
import org.bukkit.Bukkit
import org.bukkit.Bukkit.*
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.plugin.java.JavaPlugin

fun EssentialsKTPlugin.registerCommand(label: String, impl: CommandBase) {
    val bukkitCommand = this.getCommand(label)!!
    bukkitCommand.setExecutor(impl)
    bukkitCommand.tabCompleter = impl
}

class EssentialsKTPlugin : JavaPlugin() {

    lateinit var afkManager: AfkManager
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
        registerCommand("clearchat", ClearChatCommand())
        registerCommand("gamemode", GamemodeCommand())
        registerCommand("list", ListCommand())
    }

    override fun onEnable() {
        /* Bootstrapping */
        instance = this
        afkManager = AfkManager()

        config.options().copyDefaults(true)
        saveDefaultConfig()

        registerCommands()

        getCommand("ntime")?.setExecutor(TimeCommand())

        /* Command Setup (General; A->Z) */
        getCommand("afk")?.setExecutor(AfkCommand())
        getCommand("clearweather")?.setExecutor(ClearWeatherCommand())
        getCommand("feed")?.setExecutor(FeedCommand())
        getCommand("heal")?.setExecutor(HealCommand())
        getCommand("nickname")?.setExecutor(NicknameCommand())
        getCommand("ping")?.setExecutor(PingCommand())
        getCommand("rain")?.setExecutor(RainCommand())
        getCommand("remove")?.setExecutor(RemoveCommand())
        getCommand("suicide")?.setExecutor(SuicideCommand())
        getCommand("toggledownfall")?.setExecutor(ToggleDownfallCommand())
        getCommand("weather")?.setExecutor(WeatherCommand())
        getCommand("whois")?.setExecutor(WhoIsCommand())

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