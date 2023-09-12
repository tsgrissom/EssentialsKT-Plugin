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

    val permBase = "essentials.nick"
    val permOthers = "essentials.nick.others"
    val permBlacklistBypass = "essentials.nick.blacklist.bypass"
    val permAllColors = "essentials.nick.color"

    private fun transformNickname(context: CommandContext, input: String) : String {
        val sender = context.sender
        if (sender.lacksPermission(permAllColors) && input.containsChatColor()) {
            context.sendNoPermission(sender, permAllColors)
            return input.translateAndStripColorCodes()
        }

        return input.translateColor()
    }

    override fun execute(context: CommandContext) {
        val usage = "&4Usage: &c/nick <Target> <NewDisplayName>"

        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(permBase))
            return context.sendNoPermission(sender, permBase)

        if (args.isEmpty() || args.size == 1)
            return sender.sendColored(usage)

        val sub = args[0]
        val arg1 = args[1]

        val t = if (sub.equalsIc("self")) {
            if (sender is ConsoleCommandSender)
                return sender.sendColored("&4Console does not have a nickname to set")
            if (sender !is Player)
                return

            sender
        } else {
            Bukkit.getPlayer(sub)
                ?: return sender.sendColored("&4Could not find player \"$sub\"")
        }

        val tn = t.name

        if (t == sender && sender.lacksPermission(permBase))
            return context.sendNoPermission(sender, permBase)
        if (t != sender && sender.lacksPermission(permOthers))
            return context.sendNoPermission(sender, permOthers)

        if (arg1.equalsIc("reset")) {
            t.setDisplayName(t.name)
            sender.sendColored("&6You reset &c${tn}'s &6nickname to their username")
            return
        }

        val newNickname = transformNickname(context, arg1)

        t.setDisplayName(newNickname)

        if (sender != t)
            sender.sendColored("&6You set &c${tn}'s &6nickname to &r${newNickname}")
        t.sendColored("&6Your new nickname is &r${newNickname}")
    }
}