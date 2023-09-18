package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.*
import io.github.tsgrissom.pluginapi.misc.ClickableText
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class IPAddressCommand : CommandBase() {

    companion object {
        const val PERM = "essentials.ipaddress"
    }

    override fun execute(context: CommandContext) {
        val args = context.args
        val label = context.label
        val sender = context.sender

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        if (args.isEmpty())
            return sender.sendColored("&4Usage: &c/$label <Target>")

        val sub = args[0]
        val t: Player = Bukkit.getPlayer(sub)
            ?: return sender.sendColored("&4Could not find player &c\"$sub\"")

        sender.sendChatComponents(generateDisplayIPAddressAsTextComponent(t))
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        val tab = mutableListOf<String>()
        val len = args.size

        if (len == 1) {
            StringUtil.copyPartialMatches(args[0], getOnlinePlayerNamesToMutableList(), tab)
        }

        return tab.sorted().toMutableList()
    }

    private fun generateDisplayIPAddressAsTextComponent(t: Player) : Array<BaseComponent> {
        val data = ClickableText
            .compose(t.getIPString())
            .action(ClickEvent.Action.COPY_TO_CLIPBOARD)
            .value(t.getIPString())
            .toTextComponent()
        val builder = ComponentBuilder()
            .appendc(" ---------------------------------------\n", DARK_GRAY)
            .appendc(" | ", DARK_GRAY).appendc("IP Address of ", GRAY).appendc(t.name, YELLOW).append("\n")
            .appendc(" | ", DARK_GRAY).appendc("> ", GOLD).bold(true).appendc(data, YELLOW).bold(false).append("\n").reset()
            .appendc(" ---------------------------------------", DARK_GRAY)

        return builder.create()
    }
}