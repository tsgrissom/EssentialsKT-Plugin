package io.github.tsgrissom.essentialskt

import io.github.tsgrissom.essentialskt.command.*
import io.github.tsgrissom.essentialskt.listener.ChatListener
import io.github.tsgrissom.essentialskt.listener.JoinAndQuitListener
import io.github.tsgrissom.essentialskt.listener.MovementListener
import io.github.tsgrissom.essentialskt.manager.AfkManager
import io.github.tsgrissom.essentialskt.task.CheckAfkRunnable
import org.bukkit.Bukkit
import org.bukkit.Bukkit.*
import org.bukkit.plugin.java.JavaPlugin

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

    override fun onEnable() {
        /* Bootstrapping */
        instance = this
        afkManager = AfkManager()

        config.options().copyDefaults(true)
        saveDefaultConfig()

        getCommand("ntime")?.setExecutor(TimeCommand())

        /* Command Setup (General; A->Z) */
        getCommand("afk")?.setExecutor(AfkCommand())
        getCommand("clearchat")?.setExecutor(ClearChatCommand())
        getCommand("clearweather")?.setExecutor(ClearWeatherCommand())
        getCommand("feed")?.setExecutor(FeedCommand())
        getCommand("gamemode")?.setExecutor(GamemodeCommand())
        getCommand("heal")?.setExecutor(HealCommand())
        getCommand("nickname")?.setExecutor(NicknameCommand())
        getCommand("ping")?.setExecutor(PingCommand())
        getCommand("rain")?.setExecutor(RainCommand())
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