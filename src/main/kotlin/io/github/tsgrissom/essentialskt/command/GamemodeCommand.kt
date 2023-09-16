package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.capitalizeAllCaps
import io.github.tsgrissom.pluginapi.extension.equalsIc
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import io.github.tsgrissom.pluginapi.misc.ClickableText
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.GameMode.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

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

    private fun getNextGameMode(target: Player) : GameMode? {
        return when (target.gameMode) {
            ADVENTURE -> CREATIVE
            CREATIVE -> SURVIVAL
            SURVIVAL -> ADVENTURE
            else -> null
        }
    }

    private fun cycleGameMode(sender: CommandSender, target: Player) {
        val gm = getNextGameMode(target)
            ?: return sender.sendColored("&c${target.name} &4is in a gamemode which cannot be cycled")
        val mn = gm.name.capitalizeAllCaps()
        val tn = target.name

        setGameMode(sender, target, gm)

        target.sendColored("&6Your gamemode is set to &c$mn")
        if (sender != target)
            sender.sendColored("&6You set &c$tn's &6gamemode to &c$mn")
    }

    private fun getAvailableGameModes(sender: CommandSender) : Set<GameMode> {
        val available = mutableSetOf<GameMode>()

        if (sender.hasPermission(permAdventure))
            available.add(ADVENTURE)
        if (sender.hasPermission(permCreative))
            available.add(CREATIVE)
        if (sender.hasPermission(permSurvival))
            available.add(SURVIVAL)
        if (sender.hasPermission(permSpectator))
            available.add(SPECTATOR)

        return available
    }

    private fun getAvailableGameModesAsComponentBuilder(sender: CommandSender, negative: Boolean = true) : ComponentBuilder {
        val available = getAvailableGameModes(sender)
            .map { it.name.lowercase() }
            .toSet()
        val mainColor = if (negative) ChatColor.DARK_RED else ChatColor.GRAY
        val detailColor = if (negative) ChatColor.RED else ChatColor.YELLOW

        val builder = ComponentBuilder()
            .append("Available gamemodes").color(mainColor)
            .append(":").color(ChatColor.DARK_GRAY)

        if (available.isEmpty()) {
            builder
                .append(" ")
                .append("None").color(ChatColor.RED)
            return builder
        }

        for ((i, mode) in available.withIndex()) {
            val clickText = ClickableText
                .compose("$detailColor$mode")
                .action(ClickEvent.Action.SUGGEST_COMMAND)
                .value("/gm $mode ")

            builder
                .append(" ")
                .append(clickText.toTextComponent())

            if (i != (available.size - 1))
                builder.append(",").color(ChatColor.DARK_GRAY)
        }

        return builder
    }

    private fun getCommandUsageAsComponentBuilder(sender: CommandSender) : ComponentBuilder {
        val isConsole = sender is ConsoleCommandSender
        val available = getAvailableGameModes(sender).map { it.name.lowercase() }.toSet()
        val builder = ComponentBuilder()
            .append(if (isConsole) "Console Usage: " else "Usage: ").color(ChatColor.DARK_RED)
            .append("/gm <").color(ChatColor.RED)

        if (available.isNotEmpty()) {
            for ((i, mode) in available.withIndex()) {
                val clickText = ClickableText
                    .compose(mode)
                    .action(ClickEvent.Action.SUGGEST_COMMAND)
                    .value("/gm $mode ")

                builder
                    .append(clickText.toTextComponent()).color(ChatColor.RED)

                if (i != (available.size - 1))
                    builder.append("/").color(ChatColor.DARK_GRAY)
            }
        } else {
            builder.append("No Available Gamemodes")
        }

        builder
            .append("> ").color(ChatColor.RED)
            .append(if (isConsole) "<Target>" else "[Target]")

        return builder
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

        if (args.isEmpty()) {
            val usage = getCommandUsageAsComponentBuilder(sender)
            return sender.spigot().sendMessage(*usage.create())
        }

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

        if (sub.equalsIc("toggle", "t", "cycle" )) {
            return cycleGameMode(sender, target)
        } else if (sub.equalsIc("available", "list", "ls")) {
            val ls = getAvailableGameModesAsComponentBuilder(sender, false)
            return sender.spigot().sendMessage(*ls.create())
        } else if (sub.equalsIc("usage")) {
            val usage = getCommandUsageAsComponentBuilder(sender)
            return sender.spigot().sendMessage(*usage.create())
        }

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
            else -> {
                val component = getAvailableGameModesAsComponentBuilder(sender, true)
                sender.sendColored("&4Unknown gamemode &c\"$sub\"")
                return sender.spigot().sendMessage(*component.create())
            }
        }

        setGameMode(sender, target, mode)

        val mn = mode.name.capitalizeAllCaps()
        val tn = target.name

        target.sendColored("&6Your gamemode has been set to &c$mn")
        if (sender != target)
            sender.sendColored("&6You set &c$tn's &6gamemode to &c$mn")
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val suggestGamemodes = mutableListOf("adventure", "creative", "survival", "spectator", "toggle")
        val shortLabels = mutableListOf("gma", "gmc", "gms", "gmsp")

        val tab = mutableListOf<String>()
        val len = args.size

        if (len > 0) {
            val sub = args[0]

            if (len == 1) {
                if (shortLabels.contains(label.lowercase())) {
                    if (sender.hasPermission(permOthers))
                        StringUtil.copyPartialMatches(args[0], getOnlinePlayerNamesToMutableList(), tab)
                } else {
                    StringUtil.copyPartialMatches(sub, suggestGamemodes, tab)
                }
            } else if (len == 2) {
                if (sub.equalsIc(suggestGamemodes)) {
                    if (sender.hasPermission(permOthers))
                        StringUtil.copyPartialMatches(args[1], getOnlinePlayerNamesToMutableList(), tab)
                }
            }
        }

        return tab.sorted().toMutableList()
    }
}