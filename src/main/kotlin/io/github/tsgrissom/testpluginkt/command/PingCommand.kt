package io.github.tsgrissom.testpluginkt.command

import org.bukkit.ChatColor
import org.bukkit.command.ConsoleCommandSender

class PingCommand : CommandBase() {

    override fun execute(context: CommandContext) {
        val sender = context.sender

        if (sender is ConsoleCommandSender) {
            sender.sendMessage("Ping!")
            return
        }

        sender.sendMessage(ChatColor.DARK_AQUA.toString() + "Ping!" + ChatColor.RESET.toString())
    }
}