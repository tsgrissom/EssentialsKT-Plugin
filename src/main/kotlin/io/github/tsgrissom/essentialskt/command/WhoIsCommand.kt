package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.essentialskt.misc.EssPlayer
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.equalsIc
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendChatComponents
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class WhoIsCommand : CommandBase() {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()

    // MARK: Static Declarations
    companion object {
        const val PERM_SELF = "essentialskt.whoami"
        const val PERM_OTHERS = "essentialskt.whois"
        const val PERM_IP = "essentialskt.whois.ip"
    }

    // MARK: Command Body
    override fun execute(context: CommandContext) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)

        val label = context.label
        val sender = context.sender

        when (label) {
            "whoami", "ewhoami" -> handleWhoAmI(context)
            "whois", "ewhois" -> handleWhoIs(context)
            else -> {
                sender.sendMessage("${ccErr}Unknown ${ccErrDetl}/whois ${ccErr}command label (this should not happen)")
                error("Unhandled command label \"$label\" was passed to /whois handler")
            }
        }
    }

    // MARK: Operational Helper Functions
    private fun displayTemporaryWhoIs(sender: CommandSender, target: Player) {
        val essTarget = EssPlayer(target)
        sender.sendChatComponents(
            essTarget.generateTemporaryAttributesList(
                withHeader=true
            )
        )
    }

    private fun displaySemipermanentWhoIs(sender: CommandSender, target: Player) {
        val essTarget = EssPlayer(target)
        sender.sendChatComponents(
            essTarget.generateSemipermanentAttributesList(
                withHeader=true,
                excludeIp=sender.lacksPermission(PERM_IP)
            )
        )
    }

    private fun displayPermanentWhoIs(sender: CommandSender, target: Player) {
        val essTarget = EssPlayer(target)
        sender.sendChatComponents(
            essTarget.generatePermanentAttributesList(
                withHeader=true
            )
        )
    }

    private fun displayWhoIs(sender: CommandSender, target: Player) {
        val essTarget = EssPlayer(target)
        sender.sendChatComponents(
            essTarget.generatePermanentAttributesList(
                withHeader=true
            )
        )
        sender.sendChatComponents(
            essTarget.generateSemipermanentAttributesList(
                withHeader=false,
                excludeIp=sender.lacksPermission(PERM_IP)
            )
        )
        sender.sendChatComponents(
            essTarget.generateTemporaryAttributesList(
                withHeader=false
            )
        )
    }

    // MARK: Handlers
    private fun handleWhoAmI(context: CommandContext) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)

        val args = context.args
        val label = args.size
        val sender = context.sender

        if (sender.lacksPermission(PERM_SELF))
            return context.sendNoPermission(sender, PERM_SELF)

        if (sender is ConsoleCommandSender)
            return sender.sendMessage("${ccErr}Console Usage: ${ccErrDetl}/whois <Target>")

        if (sender !is Player)
            return

        if (args.isEmpty()) {
            displayWhoIs(sender, sender)
        } else {
            val sub = args[0]

            if (sub.equalsIc(KEYS_SUBC_HELP))
                return sender.sendMessage("${ccErr}Usage: ${ccErrDetl}/$label [temporary,permanent]")

            when (sub.lowercase()) {
                "temporary", "temp", "t" -> displayTemporaryWhoIs(sender, sender)
                "semipermanent", "semi", "s" -> displaySemipermanentWhoIs(sender, sender)
                "permanent", "perm", "p" -> displayPermanentWhoIs(sender, sender)
                else -> displayWhoIs(sender, sender)
            }
        }
    }

    private fun handleWhoIs(context: CommandContext) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)

        val args = context.args
        val label = context.label
        val len = args.size
        val sender = context.sender

        if (args.isEmpty())
            return sender.sendMessage("${ccErr}Usage: ${ccErrDetl}/whois <Target>")

        if (sender.lacksPermission(PERM_OTHERS))
            return context.sendNoPermission(sender, PERM_OTHERS)

        val sub = args[0]
        val t = Bukkit.getPlayer(sub)
            ?: return sender.sendMessage("${ccErr}Could not find player ${ccErrDetl}\"$sub\"")

        if (len == 1) {
            displayWhoIs(sender, t)
        } else {
            val arg1 = args[1]

            if (arg1.equalsIc(KEYS_SUBC_HELP_OR_USAGE))
                return sender.sendMessage("${ccErr}Usage: ${ccErrDetl}/$label <Target> [temporary,permanent]")

            when (arg1.lowercase()) {
                "temporary", "temp", "t" -> displayTemporaryWhoIs(sender, t)
                "semipermanent", "semi", "s" -> displaySemipermanentWhoIs(sender, t)
                "permanent", "perm", "p" -> displayPermanentWhoIs(sender, t)
                else -> displayWhoIs(sender, t)
            }
        }
    }

    // MARK: Tab Completion Handler
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> {
        val len = args.size
        val tab = mutableListOf<String>()
        val options = listOf("temporary", "semipermanent", "permanent", "help")

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
}