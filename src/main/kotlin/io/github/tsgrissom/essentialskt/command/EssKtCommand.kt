package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.conversation.prompt.WorldNamePrompt
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.command.help.CommandHelpGenerator
import io.github.tsgrissom.pluginapi.command.help.SubcommandHelp
import io.github.tsgrissom.pluginapi.extension.equalsIc
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendChatComponents
import net.md_5.bungee.api.ChatColor.RED
import net.md_5.bungee.api.ChatColor.DARK_RED as D_RED
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.conversations.Conversable
import org.bukkit.conversations.ConversationFactory

class EssKtCommand : CommandBase() {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("Plugin not instantiated!")

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

    private fun printVersion(context: CommandContext) {
        val version = getPlugin().description.version
        context.sender.sendMessage("EssentialsKT version: $version")
    }

    override fun execute(context: CommandContext) {
        val sender = context.sender

        if (context.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        val args = context.args
        val len = context.args.size

        if (len == 0 || (len == 1 && args[0].equalsIc(KEYS_SUBC_HELP)))
            return sender.sendChatComponents(getHelpAsComponents(context))

        val sub = args[0]

        if (sub.equalsIc("version", "v")) {
            return printVersion(context)
        } else if (sub.equalsIc("test")) {
            val convF = ConversationFactory(getPlugin())
            val conv = convF
                .thatExcludesNonPlayersWithMessage("${RED}Console cannot use this conversation")
                .withTimeout(10)
                .withEscapeSequence("exit")
                .withModality(true)
                .withFirstPrompt(WorldNamePrompt())
                .buildConversation(sender as Conversable)
            sender.beginConversation(conv)
        } else {
            return sender.sendMessage("${D_RED}Unknown subcommand: ${RED}\"$sub\" ${D_RED}Do ${RED}/esskt ${D_RED}for help.")
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

        tab.addAll(listOf("test", "version"))

        return tab.sorted().toMutableList()
    }
}