package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.command.help.CommandHelpGenerator
import io.github.tsgrissom.pluginapi.command.help.SubcommandHelp
import io.github.tsgrissom.pluginapi.extension.equalsIc
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendChatComponents
import net.md_5.bungee.api.ChatColor.GREEN
import net.md_5.bungee.api.ChatColor.RED
import net.md_5.bungee.api.ChatColor.DARK_RED as D_RED
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

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
        } else if (sub.equalsIc("testany")) { // TODO Remove
            val hasAnyQuotedString = context.getAnyQuotedString()
            if (hasAnyQuotedString == null) {
                sender.sendMessage("${RED}No quoted String found (console)")
            } else {
                sender.sendMessage("Quoted string found=${hasAnyQuotedString}")
            }
        } else if (sub.equalsIc("testrange")) { // TODO Remove
            if (len == 1)
                return sender.sendMessage("${RED}Not enough args")

            val args1 = args[1]
            if (!args1.contains(":"))
                return sender.sendMessage("${RED}Not a range!")

            val split = args1.split(":")
            if (split.size != 2)
                return sender.sendMessage("${RED}Should split into 2!")

            val first = split[0].toIntOrNull()
                ?: return sender.sendMessage("${RED}${split[0]} is not an int")
            val second = split[1].toIntOrNull()
                ?: return sender.sendMessage("${RED}${split[1]} is not an int")

            if (first > args.size)
                return sender.sendMessage("${RED}startIndex is greater than args size")
            if (second > args.size)
                return sender.sendMessage("${RED}endIndex is greater than args size")
            if (second < first)
                return sender.sendMessage("{${RED}endIndex is less than startIndex")

            val rangeStr = "args[${first}..${second}]"
            val executedStr = context.getExecutedString(withLabel=false, first, second)
            sender.sendMessage("Selected $rangeStr ranged String=${executedStr}")
            val result = context.getQuotedStringFromArgsRange(first, second)
                ?: return sender.sendMessage("${RED}Selected is not a quoted String")
            val (quotedString, startIndex, endIndex) = result
            sender.sendMessage(
                "${GREEN}Quoted String found",
                "Quoted: $quotedString",
                "Range: $startIndex..$endIndex",
                "Dequoted: $result",
                "Quotation Mark: ${result.getQuotationMark()}",
                "Contains Floating Quote Marks: ${result.containsFloatingQuotationMarks()}"
            )
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