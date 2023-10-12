package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.equalsIc
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.ChatColor.DARK_RED as D_RED
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class ClearChatCommand : CommandBase() {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()
    private fun getConfiguredRepeatCount() = getConfig().getClearChatRepeatBlankLineCount()

    companion object {
        const val PERM_SELF   = "essentialskt.clearchat"
        const val PERM_ALL    = "essentialskt.clearchat.all"
        const val PERM_OTHERS = "essentialskt.clearchat.others"
        const val PERM_EXEMPT = "essentialskt.clearchat.exemptall"
    }

    private fun performClearChatOperation(
        t: Player,
        count: Int = getConfiguredRepeatCount()
    ) {
        repeat(count) {
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
            return sender.sendMessage("${D_RED}Console Usage: ${RED}/cls <Target OR all>")
        if (sender !is Player)
            return

        performClearChatOperation(sender)
    }

    private fun handleSubcAll(context: CommandContext) {
        val sender = context.sender

        if (sender.lacksPermission(PERM_ALL))
            return context.sendNoPermission(sender, PERM_ALL)

        Bukkit.getOnlinePlayers()
            .filter { it.lacksPermission(PERM_EXEMPT) }
            .forEach { performClearChatOperation(it, getConfiguredRepeatCount()) }
        sender.sendMessage("${GOLD}You cleared the chat messages of all players on the server")
    }

    private fun handleOneOrMoreArgs(context: CommandContext) {
        val args = context.args
        val sender = context.sender

        val clearedPlayers = mutableListOf<String>()

        for (i in 0..args.size) {
            val arg = args[i]
            val t: Player = Bukkit.getPlayer(arg)
                ?: return sender.sendMessage("${D_RED}Could not find player ${RED}\"$arg\"")
            val tn = t.name
            if (clearedPlayers.contains(tn)) {
                sender.sendMessage("${D_RED}You already cleared ${RED}${tn}'s ${D_RED}chat")
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

            performClearChatOperation(t)
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
            sender.sendMessage("${GOLD}You cleared ${RED}${who} ${GOLD}chat messages")
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