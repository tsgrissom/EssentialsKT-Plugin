package io.github.tsgrissom.testpluginkt

import io.github.tsgrissom.testpluginkt.command.GamemodeCommand
import io.github.tsgrissom.testpluginkt.command.PingCommand
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    companion object {
        var instance: Main? = null
        private set
    }

    override fun onEnable() {
        getCommand("ping").executor = PingCommand()
        getCommand("gamemode").executor = GamemodeCommand()
        Bukkit.getLogger().info("Hello world! TestPluginKT is working.")
        instance = this
    }

    override fun onDisable() {
        Bukkit.getLogger().info("TestPluginKT is disabled.")
    }
}