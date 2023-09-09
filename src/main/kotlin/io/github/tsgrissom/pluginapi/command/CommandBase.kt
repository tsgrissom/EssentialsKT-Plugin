package io.github.tsgrissom.pluginapi.command

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

abstract class CommandBase : CommandExecutor {

    abstract fun execute(context: CommandContext)

    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        if (sender == null || command == null || label == null || args == null) {
            Bukkit.getLogger().warning("CommandBase command \"$label\" returned false!")
            Bukkit.getLogger().warning("SHOULDN'T HAPPEN")
            return false
        }

        val context = CommandContext(sender=sender, command=command, label=label, args=args)

        execute(context)

        return true
    }
}