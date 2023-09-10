package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.lacksPermission

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
}