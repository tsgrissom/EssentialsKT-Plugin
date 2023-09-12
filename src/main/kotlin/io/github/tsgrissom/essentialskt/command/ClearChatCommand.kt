package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.equalsIc
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import org.bukkit.Bukkit
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class ClearChatCommand : CommandBase() {

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
        val perm = "essentials.clearchat"
        val sender = context.sender

        if (sender.lacksPermission(perm))
            return context.sendNoPermission(sender, perm)

        if (sender is ConsoleCommandSender)
            return sender.sendColored("&4Console Usage: &c/cls <Target OR all>")
        if (sender !is Player)
            return

        clearChat(sender, getConfiguredRepeatCount())
    }

    private fun handleSubcAll(context: CommandContext) {
        val perm = "essentials.clearchat.all"
        val sender = context.sender

        if (sender.lacksPermission(perm))
            return context.sendNoPermission(sender, perm)

        Bukkit.getOnlinePlayers()
            .filter { it.lacksPermission("essentials.clearchat.bypassall") }
            .forEach { clearChat(it, getConfiguredRepeatCount()) }
        sender.sendColored("&6You cleared the chat messages of all players on the server")
    }

    private fun handleTargeted(context: CommandContext) {
        val perm = "essentials.clearchat.others"
        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(perm))
            return context.sendNoPermission(sender, perm)

        val sub = args[0]
        val t: Player = Bukkit.getPlayer(sub)
            ?: return sender.sendColored("&4Could not find player &c\"$sub\"")

        clearChat(t, getConfiguredRepeatCount())
        sender.sendColored("&6You cleared &c${t.name}'s &6chat messages")

        // TODO Targeted
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
}