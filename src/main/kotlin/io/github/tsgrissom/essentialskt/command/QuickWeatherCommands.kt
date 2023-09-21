package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.*
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

    companion object {
        const val PERM_ALL = "essentials.weather"
    }

    private fun getWeatherSetMessage(w: World) =
        "&6You set world &c${w.name}'s &6weather to &c$weatherName"

    abstract fun setWeather(w: World)

    override fun execute(context: CommandContext) {
        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(PERM_ALL))
            return context.sendNoPermission(sender, PERM_ALL)

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

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> {

        val tab = mutableListOf<String>()

        if (sender.lacksPermission(PERM_ALL))
            return tab

        val len = args.size

        if (len > 0) {
            if (len == 1)
                StringUtil.copyPartialMatches(args[0], getWorldNamesToMutableList(), tab)
        }

        return tab.sorted().toMutableList()
    }
}