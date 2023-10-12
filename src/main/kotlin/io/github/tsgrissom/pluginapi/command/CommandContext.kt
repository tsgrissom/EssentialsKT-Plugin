package io.github.tsgrissom.pluginapi.command

import io.github.tsgrissom.pluginapi.extension.equalsIc
import net.md_5.bungee.api.ChatColor.RED
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission
import net.md_5.bungee.api.ChatColor.DARK_RED as D_RED

class CommandContext(
    val sender: CommandSender,
    val command: Command,
    val label: String,
    val args: Array<out String>
) {

    fun sendNoPermission(sender: CommandSender, permission: String) {
        var resp = "${D_RED}You do not have access to that command."
        if (sender.hasPermission("essentialskt.disclosepermission"))
            resp += " ${D_RED}(${RED}$permission${D_RED})"
        sender.sendMessage(resp)
    }

    fun hasPermission(permission: String) =
        sender.hasPermission(permission)
    fun hasPermission(permission: Permission) =
        sender.hasPermission(permission)
    fun lacksPermission(permission: String) =
        !sender.hasPermission(permission)
    fun lacksPermission(permission: Permission) =
        !sender.hasPermission(permission)

    fun hasFlag(pair: Pair<String, String>) : Boolean =
        hasFlag(pair.first, pair.second)
    fun hasFlag(long: String, short: String) : Boolean {
        if (args.isEmpty())
            return false

        val longFlag = "--$long"
        val shortFlag = "-$short"

        for (a in args)
            if (a.equalsIc(longFlag, shortFlag))
                return true

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

        for (i in startIndex..endIndex)
            builder += " ${args[i]}"

        return builder.trim()
    }

    fun getExecutedString(withLabel: Boolean = true) : String {
        val len = args.size
        val startIndex = 0
        val endIndex = if (len > 0) args.size - 1 else 0
        return getExecutedString(withLabel=withLabel, startIndex, endIndex)
    }

    fun performCommand(command: String) {
        when (sender) {
            is ConsoleCommandSender -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
            is Player -> sender.performCommand(command)
            else -> {
                throw IllegalStateException("Cannot performCommand for a CommandSender who is neither Player nor Console")
            }
        }
    }
}