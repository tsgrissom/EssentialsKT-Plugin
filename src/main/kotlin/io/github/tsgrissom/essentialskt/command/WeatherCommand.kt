package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.*
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.CommandSender

class WeatherCommand : CommandBase() {

    val perm = "essentials.weather"

    private fun getHelpText() : Array<String> =
        arrayOf(
            "   &6Command Help for &e/weather",
            "&6&l> &7/wthr clear",
            "&6&l> &7/wthr display",
            "&6&l> &7/wthr rain",
            "&6&l> &7/wthr thunder"
        )
    private fun sendHelp(sender: CommandSender) =
        getHelpText().forEach { sender.sendColored(it) }

    override fun execute(context: CommandContext) {
        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(perm))
            return context.sendNoPermission(sender, perm)

        if (args.isEmpty())
            return sendHelp(sender)

        val sub = args[0]

        when (sub.lowercase()) {
            "help", "h", "?" -> sendHelp(sender)
            "clear", "clr" -> delegateSubcClear(context)
            "display", "d", "info", "i" -> delegateSubcDisplay(context)
            "rain" -> delegateSubcRain(context)
            "thunder" -> delegateSubcThunder(context)
            else -> sender.sendColored("&4Unknown subcommand &c\"$sub\"&4. Do &c/wthr ? &4for help.")
        }
    }

    private fun delegateSubcClear(context: CommandContext) {
        val args = context.args
        val sender = context.sender
        var w: World = sender.getCurrentWorldOrDefault()

        if (args.size > 1) {
            val arg1 = args[1]
            w = Bukkit.getWorld(arg1)
                ?: return sender.sendColored("&4Unknown world &c\"${arg1}\"")
        }

        w.clearRain()
        sender.sendColored("&6Weather cleared for world &c${w.name}")
    }

    private fun delegateSubcDisplay(context: CommandContext) {
        val args = context.args
        val sender = context.sender
        var w: World = sender.getCurrentWorldOrDefault()

        if (args.size > 1) {
            val arg1 = args[1]
            w = Bukkit.getWorld(arg1)
                ?: return sender.sendColored("&4Unknown world &c\"${arg1}\"")
        }

        val wTicks = w.weatherDuration
        val cTicks = w.clearWeatherDuration
        val wSecs = wTicks / 20.0
        val cSecs = cTicks / 20.0

        sender.sendColored("&8&l> &7Is Clear: &r${w.isClearWeather.palatable(withColor=true)}")
        sender.sendColored("&8&l> &7Weather Remaining: &e$wTicks ticks &7or &e${wSecs.roundToDigits(1)} seconds")
        sender.sendColored("&8&l> &7Clear Remaining: &e$cTicks ticks &7or &e${cSecs.roundToDigits(1)} seconds")
    }

    private fun delegateSubcRain(context: CommandContext) {
        val args = context.args
        val sender = context.sender
        var w: World = sender.getCurrentWorldOrDefault()

        if (args.size > 1) {
            val arg1 = args[1]
            w = Bukkit.getWorld(arg1)
                ?: return sender.sendColored("&4Unknown world &c\"${arg1}\"")
        }

        w.makeRain()
        sender.sendColored("&6Rain turned on for world &c${w.name}")
    }

    private fun delegateSubcThunder(context: CommandContext) {
        // TODO Implement thunder
        context.sender.sendColored("&6Feature is in progress")
    }
}