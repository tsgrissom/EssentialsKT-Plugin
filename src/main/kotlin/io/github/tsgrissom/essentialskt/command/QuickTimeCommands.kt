package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.ChatColor.DARK_RED as D_RED
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class DayCommand
    : QuickTimeCommand(TimeCommand.TIME_DAY, "Day")
class NoonCommand
    : QuickTimeCommand(TimeCommand.TIME_NOON, "Noon")
class SunsetCommand
    : QuickTimeCommand(TimeCommand.TIME_SUNSET, "Dusk")
class NightCommand
    : QuickTimeCommand(TimeCommand.TIME_NIGHT, "Night")
class MidnightCommand
    : QuickTimeCommand(TimeCommand.TIME_MIDNIGHT, "Midnight")
class SunriseCommand
    : QuickTimeCommand(TimeCommand.TIME_SUNRISE, "Dawn")

private fun getTimeSetMessage(w: World, tn: String) =
    "${GOLD}You set world ${RED}${w.name}'s ${GOLD}time to ${RED}$tn"

open class QuickTimeCommand(
    private val time: Long,
    private val timeName: String
) : CommandBase() {

    override fun execute(context: CommandContext) {
        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(TimeCommand.PERM_SET))
            return context.sendNoPermission(sender, TimeCommand.PERM_SET)

        var world = Bukkit.getWorlds()[0]

        if (args.isNotEmpty()) {
            val sub = args[0]
            world = Bukkit.getWorld(sub)
                ?: return sender.sendMessage("${D_RED}Could not find world ${RED}\"$sub\"")
        }

        if (TimeCommand.lacksPermissionToSetWorldTime(sender, world))
            return context.sendNoPermission(sender, TimeCommand.getTimeSetPerWorldPermission(world))

        val setMessage = getTimeSetMessage(world, timeName)

        world.time = time
        sender.sendMessage(setMessage)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ):  MutableList<String> {
        val tab = mutableListOf<String>()

        if (sender.lacksPermission(TimeCommand.PERM_SET))
            return tab

        if (args.size == 1)
            StringUtil.copyPartialMatches(args[0], getWorldNamesToMutableList(), tab)

        return tab.sorted().toMutableList()
    }
}