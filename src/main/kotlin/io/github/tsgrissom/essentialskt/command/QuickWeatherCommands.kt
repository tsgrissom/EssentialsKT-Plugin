package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.*
import org.bukkit.Bukkit
import org.bukkit.World

class RainCommand
    : QuickWeatherCommand("Rain") {
    override fun setWeather(w: World) = w.makeRain()
}

class ClearWeatherCommand
    : QuickWeatherCommand("Clear") {
    override fun setWeather(w: World) = w.clearRain()
}

abstract class QuickWeatherCommand(private val weatherName: String) : CommandBase() {

    private fun getWeatherSetMessage(w: World) =
        "&6You set world &c${w.name}'s &6weather to &c$weatherName"

    abstract fun setWeather(w: World)

    override fun execute(context: CommandContext) {
        val perm = "essentials.weather"
        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(perm))
            return context.sendNoPermission(sender, perm)

        var world = sender.getCurrentWorldOrDefault()

        if (args.isNotEmpty()) {
            val sub = args[0]
            world = Bukkit.getWorld(sub)
                ?: return sender.sendColored("&4Could not find world &c\"$sub\"")
        }

        val message = getWeatherSetMessage(world)

        setWeather(world)
        sender.sendColored(message)
    }
}