package io.github.tsgrissom.testpluginkt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
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

const val PERMISSION_ALTER_SELF = "essentials.command.gamemode"
const val PERMISSION_ALTER_OTHER = "essentials.command.other"
const val PERMISSION_DEFENSIVE = "essentials.command.gamemode.noalter"

class GamemodeCommand : CommandBase() {

    private fun handleShorthandLabel(context: CommandContext) {
        /*
         * /gmc - Target self (sender must be player)
         * /gmc [TargetPlayer] - Target another (sender can be anyone)
         */

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

        if ((target == sender && sender.lacksPermission(PERMISSION_ALTER_SELF))
            || (target != sender && sender.lacksPermission(PERMISSION_ALTER_OTHER)))
            return sender.sendColored("&4You do not have permission to do that")

        when (label) {
            "gma", "gm2" -> {
                setGameMode(sender, target, ADVENTURE)
                target.sendColored("&6Your gamemode is set to &cAdventure")
            }
            "gmc", "gm1" -> {
                setGameMode(sender, target, CREATIVE)
                target.sendColored("&6Your gamemode is set to &cCreative")
            }
            "gms", "gm0" -> {
                setGameMode(sender, target, SURVIVAL)
                target.sendColored("&6Your gamemode is set to &cSurvival")
            }
            "gmsp" -> {
                setGameMode(sender, target, SPECTATOR)
                target.sendColored("&6Your gamemode is set to &cSpectator")
            }
        }
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

        val mode: GameMode = when (sub.lowercase()) {
            "adventure", "adv", "a", "2" -> ADVENTURE
            "survival", "surv", "sur", "s", "0" -> SURVIVAL
            "creative", "creat", "crt", "crtv", "c", "1" -> CREATIVE
            "spectator", "spect", "spec", "sp" -> SPECTATOR
            else -> return sender.sendColored("&4Options: &cadventure, creative, survival, spectator")
        }

        if ((target == sender && sender.lacksPermission(PERMISSION_ALTER_SELF))
            || (target != sender && sender.lacksPermission(PERMISSION_ALTER_OTHER)))
            return sender.sendColored("&4You do not have permission to do that")

        val targetName = target.name
        val modeName = getGameModeName(mode)

        setGameMode(sender, target, mode)

        if (sender != target)
            sender.sendColored("&6You set &c$targetName's &6gamemode to &c$modeName")

        target.sendColored("&6Your gamemode has been set to &c$modeName")
    }

    override fun execute(context: CommandContext) {
        val sender = context.sender
        val label = context.label

        if (sender is ConsoleCommandSender)
            return sender.sendColored("&4Console does not have a gamemode to alter")

        if (sender !is Player)
            return

        when (label.lowercase()) {
            "gma", "gmc", "gms", "gmsp" -> handleShorthandLabel(context)
            "gamemode", "gm" -> handleExtendedLabel(context)
            else -> sender.sendColored("&4Alternate gamemode command form detected")
        }
    }

    private fun getGameModeName(mode: GameMode) : String {
        return when (mode) {
            ADVENTURE -> "Adventure"
            CREATIVE -> "Creative"
            SPECTATOR -> "Spectator"
            SURVIVAL -> "Survival"
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