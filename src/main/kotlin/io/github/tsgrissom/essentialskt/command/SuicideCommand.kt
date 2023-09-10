package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class SuicideCommand : CommandBase() {

    override fun execute(context: CommandContext) {
        val sender = context.sender
        val perm = "essentials.suicide"

        if (sender.lacksPermission(perm))
            return context.sendNoPermission(sender, perm)

        if (sender is ConsoleCommandSender)
            return sender.sendColored("&4Console cannot kill itself")
        if (sender !is Player)
            return

        val p: Player = sender
        p.damage(Double.MAX_VALUE)
    }
}