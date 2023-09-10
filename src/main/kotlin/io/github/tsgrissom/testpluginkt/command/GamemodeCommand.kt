package io.github.tsgrissom.testpluginkt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.capitalizeAllCaps
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.GameMode.*
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

/*
 * TODO Per-gamemode permissions
 */

class GamemodeCommand : CommandBase() {

    val PERMISSION_ALTER_SELF = "essentials.gamemode"
    val PERMISSION_ALTER_OTHER = "essentials.gamemode.others"
    val PERMISSION_DEFENSIVE = "essentials.gamemode.noalter"

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

        if (target == sender && sender.lacksPermission(PERMISSION_ALTER_SELF))
            return context.sendNoPermission(sender, PERMISSION_ALTER_SELF)

        if (target != sender && sender.lacksPermission(PERMISSION_ALTER_OTHER))
            return context.sendNoPermission(sender, PERMISSION_ALTER_OTHER)

        if (target is ConsoleCommandSender)
            return sender.sendColored("&4Console does not have a gamemode to alter")

        val mode = when (label) {
            "gm0", "gms" -> {
                val perm = "essentials.gamemode.survival"
                if (sender.lacksPermission(perm))
                    return context.sendNoPermission(sender, perm)
                SURVIVAL
            }
            "gm1", "gmc" -> {
                val perm = "essentials.gamemode.creative"
                if (sender.lacksPermission(perm))
                    return context.sendNoPermission(sender, perm)
                CREATIVE
            }
            "gm2", "gma" -> {
                val perm = "essentials.gamemode.adventure"
                if (sender.lacksPermission(perm))
                    return context.sendNoPermission(sender, perm)
                ADVENTURE
            }
            "gmsp" -> {
                val perm = "essentials.gamemode.spectator"
                if (sender.lacksPermission(perm))
                    return context.sendNoPermission(sender, perm)
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

        if (target == sender && sender.lacksPermission(PERMISSION_ALTER_SELF))
            return context.sendNoPermission(sender, PERMISSION_ALTER_SELF)

        if (target != sender && sender.lacksPermission(PERMISSION_ALTER_OTHER))
            return context.sendNoPermission(sender, PERMISSION_ALTER_OTHER)

        if (target is ConsoleCommandSender)
            return sender.sendColored("&4Console does not have a gamemode to alter")


        val mode = when (sub.lowercase()) {
            "0", "survival", "surv", "sur", "s" -> {
                val perm = "essentials.gamemode.survival"
                if (sender.lacksPermission(perm))
                    return context.sendNoPermission(sender, perm)
                SURVIVAL
            }
            "1", "creative", "create", "creat", "crtv", "crt", "c" -> {
                val perm = "essentials.gamemode.creative"
                if (sender.lacksPermission(perm))
                    return context.sendNoPermission(sender, perm)
                CREATIVE
            }
            "2", "adventure", "adv", "a" -> {
                val perm = "essentials.gamemode.adventure"
                if (sender.lacksPermission(perm))
                    return context.sendNoPermission(sender, perm)
                ADVENTURE
            }
            "spectator", "spect", "spec", "sp" -> {
                val perm = "essentials.gamemode.spectator"
                if (sender.lacksPermission(perm))
                    return context.sendNoPermission(sender, perm)
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

    private fun setGameMode(sender: CommandSender, target: Player, mode: GameMode) {
        if (sender != target && target.hasPermission(PERMISSION_DEFENSIVE)) {
            val s = sender.name
            val t = target.name
            Bukkit.getLogger().info(
                "$s attempted to set ${t}'s gamemode but the target had \"$PERMISSION_DEFENSIVE\""
            )
            sender.sendColored("&4You are not able to set &c${t}'s &4gamemode")
            return
        }

        target.gameMode = mode
    }
}