package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.misc.EssPlayer
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.equalsIc
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendChatComponents
import net.md_5.bungee.api.ChatColor.RED
import net.md_5.bungee.api.ChatColor.DARK_RED as D_RED
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class WhoIsCommand : CommandBase() {

    companion object {
        const val PERM_SELF = "essentialskt.whoami"
        const val PERM_OTHERS = "essentialskt.whois"
        const val PERM_IP = "essentialskt.whois.ip"
    }

    override fun execute(context: CommandContext) {
        val label = context.label
        val sender = context.sender

        when (label) {
            "whoami", "ewhoami" -> handleWhoAmI(context)
            "whois", "ewhois" -> handleWhoIs(context)
            else -> {
                sender.sendMessage("${D_RED}Unknown ${RED}/whois ${D_RED}command label (this should not happen)")
                error("Unhandled command label \"$label\" was passed to /whois handler")
            }
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> {
        val len = args.size
        val tab = mutableListOf<String>()
        val options = listOf("temporary", "permanent", "help")

        if (label.equalsIc("whoami", "ewhoami") && len == 1)
            StringUtil.copyPartialMatches(args[0], options, tab)
        else {
            if (len == 1 && sender.hasPermission(PERM_OTHERS))
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

        if (sender.lacksPermission(PERM_SELF))
            return context.sendNoPermission(sender, PERM_SELF)

        if (sender is ConsoleCommandSender)
            return sender.sendMessage("${D_RED}Console Usage: ${RED}/whois <Target>")

        if (sender !is Player)
            return

        if (args.isEmpty()) {
            displayWhoIs(sender, sender)
        } else {
            val sub = args[0]

            if (sub.equalsIc(KEYS_SUBC_HELP))
                return sender.sendMessage("${D_RED}Usage: ${RED}/$label [temporary,permanent]")

            when (sub.lowercase()) {
                "temporary", "temp" -> displayTemporaryWhoIs(sender, sender)
                "permanent", "perm" -> displayPermanentWhoIs(sender, sender)
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
            return sender.sendMessage("${D_RED}Usage: ${RED}/whois <Target>")

        if (sender.lacksPermission(PERM_OTHERS))
            return context.sendNoPermission(sender, PERM_OTHERS)

        val sub = args[0]
        val t = Bukkit.getPlayer(sub)
            ?: return sender.sendMessage("${D_RED}Could not find player ${RED}\"$sub\"")

        if (len == 1) {
            displayWhoIs(sender, t)
        } else {
            val arg1 = args[1]

            if (arg1.equalsIc(KEYS_SUBC_HELP_OR_USAGE))
                return sender.sendMessage("${D_RED}Usage: ${RED}/$label <Target> [temporary,permanent]")

            when (arg1.lowercase()) {
                "temporary", "temp" -> displayTemporaryWhoIs(sender, t)
                "permanent", "perm" -> displayPermanentWhoIs(sender, t)
                else -> displayWhoIs(sender, t)
            }
        }
    }

    private fun displayTemporaryWhoIs(sender: CommandSender, target: Player) {
        val essTarget = EssPlayer(target)
        sender.sendChatComponents(
            essTarget.generateTemporaryAttributesList(
                withHeader=true
            )
        )
    }

    private fun displayPermanentWhoIs(sender: CommandSender, target: Player) {
        val essTarget = EssPlayer(target)
        sender.sendChatComponents(
            essTarget.generatePermanentAttributesList(
                withHeader=true,
                excludeIp=sender.lacksPermission(PERM_IP)
            )
        )
    }

    private fun displayWhoIs(sender: CommandSender, target: Player) {
        val essTarget = EssPlayer(target)
        sender.sendChatComponents(
            essTarget.generatePermanentAttributesList(
                withHeader=true,
                excludeIp=sender.lacksPermission(PERM_IP)
            )
        )
        sender.sendChatComponents(
            essTarget.generateTemporaryAttributesList(
                withHeader=false
            )
        )
    }
}