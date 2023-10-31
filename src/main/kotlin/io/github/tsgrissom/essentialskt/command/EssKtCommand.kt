package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.enum.ChatColorKey
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.command.help.CommandHelpGenerator
import io.github.tsgrissom.pluginapi.command.help.SubcommandHelp
import io.github.tsgrissom.pluginapi.extension.equalsIc
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendChatComponents
import net.md_5.bungee.api.ChatColor.RED
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import net.md_5.bungee.api.ChatColor.DARK_RED as D_RED

class EssKtCommand : CommandBase() {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("Plugin not instantiated!")
    private fun getConfig() =
        getPlugin().getConfigManager()

    companion object {
        const val PERM = "essentialskt.essentialskt"
    }

    private fun getHelpAsComponents(context: CommandContext) : Array<BaseComponent> {
        val help = CommandHelpGenerator(context)

        help.withSubcommand(
            SubcommandHelp
                .compose("version")
                .withAliases("v")
                .withDescription("View plugin version")
        )

        return help.toComponents()
    }

    private fun handleSubcVersion(context: CommandContext) {
        val version = getPlugin().description.version
        context.sender.sendMessage("EssentialsKT version: $version")
    }

    private fun handleSubcReload(context: CommandContext) {
        val sender = context.sender
        val ccPrimary = getConfig().getChatColor(ChatColorKey.Primary)
        sender.sendMessage("${ccPrimary}In progress")
        // TODO Reload command
    }

    override fun execute(context: CommandContext) {
        val sender = context.sender
        val ccError = getConfig().getChatColor(ChatColorKey.Error)

        if (context.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        val args = context.args
        val len = context.args.size

        if (len == 0 || (len == 1 && args[0].equalsIc(KEYS_SUBC_HELP)))
            return sender.sendChatComponents(getHelpAsComponents(context))

        val sub = args[0]

        when (sub.lowercase()) {
            "version", "v" -> handleSubcVersion(context)
            "reload", "refresh" -> handleSubcReload(context)
            else -> {
                sender.sendMessage("${D_RED}Unknown subcommand: ${RED}\"$sub\" ${D_RED}Do ${RED}/esskt ${D_RED}for help.")
            }
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> {
        val tab = mutableListOf<String>()

        if (sender.lacksPermission(PERM))
            return tab

        tab.addAll(listOf("version"))

        return tab.sorted().toMutableList()
    }
}