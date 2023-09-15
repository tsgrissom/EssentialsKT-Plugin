package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.gui.PlayerListGui
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.equalsIc
import io.github.tsgrissom.pluginapi.extension.getUniqueString
import io.github.tsgrissom.pluginapi.extension.roundToDigits
import io.github.tsgrissom.pluginapi.extension.sendColored
import io.github.tsgrissom.pluginapi.misc.HoverableText
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class ListCommand : CommandBase() {

    private fun getAvailableLists() : Array<String> =
        arrayOf(
            "&6Available Lists",
            "&bOptional flag available to players",
            "&8&l> &emobs",
            "&8&l> &eplayers &b--gui"
        )

    private fun handleSubcPlayers(context: CommandContext) {
        val sender = context.sender
        val guiFlag = Pair("gui", "g")
        val hasGuiFlag = context.hasFlag(guiFlag)

        if (sender is ConsoleCommandSender) {
            handleSubcPlayersText(context)

            if (hasGuiFlag)
                sender.sendColored("&4Console cannot view GUIs")

            return
        }
        if (sender !is Player) {
            return
        }

        return if (hasGuiFlag)
            PlayerListGui().show(sender)
        else
            handleSubcPlayersText(context)
    }

    private fun createPlayerListAsTextComponents() : Array<BaseComponent> {
        val onlinePlayers = Bukkit.getOnlinePlayers()
        val builder = ComponentBuilder()
            .append("Players").color(ChatColor.GRAY)

        if (onlinePlayers.isNotEmpty()) {
            builder
                .append(" (").color(ChatColor.DARK_GRAY)
                .append("${onlinePlayers.size} online").color(ChatColor.GOLD)
                .append(")").color(ChatColor.DARK_GRAY)
        }

        builder.append(" ")

        for ((index, p) in onlinePlayers.withIndex()) {
            val loc = p.location
            val x = loc.x.roundToDigits(1)
            val y = loc.y.roundToDigits(1)
            val z = loc.z.roundToDigits(1)
            builder.append(
                HoverableText
                    .compose("&e${p.displayName}")
                    .hoverText(
                        "&8&l> &7Username&8: &e${p.name}",
                        "&8&l> &7Display Name&8: &r${p.displayName}\n",
                        "&8&l> &7UUID&8: &e${p.getUniqueString()}\n",
                        "&8&l> &7Current World&8: &e${p.world.name}\n",
                        "&8&l> &7Location &cX&aY&bZ&8: &c$x &a$y &b$z"
                    )
                    .toTextComponent()
            )
            if (index != (onlinePlayers.size - 1))
                builder.append(" ")
        }

        return builder.create()
    }

    private fun handleSubcPlayersText(context: CommandContext) {
        val sender = context.sender

        sender.spigot().sendMessage(*createPlayerListAsTextComponents())
    }

    private fun handleSubcMobs(context: CommandContext) {
        context.sender.sendMessage("TODO Display mods as text")
    }

    override fun execute(context: CommandContext) {
        val args = context.args
        val sender = context.sender

        if (args.isEmpty())
            return getAvailableLists().forEach { sender.sendColored(it) }

        val sub = args[0]

        when (sub.lowercase()) {
            "players", "online" -> handleSubcPlayers(context)
            "mobs", "mob" -> handleSubcMobs(context)
            else -> sender.sendColored("&4Unknown list type &c\"$sub\"&4. Do &c/ls &4to view valid types.")
        }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val suggestSub = mutableListOf("players", "online", "mobs")
        val suggestPlayersArg1 = mutableListOf("--gui", "-g")
        val tab = mutableListOf<String>()

        val len = args.size

        if (len > 0) {
            val sub = args[0]

            if (len == 1) {
                if (!suggestSub.contains(sub)) {
                    StringUtil.copyPartialMatches(sub, suggestSub, tab)
                }
            } else if (len == 2) {
                if (sub.equalsIc("players", "online")) {
                    StringUtil.copyPartialMatches(args[1], suggestPlayersArg1, tab)
                }
            }
        }

        return tab.sorted().toMutableList()
    }
}