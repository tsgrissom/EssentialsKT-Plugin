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
        const val PERM_SELF = "essentials.clearchat"
        const val PERM_ALL = "essentials.clearchat.all"
        const val PERM_OTHERS = "essentials.clearchat.others"
        const val PERM_EXEMPT = "essentials.clearchat.exemptall"
    }

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfiguration() : FileConfiguration = getPlugin().config
    private fun getConfiguredRepeatCount() : Int =
        getConfiguration().getInt("Features.ClearChatRepeatBlankLine", 500)

    private fun clearChat(t: Player, repeat: Int = 500) {
        repeat(repeat) {
            t.sendMessage("")
        }
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

    private fun handleTargeted(context: CommandContext) {
        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(PERM_OTHERS))
            return context.sendNoPermission(sender, PERM_OTHERS)

        val sub = args[0]
        val t: Player = Bukkit.getPlayer(sub)
            ?: return sender.sendColored("&4Could not find player &c\"$sub\"")

        clearChat(t, getConfiguredRepeatCount())

        if (t != sender)
            sender.sendColored("&6You cleared &c${t.name}'s &6chat messages")

        // TODO Accept multiple named targets with corresponding chat completion
    }

    override fun execute(context: CommandContext) {
        val args = context.args

        if (args.isEmpty())
            return handleEmptyArgs(context)

        val sub = args[0]

        if (sub.equalsIc("all"))
            return handleSubcAll(context)

        handleTargeted(context)
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
            val sub = args[0]
            if (len == 1 && !suggestPlayers.contains(sub))
                StringUtil.copyPartialMatches(sub, suggestPlayers, tab)
        }

        return tab.sorted().toMutableList()
    }
}