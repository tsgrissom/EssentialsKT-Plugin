package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.command.GameModeCommand.Companion.PERM_BASE
import io.github.tsgrissom.essentialskt.command.GameModeCommand.Companion.PERM_OTHERS
import io.github.tsgrissom.essentialskt.command.GameModeCommand.Companion.PERM_ADVENTURE
import io.github.tsgrissom.essentialskt.command.GameModeCommand.Companion.PERM_CREATIVE
import io.github.tsgrissom.essentialskt.command.GameModeCommand.Companion.PERM_SURVIVAL
import io.github.tsgrissom.essentialskt.command.GameModeCommand.Companion.PERM_SPECTATOR
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.essentialskt.misc.EssPlayer
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.kt.capitalizeAllCaps
import io.github.tsgrissom.pluginapi.extension.kt.equalsIc
import io.github.tsgrissom.pluginapi.extension.bukkit.lacksPermission
import io.github.tsgrissom.pluginapi.extension.bukkit.sendChatComponents
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.GameMode.*
import org.bukkit.util.StringUtil

class GmxCommand : CommandBase() {

    private fun getPlugin() =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()

    override fun execute(context: CommandContext) {
        val conf = getConfig()
        val ccDetl = conf.getChatColor(ChatColorKey.Detail)
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)

        val label = context.label
        val sender = context.sender
        val args = context.args

        val target: Player = if (args.isEmpty()) {
            if (sender is ConsoleCommandSender)
                return sender.sendMessage("${ccErr}Console Usage: ${ccErrDetl}/$label <Target>")
            else if (sender !is Player)
                return

            sender
        } else {
            val tn = args[0]
            val t = Bukkit.getPlayer(tn)
                ?: return sender.sendMessage("${ccErr}Could not find player ${ccErrDetl}$tn${ccErr}.")

            t
        }

        if (target == sender && sender.lacksPermission(PERM_BASE))
            return context.sendNoPermission(sender, PERM_BASE)
        if (target != sender && sender.lacksPermission(PERM_OTHERS))
            return context.sendNoPermission(sender, PERM_OTHERS)

        if (target is ConsoleCommandSender)
            return sender.sendMessage("${ccErr}Console does not have a gamemode to alter.")

        val eTarget = EssPlayer(target)

        if (label.equalsIc("gmt", "gmtoggle")) {
            eTarget.cycleGameMode(sender)
            return
        }

        val mode = when (label.lowercase()) {
            "gm0", "gms" -> {
                if (sender.lacksPermission(PERM_SURVIVAL))
                    return context.sendNoPermission(sender, PERM_SURVIVAL)
                SURVIVAL
            }
            "gm1", "gmc" -> {
                if (sender.lacksPermission(PERM_CREATIVE))
                    return context.sendNoPermission(sender, PERM_CREATIVE)
                CREATIVE
            }
            "gm2", "gma" -> {
                if (sender.lacksPermission(PERM_ADVENTURE))
                    return context.sendNoPermission(sender, PERM_ADVENTURE)
                ADVENTURE
            }
            "gmsp" -> {
                if (sender.lacksPermission(PERM_SPECTATOR))
                    return context.sendNoPermission(sender, PERM_SPECTATOR)
                SPECTATOR
            }
            else -> {
                val available = GameModeCommand.getAvailableGameModesAsComponent(sender, true)
                return sender.sendChatComponents(available)
            }
        }

        eTarget.setGameMode(sender, mode)

        val mn = mode.name.capitalizeAllCaps()
        val tn = target.name

        target.sendMessage("${ccPrim}Your gamemode is set to ${ccDetl}$mn${ccPrim}.")
        if (sender != target)
            sender.sendMessage("${ccPrim}You set ${ccDetl}$tn's ${ccPrim}gamemode to ${ccDetl}$mn${ccPrim}.")
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        val tab = mutableListOf<String>()

        if (args.size == 1 && sender.hasPermission(PERM_OTHERS))
            StringUtil.copyPartialMatches(args[0], getOnlinePlayerNamesToMutableList(), tab)

        return tab.sorted().toMutableList()
    }
}