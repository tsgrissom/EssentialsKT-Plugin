package io.github.tsgrissom.testpluginkt.command

import org.bukkit.ChatColor.*
import org.bukkit.GameMode
import org.bukkit.GameMode.*
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class GamemodeCommand : CommandBase() {

    private fun handleShorthandLabel(context: CommandContext) {
        val sender = context.sender
        val label = context.label

        if (sender !is Player)
            return

        val p: Player = sender

        when (label) {
            "gma", "gm2" -> {
                setGameMode(p, ADVENTURE)
                p.sendMessage("${GOLD}Your gamemode is set to ${RED}")
            }
            "gmc", "gm1" -> {
                setGameMode(p, CREATIVE)
                p.sendMessage("${GOLD}Your gamemode is set to ${RED}Creative${RESET}")
            }
            "gms", "gm0" -> {
                setGameMode(p, SURVIVAL)
                p.sendMessage("${GOLD}Your gamemode is set to ${RED}Survival${RESET}")
            }
            "gmsp" -> {
                setGameMode(p, SPECTATOR)
                p.sendMessage("${GOLD}Your gamemode is set to ${RED}Spectator${RESET}")
            }
        }
    }

    private fun handleExtendedLabel(context: CommandContext) {
        val sender = context.sender
        val args = context.args

        if (args == null)
            return sender.sendMessage("Null args for extended label")

        if (args.isEmpty())
            return sender.sendMessage("Args size is 0")

        val sub = args[0]
        var target: Player

        if (args.size > 1) {

        } else {

        }

        when (sub) {
            "adventure", "adv", "a", "2" -> {

            }
            "survival", "sur", "s", "0" -> {

            }
            "creative", "crt", "crtv", "c", "1" -> {

            }
            "spectator", "spect", "spec", "sp" -> {

            }
        }
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
            else -> {
                sender.sendMessage("${RED}Alternate gamemode command form detected")
            }
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

    private fun sendSetMessage(mode: GameMode, sender: CommandSender, altered: Player) {
        fun getMessageForSelf(mode: GameMode) : String =
            "${GOLD}Your gamemode is set to $RED${getGameModeName(mode)}$RESET"
        fun getMessageForOtherToSender(mode: GameMode, altered: Player) : String =
            "${GOLD}You set $RED${altered.name}'s$GOLD gamemode to $RED${getGameModeName(mode)}"
        fun getMessageForOtherToAltered(mode: GameMode, altered: Player) : String {
            return ""
        }
    }

    private fun setGameMode(p: Player, mode: GameMode) {
        p.gameMode = mode
    }
}