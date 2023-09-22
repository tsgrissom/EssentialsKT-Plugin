package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.equalsIc
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class ClearChatCommand : CommandBase() {

    companion object {
        const val PERM_SELF = "essentialskt.clearchat"
        const val PERM_ALL = "essentialskt.clearchat.all"
        const val PERM_OTHERS = "essentialskt.clearchat.others"
        const val PERM_EXEMPT = "essentialskt.clearchat.exemptall"
    }

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfiguration() : FileConfiguration = getPlugin().config
    private fun getConfiguredRepeatCount() : Int =
        getConfiguration().getInt("Commands.ClearChatRepeatBlankLine", 500)

    private fun clearChat(t: Player, repeat: Int = 500) {
        repeat(repeat) {
            t.sendMessage("")
        }
    }

    override fun execute(context: CommandContext) {
        val args = context.args

        if (args.isEmpty())
            return handleEmptyArgs(context)

        val sub = args[0]

        if (sub.equalsIc("all"))
            return handleSubcAll(context)

        handleOneOrMoreArgs(context)
    }

    private fun handleEmptyArgs(context: CommandContext) {
        val sender = context.sender

        if (sender.lacksPermission(PERM_SELF))
            return context.sendNoPermission(sender, PERM_SELF)

        if (sender is ConsoleCommandSender)
            return sender.sendColored("&4Console Usage: &c/cls <Target OR all>")
        if (sender !is Player)
            return

        clearChat(sender, getConfiguredRepeatCount())
    }

    private fun handleSubcAll(context: CommandContext) {
        val sender = context.sender

        if (sender.lacksPermission(PERM_ALL))
            return context.sendNoPermission(sender, PERM_ALL)

        Bukkit.getOnlinePlayers()
            .filter { it.lacksPermission(PERM_EXEMPT) }
            .forEach { clearChat(it, getConfiguredRepeatCount()) }
        sender.sendColored("&6You cleared the chat messages of all players on the server")
    }

    private fun handleOneOrMoreArgs(context: CommandContext) {
        val args = context.args
        val sender = context.sender

        val clearedPlayers = mutableListOf<String>()

        for (i in 0..args.size) {
            val arg = args[i]
            val t: Player = Bukkit.getPlayer(arg)
                ?: return sender.sendColored("&4Could not find player &c\"$arg\"")
            val tn = t.name
            if (clearedPlayers.contains(tn)) {
                sender.sendColored("&4You already cleared &c${tn}'s &4chat")
                continue
            }
            if (t == sender && sender.lacksPermission(PERM_SELF)) {
                context.sendNoPermission(sender, PERM_SELF)
                continue
            }
            if (t != sender && sender.lacksPermission(PERM_OTHERS)) {
                context.sendNoPermission(sender, PERM_OTHERS)
                continue
            }

            clearChat(t, getConfiguredRepeatCount())
            clearedPlayers.add(tn)
        }

        var self = false
        val howMany = clearedPlayers.size

        if (howMany == 0)
            return

        val who = if (howMany == 1) {
            val first = clearedPlayers[0]
            if (first == sender.name)
                self = true
            "${first}'s"
        } else {
            "$howMany players"
        }

        if (!self)
            sender.sendColored("&6You cleared &c${who} &6chat messages")
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> {

        val tab = mutableListOf<String>()
        val suggestPlayers =
            if (sender.hasPermission(PERM_ALL)) mutableListOf("all")
            else mutableListOf()

        if (sender.hasPermission(PERM_ALL))
            suggestPlayers.addAll(getOnlinePlayerNamesToMutableList())

        val len = args.size

        if (len > 0) {
            for (i in 0..len) {
                val arg = args[i]
                StringUtil.copyPartialMatches(arg, suggestPlayers, tab)
            }
        }

        return tab.sorted().toMutableList()
    }
}