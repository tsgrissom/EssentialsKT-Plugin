package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class PingCommand : CommandBase() {

    companion object {
        const val PERM = "essentials.ping"
    }

    override fun execute(context: CommandContext) {
        val sender = context.sender
        val label = context.label

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        val resp = when (label) {
            "pong", "epong" -> "Ping!"
            else -> "Pong!"
        }

        sender.sendColored("&6$resp")
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> = mutableListOf()
}