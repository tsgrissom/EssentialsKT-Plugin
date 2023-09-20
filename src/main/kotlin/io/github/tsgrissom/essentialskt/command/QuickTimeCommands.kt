package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class DayCommand
    : QuickTimeCommand("essentials.time.quick.day", TimeCommand.TIME_DAY, "Day")
class NoonCommand
    : QuickTimeCommand("essentials.time.quick.noon", TimeCommand.TIME_NOON, "Noon")
class SunsetCommand
    : QuickTimeCommand("essentials.time.quick.sunset", TimeCommand.TIME_SUNSET, "Dusk")
class NightCommand
    : QuickTimeCommand("essentials.time.quick.night", TimeCommand.TIME_NIGHT, "Night")
class MidnightCommand
    : QuickTimeCommand("essentials.time.quick.midnight", TimeCommand.TIME_MIDNIGHT, "Midnight")
class SunriseCommand
    : QuickTimeCommand("essentials.time.quick.sunrise", TimeCommand.TIME_SUNRISE, "Dawn")

private fun getTimeSetMessage(w: World, tn: String) =
    "&6You set world &c${w.name}'s &6time to &c$tn"

open class QuickTimeCommand(
    private val permission: String,
    private val time: Long,
    private val timeName: String
) : CommandBase() {

    override fun execute(context: CommandContext) {
        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(permission))
            return context.sendNoPermission(sender, permission)

        var world = Bukkit.getWorlds()[0]

        if (args.isNotEmpty()) {
            val sub = args[0]
            world = Bukkit.getWorld(sub)
                ?: return sender.sendColored("&4Could not find world &c\"$sub\"")
        }

        val setMessage = getTimeSetMessage(world, timeName)

        world.time = time
        sender.sendColored(setMessage)
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        // TODO Quick time tab completion
        return mutableListOf()
    }
}