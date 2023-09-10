package io.github.tsgrissom.essentialskt

import io.github.tsgrissom.essentialskt.command.*
import io.github.tsgrissom.essentialskt.listener.ChatListener
import io.github.tsgrissom.essentialskt.listener.JoinAndQuitListener
import org.bukkit.Bukkit.*
import org.bukkit.plugin.java.JavaPlugin


class EssentialsKTPlugin : JavaPlugin() {

    companion object {
        var instance: EssentialsKTPlugin? = null
        private set
    }

    override fun onEnable() {
        instance = this

        config.options().copyDefaults(true)
        saveDefaultConfig()

        getCommand("day")?.setExecutor(DayCommand())
        getCommand("gamemode")?.setExecutor(GamemodeCommand())
        getCommand("heal")?.setExecutor(HealCommand())
        getCommand("midnight")?.setExecutor(MidnightCommand())
        getCommand("night")?.setExecutor(NightCommand())
        getCommand("noon")?.setExecutor(NoonCommand())
        getCommand("ping")?.setExecutor(PingCommand())
        getCommand("suicide")?.setExecutor(SuicideCommand())
        getCommand("sunset")?.setExecutor(SunsetCommand())
        getCommand("sunrise")?.setExecutor(SunriseCommand())
        getCommand("whois")?.setExecutor(WhoIsCommand())

        getPluginManager().registerEvents(ChatListener(), this)
        getPluginManager().registerEvents(JoinAndQuitListener(), this)
    }
}