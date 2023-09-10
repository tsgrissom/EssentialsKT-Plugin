package io.github.tsgrissom.testpluginkt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class SuicideCommand : CommandBase() {

    override fun execute(context: CommandContext) {
        val sender = context.sender

        if (sender.lacksPermission("essentials.command.suicide"))
            return sender.sendColored("&4You do not have permission to do that")

        if (sender is ConsoleCommandSender)
            return sender.sendColored("&4Console cannot kill itself")
        if (sender !is Player)
            return

        val p: Player = sender
        p.damage(Double.MAX_VALUE)
    }
}