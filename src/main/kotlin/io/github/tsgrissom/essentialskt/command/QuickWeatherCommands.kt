package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.*
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.ChatColor.DARK_RED as D_RED
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

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
        "${GOLD}You set world ${RED}${w.name}'s ${GOLD}weather to ${RED}$weatherName"

    abstract fun setWeather(w: World)

    override fun execute(context: CommandContext) {
        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(WeatherCommand.PERM))
            return context.sendNoPermission(sender, WeatherCommand.PERM)

        var world = sender.getCurrentWorldOrDefault()

        if (args.isNotEmpty()) {
            val sub = args[0]
            world = Bukkit.getWorld(sub)
                ?: return sender.sendMessage("${D_RED}Could not find world ${RED}\"$sub\"")
        }

        val message = getWeatherSetMessage(world)

        setWeather(world)
        sender.sendMessage(message)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> {

        val tab = mutableListOf<String>()

        if (sender.lacksPermission(WeatherCommand.PERM))
            return tab

        val len = args.size

        if (len > 0) {
            if (len == 1)
                StringUtil.copyPartialMatches(args[0], getWorldNamesToMutableList(), tab)
        }

        return tab.sorted().toMutableList()
    }
}