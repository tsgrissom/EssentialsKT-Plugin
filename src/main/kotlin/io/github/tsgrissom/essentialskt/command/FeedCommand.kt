package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class FeedCommand : CommandBase() {

    private fun handleEmptyArgs(context: CommandContext) {
        val label = context.label
        val sender = context.sender

        if (sender is ConsoleCommandSender)
            return sender.sendColored("&4Console Usage: &c/$label <Target>")
        if (sender !is Player)
            return

        val perm = "essentials.feed"
        if (sender.lacksPermission(perm))
            return context.sendNoPermission(sender, perm)

        feed(sender, sender)
    }

    private fun handleArgs(context: CommandContext) {
        val args = context.args
        val sender = context.sender
        val sub = args[0]

        val target: Player = Bukkit.getPlayer(sub)
            ?: return sender.sendColored("&4Could not find player &c$sub")

        val permSelf = "essentials.feed"
        val permOthers = "essentials.feed.others"
        if (target == sender && sender.lacksPermission(permSelf))
            return context.sendNoPermission(sender, permSelf)
        if (target != sender && sender.lacksPermission(permOthers))
            return context.sendNoPermission(sender, permOthers)

        feed(sender, target)
    }

    override fun execute(context: CommandContext) {
        val args = context.args

        if (args.isEmpty())
            handleEmptyArgs(context)
        else
            handleArgs(context)
    }

    private fun feed(sender: CommandSender, target: Player) {
        val tn = target.name

        target.foodLevel = 20

        if (sender != target)
            sender.sendColored("&6You fed &c$tn")
        target.sendColored("&6Your hunger was sated")
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        // TODO Implement /feed tab completion
        return mutableListOf()
    }
}