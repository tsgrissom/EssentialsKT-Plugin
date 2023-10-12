package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.*
import io.github.tsgrissom.pluginapi.chat.ClickableText
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.ChatColor.DARK_GRAY as D_GRAY
import net.md_5.bungee.api.ChatColor.DARK_RED as D_RED
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class UniqueIdCommand : CommandBase() {

    companion object {
        const val PERM = "essentialskt.uniqueid"
    }

    override fun execute(context: CommandContext) {
        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        val t: Player = if (args.size == 1) {
            val sub = args[0]
            Bukkit.getPlayer(sub)
                ?: return sender.sendMessage("${D_RED}Could not find player ${RED}\"${sub}\"")
        } else {
            if (sender is ConsoleCommandSender)
                return sender.sendMessage("${D_RED}Console Usage: ${RED}/uuid <Player>")
            if (sender !is Player)
                return

            sender
        }

        sender.sendChatComponents(generateDisplayUniqueIdAsTextComponent(t))
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

        val len = args.size

        if (len == 1) {
            StringUtil.copyPartialMatches(args[0], getOnlinePlayerNamesToMutableList(), tab)
        }

        return tab.sorted().toMutableList()
    }

    private fun generateDisplayUniqueIdAsTextComponent(t: Player) : Array<BaseComponent> {
        val uuid = t.getUniqueString()
        val data = ClickableText
            .compose(uuid)
            .color(YELLOW)
            .action(ClickEvent.Action.COPY_TO_CLIPBOARD)
            .value(uuid)
            .toComponent()
        val builder = ComponentBuilder()
            .appendc(" ---------------------------------------\n", D_GRAY)
            .appendc(" | ", D_GRAY).appendc("UUID of ", GRAY).appendc(t.name, YELLOW).append("\n")
            .appendc(" | ", D_GRAY).appendc("> ", GOLD).bold(true).append(data).bold(false).append("\n").reset()
            .appendc(" ---------------------------------------", D_GRAY)

        return builder.create()
    }
}