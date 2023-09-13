package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class AfkCommand : CommandBase() {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getAfkManager() = getPlugin().afkManager

    override fun execute(context: CommandContext) {
        val perm = "essentials.afk"
        val sender = context.sender

        if (sender.lacksPermission(perm))
            return context.sendNoPermission(sender, perm)

        if (sender is ConsoleCommandSender)
            return sender.sendColored("&4Console cannot be AFK")
        if (sender !is Player)
            return

        val p: Player = sender

        getAfkManager().toggleAfk(p)
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        // TODO Implement /afk tab completion
        return mutableListOf()
    }
}