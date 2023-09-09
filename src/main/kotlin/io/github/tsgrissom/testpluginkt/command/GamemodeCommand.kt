package io.github.tsgrissom.testpluginkt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.GameMode
import org.bukkit.GameMode.*
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

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
                return sender.sendMessage(
                    "${DARK_RED}Console Usage: ${RED}/${label} <adventure|creative|survival|spectator>"
                )

            sender
        } else {
            val targetName = args[0]
            val p = Bukkit.getPlayer(targetName)
                ?: return sender.sendMessage("${DARK_RED}Could not find player $RED$targetName")

            p
        }

        when (label) {
            "gma", "gm2" -> {
                setGameMode(target, ADVENTURE)
                target.sendMessage("${GOLD}Your gamemode is set to ${RED}Adventure${RESET}")
            }
            "gmc", "gm1" -> {
                setGameMode(target, CREATIVE)
                target.sendMessage("${GOLD}Your gamemode is set to ${RED}Creative${RESET}")
            }
            "gms", "gm0" -> {
                setGameMode(target, SURVIVAL)
                target.sendMessage("${GOLD}Your gamemode is set to ${RED}Survival${RESET}")
            }
            "gmsp" -> {
                setGameMode(target, SPECTATOR)
                target.sendMessage("${GOLD}Your gamemode is set to ${RED}Spectator${RESET}")
            }
        }
    }

    private fun handleExtendedLabel(context: CommandContext) {
        val label = context.label
        val sender = context.sender
        val args = context.args

        if (args.isEmpty())
            return sender.sendMessage(
                "${RED}Usage: /gm <adventure|creative|survival|spectator> [Target]"
            )

        val sub = args[0]

        val target: Player = if (args.size == 1) {
            if (sender !is Player)
                return sender.sendMessage(
                    "${DARK_RED}Console Usage: ${RED}/${label} <adventure|creative|survival|spectator> <Target>"
                )

            sender
        } else {
            val targetName = args[1]
            val p = Bukkit.getPlayer(targetName)
                ?: return sender.sendMessage("${DARK_RED}Could not find player $RED$targetName")

            p
        }

        val mode: GameMode = when (sub.lowercase()) {
            "adventure", "adv", "a", "2" -> ADVENTURE
            "survival", "surv", "sur", "s", "0" -> SURVIVAL
            "creative", "creat", "crt", "crtv", "c", "1" -> CREATIVE
            "spectator", "spect", "spec", "sp" -> SPECTATOR
            else -> return sender.sendMessage("${DARK_RED}Options: ${RED}adventure, creative, survival, spectator")
        }

        val targetName = target.name
        val modeName = getGameModeName(mode)

        setGameMode(target, mode)

        if (sender != target)
            sender.sendMessage("${GOLD}You set $RED$targetName's ${GOLD}gamemode to $RED$modeName")

        target.sendMessage("${GOLD}Your gamemode has been set to $RED$modeName")
    }

    override fun execute(context: CommandContext) {
        val sender = context.sender
        val label = context.label

        if (sender is ConsoleCommandSender) {
            sender.sendMessage("Console does not have a gamemode to alter")
            return
        }

        if (sender !is Player)
            return

        when (label.lowercase()) {
            "gma", "gmc", "gms", "gmsp" -> handleShorthandLabel(context)
            "gamemode", "gm" -> handleExtendedLabel(context)
            else -> sender.sendMessage("${RED}Alternate gamemode command form detected")
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

    private fun setGameMode(p: Player, mode: GameMode) {
        p.gameMode = mode
    }
}