package io.github.tsgrissom.pluginapi.command

import io.github.tsgrissom.pluginapi.extension.equalsIc
import io.github.tsgrissom.pluginapi.extension.sendColored
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandContext(
    val sender: CommandSender,
    val command: Command,
    val label: String,
    val args: Array<out String>
    ) {

    fun noPermission() : String = "&4You do not have permission to do that"
    fun sendNoPermission(sender: CommandSender, permission: String) {
        var resp = noPermission()
        if (sender.hasPermission("essentials.disclosepermission")) {
            resp += " (&c$permission&4)"
        }
        sender.sendColored(resp)
    }

    fun hasFlag(pair: Pair<String, String>) : Boolean =
        hasFlag(pair.first, pair.second)
    fun hasFlag(long: String, short: String) : Boolean {
        if (args.isEmpty())
            return false

        val longFlag = "--$long"
        val shortFlag = "-$short"

        for (a in args) {
            if (a.equalsIc(longFlag, shortFlag))
                return true
        }

        return false
    }
}