package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.misc.EssPlayer
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.equalsIc
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class WhoIsCommand : CommandBase() {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getAfkManager() = getPlugin().afkManager

    private val permSelf = "essentials.whoami"
    private val permOthers = "essentials.whois"
    private val permIp = "essentials.whois.ip"

    override fun execute(context: CommandContext) {
        val label = context.label
        val sender = context.sender

        when (label) {
            "whoami", "ewhoami" -> handleWhoAmI(context)
            "whois", "ewhois" -> handleWhoIs(context)
            else -> sender.sendColored("&4Unknown /whois subcommand (error)")
        }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val len = args.size
        val tab = mutableListOf<String>()
        val options = listOf("temporary", "permanent", "help")

        if (label.equalsIc("whoami")) {
            if (len == 1)
                StringUtil.copyPartialMatches(args[0], options, tab)

            return tab
        } else {
            if (len == 1 && sender.hasPermission(permOthers))
                StringUtil.copyPartialMatches(args[0], getOnlinePlayerNamesToMutableList(), tab)
            if (len == 2)
                StringUtil.copyPartialMatches(args[1], options, tab)
        }

        return tab.sorted().toMutableList()
    }

    private fun handleWhoAmI(context: CommandContext) {
        val args = context.args
        val label = args.size
        val sender = context.sender

        if (sender.lacksPermission(permSelf))
            return context.sendNoPermission(sender, permSelf)

        if (sender is ConsoleCommandSender)
            return sender.sendColored("&4Console Usage: &c/whois <Target>")

        if (sender !is Player)
            return

        if (args.isEmpty()) {
            displayWhoIs(sender, sender)
        } else {
            val sub = args[0]

            when (sub.lowercase()) {
                "temporary", "temp" -> displayTemporaryWhoIs(sender, sender)
                "permanent", "perm" -> displayPermanentWhoIs(sender, sender)
                "help", "h", "?", "usage" -> sender.sendColored("&4Usage: &c/$label [temporary,permanent]")
                else -> displayWhoIs(sender, sender)
            }
        }
    }

    private fun handleWhoIs(context: CommandContext) {
        val args = context.args
        val label = context.label
        val len = args.size
        val sender = context.sender

        if (args.isEmpty())
            return sender.sendColored("&4Usage: &c/whois <Target>")

        if (sender.lacksPermission(permOthers))
            return context.sendNoPermission(sender, permOthers)

        val sub = args[0]
        val t = Bukkit.getPlayer(sub)
            ?: return sender.sendColored("&4Could not find player &c\"$sub\"")

        if (len == 1) {
            displayWhoIs(sender, t)
        } else {
            val arg1 = args[1]

            when (arg1.lowercase()) {
                "temporary", "temp" -> displayTemporaryWhoIs(sender, t)
                "permanent", "perm" -> displayPermanentWhoIs(sender, t)
                "help", "h", "?", "usage" -> sender.sendColored("&4Usage: &c/$label <Target> [temporary,permanent]")
                else -> displayWhoIs(sender, t)
            }
        }
    }

    private fun displayTemporaryWhoIs(sender: CommandSender, target: Player) {
        val essP = EssPlayer(target)
        sender.spigot().sendMessage(
            *essP.generateTemporaryAttributesList(
                withHeader=true,
                isAfk=getAfkManager().isAfk(target)
            )
        )
    }

    private fun displayPermanentWhoIs(sender: CommandSender, target: Player) {
        val essP = EssPlayer(target)
        sender.spigot().sendMessage(
            *essP.generatePermanentAttributesList(
                withHeader=true,
                excludeIp=sender.lacksPermission(permIp)
            )
        )
    }

    private fun displayWhoIs(sender: CommandSender, target: Player) {
        val essP = EssPlayer(target)
        sender.spigot().sendMessage(
            *essP.generatePermanentAttributesList(
                withHeader=true,
                excludeIp=sender.lacksPermission(permIp)
            )
        )
        sender.spigot().sendMessage(
            *essP.generateTemporaryAttributesList(
                withHeader=false,
                isAfk=getAfkManager().isAfk(target)
            )
        )
    }
}