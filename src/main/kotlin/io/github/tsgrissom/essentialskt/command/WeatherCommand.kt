package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.*
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class WeatherCommand : CommandBase() {

    private val perm = "essentials.weather"

    private fun getHelpText(context: CommandContext) : Array<String> {
        val label = context.label
        return arrayOf(
            "  &6Command Help &8-> &e/${label}",
            "&6> &7Aliases&8: &eweather&8, &ewthr",
            "&6> &7Parameters&8: &c<Required> &7& &a[Optional]",
            " &8/&e${label} clear &a[World]&8:",
            "     &7Clear world weather conditions",
            " &8/&e${label} display &a[World]&8:",
            "     &7Display world weather statistics",
            " &8/&e${label} rain &a[World]&8:",
            "     &7Enable rain for a world",
            " &8/&e${label} thunder &a[World]&8:",
            "     &7Enable thunderstorms for a world"
        )
    }
    private fun sendHelp(context: CommandContext) =
        getHelpText(context).forEach { context.sender.sendColored(it) }

    override fun execute(context: CommandContext) {
        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(perm))
            return context.sendNoPermission(sender, perm)

        if (args.isEmpty())
            return sendHelp(context)

        val sub = args[0]

        when (sub.lowercase()) {
            "help", "h", "?" -> sendHelp(context)
            "clear", "clr" -> delegateSubcClear(context)
            "display", "d", "info", "i" -> delegateSubcDisplay(context)
            "rain" -> delegateSubcRain(context)
            "thunder" -> delegateSubcThunder(context)
            else -> sender.sendColored("&4Unknown subcommand &c\"$sub\"&4. Do &c/wthr ? &4for help.")
        }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val suggestSub = mutableListOf("help", "clear", "display", "rain", "thunder")
        val tab = mutableListOf<String>()
        val len = args.size

        if (sender.lacksPermission(perm))
            return tab

        if (len > 0) {
            val sub = args[0]

            if (len == 1) {
                StringUtil.copyPartialMatches(sub, suggestSub, tab)
            } else if (len == 2) {
                if (sub.equalsIc("clear", "display", "rain", "thunder"))
                    StringUtil.copyPartialMatches(args[1], getWorldNamesToMutableList(), tab)
            }
        }

        return tab.sorted().toMutableList()
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