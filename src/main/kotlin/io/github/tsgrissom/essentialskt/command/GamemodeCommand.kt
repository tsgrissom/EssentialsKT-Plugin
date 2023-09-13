package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.capitalizeAllCaps
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.GameMode.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

/*
 * TODO Per-gamemode permissions
 */

class GamemodeCommand : CommandBase() {

    private val permSelf = "essentials.gamemode"
    private val permOthers = "essentials.gamemode.others"
    private val permDefensive = "essentials.gamemode.noalter"
    private val permAdventure = "essentials.gamemode.adventure"
    private val permCreative = "essentials.gamemode.creative"
    private val permSurvival = "essentials.gamemode.survival"
    private val permSpectator = "essentials.gamemode.spectator"

    private fun handleShorthandLabel(context: CommandContext) {
        val label = context.label
        val sender = context.sender
        val args = context.args

        val target: Player = if (args.isEmpty()) {
            if (sender !is Player)
                return sender.sendColored(
                    "&4Console Usage: &c/$label <adventure|creative|survival|spectator>"
                )

            sender
        } else {
            val targetName = args[0]
            val p = Bukkit.getPlayer(targetName)
                ?: return sender.sendColored("&4Could not find player &c$targetName")

            p
        }

        if (target == sender && sender.lacksPermission(permSelf))
            return context.sendNoPermission(sender, permSelf)

        if (target != sender && sender.lacksPermission(permOthers))
            return context.sendNoPermission(sender, permOthers)

        if (target is ConsoleCommandSender)
            return sender.sendColored("&4Console does not have a gamemode to alter")

        val mode = when (label) {
            "gm0", "gms" -> {
                if (sender.lacksPermission(permSurvival))
                    return context.sendNoPermission(sender, permSurvival)
                SURVIVAL
            }
            "gm1", "gmc" -> {
                if (sender.lacksPermission(permCreative))
                    return context.sendNoPermission(sender, permCreative)
                CREATIVE
            }
            "gm2", "gma" -> {
                if (sender.lacksPermission(permAdventure))
                    return context.sendNoPermission(sender, permAdventure)
                ADVENTURE
            }
            "gmsp" -> {
                if (sender.lacksPermission(permSpectator))
                    return context.sendNoPermission(sender, permSpectator)
                SPECTATOR
            }
            else -> return sender.sendColored("&4Options: &cadventure, creative, survival, spectator")
        }

        setGameMode(sender, target, mode)

        val mn = mode.name.capitalizeAllCaps()
        val tn = target.name

        target.sendColored("&6Your gamemode is set to &c$mn")
        if (sender != target)
            sender.sendColored("&6You set &c$tn's &6gamemode to &c$mn")
    }

    private fun handleExtendedLabel(context: CommandContext) {
        val sender = context.sender
        val args = context.args

        if (args.isEmpty())
            return sender.sendColored(
                "&4Usage: &c/gm <adventure|creative|survival|spectator> [Target]"
            )

        val sub = args[0]

        val target: Player = if (args.size == 1) {
            if (sender !is Player)
                return sender.sendColored(
                    "&4Console Usage: &c/gm <adventure|creative|survival|spectator> <Target>"
                )

            sender
        } else {
            val targetName = args[1]
            val p = Bukkit.getPlayer(targetName)
                ?: return sender.sendColored("&4Could not find player &c$targetName")

            p
        }

        if (target == sender && sender.lacksPermission(permSelf))
            return context.sendNoPermission(sender, permSelf)
        if (target != sender && sender.lacksPermission(permOthers))
            return context.sendNoPermission(sender, permOthers)

        if (target is ConsoleCommandSender)
            return sender.sendColored("&4Console does not have a gamemode to alter")

        val mode = when (sub.lowercase()) {
            "0", "survival", "surv", "sur", "s" -> {
                if (sender.lacksPermission(permSurvival))
                    return context.sendNoPermission(sender, permSurvival)
                SURVIVAL
            }
            "1", "creative", "create", "creat", "crtv", "crt", "c" -> {
                if (sender.lacksPermission(permCreative))
                    return context.sendNoPermission(sender, permCreative)
                CREATIVE
            }
            "2", "adventure", "adv", "a" -> {
                if (sender.lacksPermission(permAdventure))
                    return context.sendNoPermission(sender, permAdventure)
                ADVENTURE
            }
            "spectator", "spect", "spec", "sp" -> {
                if (sender.lacksPermission(permSpectator))
                    return context.sendNoPermission(sender, permSpectator)
                SPECTATOR
            }
            else -> return sender.sendColored("&4Options: &cadventure, creative, survival, spectator")
        }

        setGameMode(sender, target, mode)

        val mn = mode.name.capitalizeAllCaps()
        val tn = target.name

        target.sendColored("&6Your gamemode has been set to &c$mn")
        if (sender != target)
            sender.sendColored("&6You set &c$tn's &6gamemode to &c$mn")
    }

    override fun execute(context: CommandContext) {
        val sender = context.sender
        val label = context.label

        when (label.lowercase()) {
            "gma", "gmc", "gms", "gmsp", "egma", "egmc", "egms", "egmsp", "gm0", "gm1", "gm2" -> handleShorthandLabel(context)
            "gamemode", "gm", "egamemode", "egm" -> handleExtendedLabel(context)
            else -> sender.sendColored("&4Alternate gamemode command form detected")
        }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        // TODO Implement /gamemode tab completion
        return mutableListOf()
    }

    private fun setGameMode(sender: CommandSender, target: Player, mode: GameMode) {
        if (sender != target && target.hasPermission(permDefensive)) {
            val s = sender.name
            val t = target.name
            Bukkit.getLogger().info(
                "$s attempted to set ${t}'s gamemode but the target had \"$permDefensive\""
            )
            sender.sendColored("&4You are not able to set &c${t}'s &4gamemode")
            return
        }

        target.gameMode = mode
    }
}