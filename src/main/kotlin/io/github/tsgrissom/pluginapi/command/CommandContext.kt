package io.github.tsgrissom.pluginapi.command

import io.github.tsgrissom.pluginapi.extension.equalsIc
import io.github.tsgrissom.pluginapi.extension.sendColored
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class CommandContext(
    val sender: CommandSender,
    val command: Command,
    val label: String,
    val args: Array<out String>
    ) {

    companion object {
        const val TEXT_PERMISSION = "&4You do not have access to that command."
    }

    fun sendNoPermission(sender: CommandSender, permission: String) {
        var resp = TEXT_PERMISSION
        if (sender.hasPermission("essentialskt.disclosepermission")) {
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

    fun getExecutedString(withLabel: Boolean = true, startIndex: Int, endIndex: Int) : String {
        var builder = String()

        if (withLabel)
            builder += label

        if (args.isEmpty())
            return builder

        if (startIndex > args.size)
            error("startIndex is out of bounds of arguments")
        if (endIndex > args.size)
            error("endIndex is out of bounds of arguments")

        for (i in startIndex..endIndex) {
            builder += " ${args[i]}"
        }

        return builder.trim()
    }

    fun getExecutedString(withLabel: Boolean = true) : String {
        val len = args.size
        val startIndex = 0
        val endIndex = if (len > 0) args.size - 1 else 0
        return getExecutedString(withLabel=withLabel, startIndex, endIndex)
    }

    fun performCommand(command: String) {
        if (sender is ConsoleCommandSender)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
        else if (sender is Player)
            sender.performCommand(command)
    }


}