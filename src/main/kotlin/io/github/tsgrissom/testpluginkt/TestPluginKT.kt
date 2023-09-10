package io.github.tsgrissom.testpluginkt

import io.github.tsgrissom.testpluginkt.command.*
import io.github.tsgrissom.testpluginkt.command.DayCommand
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

import io.github.tsgrissom.testpluginkt.listener.ChatListener
import io.github.tsgrissom.testpluginkt.listener.JoinAndQuitListener

class TestPluginKT : JavaPlugin() {

    companion object {
        var instance: TestPluginKT? = null
        private set
    }

    override fun onEnable() {
        instance = this

        config.options().copyDefaults(true)
        saveDefaultConfig()

        getCommand("day").executor = DayCommand()
        getCommand("gamemode").executor = GamemodeCommand()
        getCommand("heal").executor = HealCommand()
        getCommand("midnight").executor = MidnightCommand()
        getCommand("night").executor = NightCommand()
        getCommand("noon").executor = NoonCommand()
        getCommand("ping").executor = PingCommand()
        getCommand("suicide").executor = SuicideCommand()
        getCommand("sunset").executor = SunsetCommand()
        getCommand("sunrise").executor = SunriseCommand()
        getCommand("whois").executor = WhoIsCommand()

        Bukkit.getPluginManager().registerEvents(ChatListener(), this)
        Bukkit.getPluginManager().registerEvents(JoinAndQuitListener(), this)
    }
}