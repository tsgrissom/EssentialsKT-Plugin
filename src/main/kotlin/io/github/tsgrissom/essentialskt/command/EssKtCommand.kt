package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.conversation.prompt.WorldNamePrompt
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.command.help.CommandHelpGenerator
import io.github.tsgrissom.pluginapi.command.help.SubcommandHelp
import io.github.tsgrissom.pluginapi.extension.equalsIc
import io.github.tsgrissom.pluginapi.extension.sendChatComponents
import io.github.tsgrissom.pluginapi.extension.sendColored
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

        if (len == 0 || (len == 1 && args[0].equalsIc(VALID_HELP_KEYS)))
            return sender.sendChatComponents(getHelpAsComponents(context))

        val sub = args[0]

        if (sub.equalsIc("version", "v")) {
            return printVersion(context)
        } else if (sub.equalsIc("test")) {
            val convF = ConversationFactory(getPlugin())
            val conv = convF
                .withTimeout(10)
                .withModality(true)
                .withFirstPrompt(WorldNamePrompt())
                .buildConversation(sender as Conversable)
            sender.beginConversation(conv)
        } else {
            return sender.sendColored("&4Unknown subcommand: &c\"$sub\" &4Do &c/esskt &4for help.")
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> {
        val tab = mutableListOf<String>()



        return tab.sorted().toMutableList()
    }
}