package io.github.tsgrissom.essentialskt

import io.github.tsgrissom.essentialskt.command.*
import io.github.tsgrissom.essentialskt.listener.ChatListener
import io.github.tsgrissom.essentialskt.listener.JoinAndQuitListener
import org.bukkit.Bukkit.*
import org.bukkit.plugin.java.JavaPlugin

class EssentialsKTPlugin : JavaPlugin() {

    /* Static Instance */
    companion object {
        var instance: EssentialsKTPlugin? = null
        private set
    }

    override fun onEnable() {
        /* Bootstrapping */
        instance = this

        config.options().copyDefaults(true)
        saveDefaultConfig()

        /* Command Setup (General; A->Z) */
        getCommand("feed")?.setExecutor(FeedCommand())
        getCommand("gamemode")?.setExecutor(GamemodeCommand())
        getCommand("heal")?.setExecutor(HealCommand())
        getCommand("ping")?.setExecutor(PingCommand())
        getCommand("suicide")?.setExecutor(SuicideCommand())
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
        getPluginManager().registerEvents(JoinAndQuitListener(), this)
    }
}