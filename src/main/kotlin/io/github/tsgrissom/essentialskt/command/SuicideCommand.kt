package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class SuicideCommand : CommandBase() {

    companion object {
        const val PERM = "essentials.suicide"
    }

    override fun execute(context: CommandContext) {
        val sender = context.sender

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        if (sender is ConsoleCommandSender)
            return sender.sendColored("&4Console cannot kill itself")
        if (sender !is Player)
            return

        sender.damage(Double.MAX_VALUE)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> = mutableListOf()
}