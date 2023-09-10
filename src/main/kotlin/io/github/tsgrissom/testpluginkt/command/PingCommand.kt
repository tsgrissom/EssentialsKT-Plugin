package io.github.tsgrissom.testpluginkt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored

class PingCommand : CommandBase() {

    override fun execute(context: CommandContext) {
        val sender = context.sender
        val label = context.label

        if (sender.lacksPermission("essentials.command.ping"))
            return sender.sendColored("&4You do not have permission to do that")

        val resp = when (label) {
            "pong", "epong" -> "Ping!"
            else -> "Pong!"
        }

        sender.sendMessage(resp)
    }
}