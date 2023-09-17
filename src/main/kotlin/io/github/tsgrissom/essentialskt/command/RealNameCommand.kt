package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import io.github.tsgrissom.pluginapi.extension.stripColor
import io.github.tsgrissom.pluginapi.extension.translateAndStripColorCodes
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class RealNameCommand : CommandBase() {

    companion object {
        const val PERM = "essentials.realname"
    }

    override fun execute(context: CommandContext) {
        val args = context.args
        val label = context.label
        val sender = context.sender

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        if (args.isEmpty())
            return sender.sendColored("&4Usage: &c/$label <Target>")

        val nickname = args[0]
        val t: Player = getPlayerByDisplayName(nickname)
            ?: return sender.sendColored("&4Could not find player by nickname &c\"$nickname\"")

        sender.sendColored("&7Nickname &r\"${t.displayName}&r\"'s &7real username is &e${t.name}")
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> {
        val tab = mutableListOf<String>()

        if (sender.hasPermission(PERM)) {
            val nicknames = getOnlineDisplayNames()
            val len = args.size

            if (len == 1) {
                StringUtil.copyPartialMatches(args[0], nicknames, tab)
            }
        }

        return tab.sorted().toMutableList()
    }

    private fun getOnlineDisplayNames() : MutableList<String> =
        Bukkit.getOnlinePlayers().map { it.displayName }.toMutableList()

    private fun getPlayerByDisplayName(s: String) : Player? {
        val players = Bukkit.getOnlinePlayers()

        for (p in players) {
            if (p.displayName.stripColor() == s.translateAndStripColorCodes())
                return p
        }

        return null
    }
}