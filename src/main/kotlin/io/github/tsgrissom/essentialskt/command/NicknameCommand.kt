package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.*
import org.bukkit.Bukkit
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

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
     */

    private val permBase = "essentials.nick"
    private val permOthers = "essentials.nick.others"
    private val permBlacklistBypass = "essentials.nick.blacklist.bypass" // TODO Implement blacklist
    private val permAllColors = "essentials.nick.color"

    private fun getHelpText() : Array<String> =
        arrayOf(
            "   &6Command Help for &e/nickname",
            "&8&l> &e/nick <NewDisplayName> [Target] &8- &7Set a player's nickname",
            "&8&l> &e/nick reset [Target] &8- &7Reset a player's nickname",
            "&8&l> &e/nick of [Target] &8- &7View a player's nickname"
        )

    private fun transformNickname(context: CommandContext, input: String, target: Player) : String {
        val sender = context.sender

        if (sender.lacksPermission(permAllColors) && input.containsChatColor()) {
            context.sendNoPermission(sender, permAllColors)
            return input.translateAndStripColorCodes()
        }

        val new = if (input.isOnlyColorCodes())
            input + target.name
        else
            input

        return new.translateColor()
    }

    override fun execute(context: CommandContext) {
        val usage = "&4Usage: &c/nick <NewDisplayName> [Target]"

        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(permBase))
            return context.sendNoPermission(sender, permBase)

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

        if (t == sender && sender.lacksPermission(permBase))
            return context.sendNoPermission(sender, permBase)
        if (t != sender && sender.lacksPermission(permOthers))
            return context.sendNoPermission(sender, permOthers)

        if (sub.equalsIc("reset", "remove")) {
            t.setDisplayName(t.name)
            if (t == sender)
                sender.sendColored("&6Your nickname has been reset")
            else
                sender.sendColored("&6You have reset &c${tn}'s &6nickname")
            return
        } else if (sub.equalsIc("view", "of")) {
            val dn = t.displayName
            if (dn == tn)
                sender.sendColored("&c${tn} &6does not have a nickname")
            else
                sender.sendColored("&c${tn}'s &6nickname is &r${dn}")
            return
        } else if (sub.equalsIc("help", "h", "?")) {
            return getHelpText().forEach { sender.sendColored(it) }
        }

        val newNickname = transformNickname(context, sub, t)

        t.setDisplayName(newNickname)

        if (sender != t)
            sender.sendColored("&6You set &c${tn}'s &6nickname to &r${newNickname}")
        t.sendColored("&6Your new nickname is &r${newNickname}")
    }
}