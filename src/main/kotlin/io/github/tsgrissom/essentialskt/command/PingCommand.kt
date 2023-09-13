package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class PingCommand : CommandBase() {

    override fun execute(context: CommandContext) {
        val sender = context.sender
        val label = context.label
        val perm = "essentials.ping"

        if (sender.lacksPermission(perm))
            return context.sendNoPermission(sender, perm)

        val resp = when (label) {
            "pong", "epong" -> "Ping!"
            else -> "Pong!"
        }

        sender.sendMessage(resp)
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        // TODO Implement /ping tab completion
        return mutableListOf()
    }
}