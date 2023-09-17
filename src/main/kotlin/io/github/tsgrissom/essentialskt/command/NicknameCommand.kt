package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.*
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class NicknameCommand : CommandBase() {

    /*
     * EssentialsX Nick-Related Permissions
     * - essentials.nick
     * - essentials.nick.<color>
     * - essentials.nick.blacklist.bypass
     * - essentials.nick.changecolors
     * - essentials.nick.changecolors.bypass
     * - essentials.nick.rgb
     * - essentials.nick.others
     * - essentials.nick.magic
     * - essentials.nick.format
     */

    companion object {
        const val PERM_BASE = "essentials.nick"
        const val PERM_OTHERS = "essentials.nick.others"
        const val PERM_BYPASS_BLACKLIST = "essentials.nick.blacklist.bypass" // TODO Implement blacklist
        const val PERM_ALL_COLORS = "essentials.nick.color"
        const val PERM_VIEW = "essentials.nick.view"
    }

    private fun getHelpText() : Array<String> =
        arrayOf(
            "   &6Command Help for &e/nickname",
            "&8&l> &e/nick <NewDisplayName> [Target] &8- &7Set a player's nickname",
            "&8&l> &e/nick reset [Target] &8- &7Reset a player's nickname",
            "&8&l> &e/nick of [Target] &8- &7View a player's nickname"
        )

    override fun execute(context: CommandContext) {
        val usage = "&4Usage: &c/nick <NewDisplayName> [Target]"

        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(PERM_BASE))
            return context.sendNoPermission(sender, PERM_BASE)

        if (args.isEmpty())
            return sender.sendColored(usage)

        val sub = args[0]

        val t: Player = if (args.size == 1) {
            if (sender is ConsoleCommandSender)
                return sender.sendColored("&4Console Usage: &c/nick <NewDisplayName> <Target>")
            if (sender !is Player)
                return
            sender
        } else {
            val arg1 = args[1]
            val p = Bukkit.getPlayer(arg1)
                ?: return sender.sendColored("&4Could not find player &c${arg1}")
            p
        }

        val tn = t.name

        if (t == sender && sender.lacksPermission(PERM_BASE))
            return context.sendNoPermission(sender, PERM_BASE)
        if (t != sender && sender.lacksPermission(PERM_OTHERS))
            return context.sendNoPermission(sender, PERM_OTHERS)

        if (sub.equalsIc("reset", "remove")) {
            t.setDisplayName(t.name)
            if (t == sender)
                sender.sendColored("&6Your nickname has been reset")
            else
                sender.sendColored("&6You have reset &c${tn}'s &6nickname")
            return
        } else if (sub.equalsIc("view", "of")) {
            if (sender.lacksPermission(PERM_VIEW))
                return context.sendNoPermission(sender, PERM_VIEW)

            val dn = t.displayName
            if (dn == tn)
                sender.sendColored("&c${tn} &6does not have a nickname")
            else
                sender.sendColored("&c${tn}'s &6nickname is &r${dn}")
            return
        } else if (sub.equalsIc("help", "h", "?")) {
            return getHelpText().forEach { sender.sendColored(it) }
        }

        val newNickname: String = transformNickname(context, sub, t)
            ?: return

        t.setDisplayName(newNickname)

        if (sender != t)
            sender.sendColored("&6You set &c${tn}'s &6nickname to &r${newNickname}")
        t.sendColored("&6Your new nickname is &r${newNickname}")
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val suggestSub = mutableListOf<String>()
        val tab = mutableListOf<String>()
        val len = args.size

        if (sender.hasPermission(PERM_BASE))
            suggestSub.addAll(arrayOf("reset", "view", "help"))

        if (len > 0) {
            val sub = args[0]

            if (len == 1) {
                StringUtil.copyPartialMatches(sub, suggestSub, tab)
            } else if (len == 2) {
                if (sender.hasPermission(PERM_OTHERS))
                    StringUtil.copyPartialMatches(args[1], getOnlinePlayerNamesToMutableList(), tab)
            }
        }

        return tab.sorted().toMutableList()
    }

    private fun transformNickname(context: CommandContext, input: String, target: Player) : String? {
        val sender = context.sender

        if (sender.lacksPermission(PERM_ALL_COLORS) && input.containsChatColor()) {
            context.sendNoPermission(sender, PERM_ALL_COLORS)
            return null
        }

        val new = if (input.isOnlyColorCodes())
            input + target.name
        else
            input

        return new.translateColor()
    }
}