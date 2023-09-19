package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.gui.GameModeSelectorGui
import io.github.tsgrissom.essentialskt.misc.EssPlayer
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.*
import io.github.tsgrissom.pluginapi.misc.ClickableText
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
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

class GameModeCommand : CommandBase() {

    companion object {
        const val PERM_BASE = "essentials.gamemode"
        const val PERM_OTHERS = "essentials.gamemode.others"
        const val PERM_DEFENSIVE = "essentials.gamemode.noalter"
        const val PERM_ADVENTURE = "essentials.gamemode.adventure"
        const val PERM_CREATIVE = "essentials.gamemode.creative"
        const val PERM_SURVIVAL = "essentials.gamemode.survival"
        const val PERM_SPECTATOR = "essentials.gamemode.spectator"
    }

    private val flagGui = Pair("gui", "g")

    private fun setGameMode(sender: CommandSender, target: Player, mode: GameMode) {
        if (sender != target && target.hasPermission(PERM_DEFENSIVE)) {
            val s = sender.name
            val t = target.name
            Bukkit.getLogger().info(
                "$s attempted to set ${t}'s gamemode but the target had \"$PERM_DEFENSIVE\""
            )
            sender.sendColored("&4You are not able to set &c${t}'s &4gamemode")
            return
        }

        target.gameMode = mode
    }

    private fun getNextGameMode(sender: CommandSender, target: Player) : GameMode? {
        fun checkPermission(mode: GameMode) : Boolean {
            return sender.hasPermission("essentials.gamemode.${mode.name.lowercase()}")
        }

        return when (target.gameMode) {
            ADVENTURE -> {
                if (checkPermission(CREATIVE)) CREATIVE
                else if (checkPermission(SURVIVAL)) SURVIVAL
                else ADVENTURE
            }
            CREATIVE -> {
                if (checkPermission(SURVIVAL)) SURVIVAL
                else if (checkPermission(ADVENTURE)) ADVENTURE
                else CREATIVE
            }
            SURVIVAL -> {
                if (checkPermission(ADVENTURE)) ADVENTURE
                else if (checkPermission(CREATIVE)) CREATIVE
                else SURVIVAL
            }
            else -> null
        }
    }

    private fun cycleGameMode(sender: CommandSender, target: Player) {
        val gm = getNextGameMode(sender, target)
            ?: return sender.sendColored("&c${target.name} &4is in a gamemode which cannot be cycled")
        val mn = gm.name.capitalizeAllCaps()
        val tn = target.name

        if (gm == target.gameMode)
            return sender.sendColored("&4You do not have permission to cycle to another gamemode")

        Bukkit.dispatchCommand(sender, "gm $mn $tn")
    }

    private fun getAvailableGameModesAsComponent(
        sender: CommandSender,
        negative: Boolean = true
    ) : Array<BaseComponent> {
        val available: Set<GameMode> = if (sender is Player)
            EssPlayer(sender).getAvailableGameModes()
        else
            GameMode.entries.toSet()
        val mainColor = if (negative) ChatColor.DARK_RED else ChatColor.GRAY
        val detailColor = if (negative) ChatColor.RED else ChatColor.YELLOW

        val builder = ComponentBuilder()
            .append("Available gamemodes").color(mainColor)
            .append(":").color(ChatColor.DARK_GRAY)

        if (available.isEmpty()) {
            builder
                .append(" ")
                .append("None").color(ChatColor.RED)
            return builder.create()
        }

        for ((i, mode) in available.withIndex()) {
            val mn = mode.name.capitalizeAllCaps()
            val ml = mode.name.lowercase()
            var value = "/gm $ml"

            if (sender.hasPermission(PERM_OTHERS))
                value += " "

            val clickText = ClickableText
                .compose("$detailColor$mn")
                .action(ClickEvent.Action.SUGGEST_COMMAND)
                .value(value)

            builder
                .append(" ")
                .append(clickText.toTextComponent())

            if (i != (available.size - 1))
                builder.append(",").color(ChatColor.DARK_GRAY)
        }

        return builder.create()
    }

    private fun getCommandUsageAsComponent(sender: CommandSender) : Array<BaseComponent> {
        val isConsole = sender is ConsoleCommandSender
        val available: Set<GameMode> = if (sender is Player)
            EssPlayer(sender).getAvailableGameModes()
        else
            GameMode.entries.toSet()
        val availableNames = available.map { it.name.lowercase() }.toSet()
        val builder = ComponentBuilder()
            .append(if (isConsole) "Console Usage: " else "Usage: ").color(ChatColor.DARK_RED)
            .append("/gm <").color(ChatColor.RED)

        if (available.isNotEmpty()) {
            for ((i, mode) in availableNames.withIndex()) {
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

        return builder.create()
    }

    override fun execute(context: CommandContext) {
        val sender = context.sender
        val label = context.label

        when (label.lowercase()) {
            "gma", "gmc", "gms", "gmsp", "egma", "egmc", "egms", "egmsp", "gm0", "gm1", "gm2", "gmt", "gmtoggle" -> {
                handleShorthandLabel(context)
            }
            "gamemode", "gm", "egamemode", "egm" -> {
                handleExtendedLabel(context)
            }
            else -> {
                sender.sendColored("&4Alternate gamemode command form detected")
            }
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

        if (target == sender && sender.lacksPermission(PERM_BASE))
            return context.sendNoPermission(sender, PERM_BASE)

        if (target != sender && sender.lacksPermission(PERM_OTHERS))
            return context.sendNoPermission(sender, PERM_OTHERS)

        if (target is ConsoleCommandSender)
            return sender.sendColored("&4Console does not have a gamemode to alter")

        if (label.equalsIc("gmt", "gmtoggle")) {
            cycleGameMode(sender, target)
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
                val available = getAvailableGameModesAsComponent(sender, true)
                return sender.sendChatComponents(available)
            }
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
            return sender.spigot().sendMessage(*getCommandUsageAsComponent(sender))

        val sub = args[0]

        if (args.size == 1 && context.hasFlag(flagGui)) {
            if (sender is ConsoleCommandSender)
                return sender.sendColored("&4Console cannot open GUIs")
            if (sender !is Player)
                return

            GameModeSelectorGui(sender, sender).show(sender)
            return
        }

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

        if (target == sender && sender.lacksPermission(PERM_BASE))
            return context.sendNoPermission(sender, PERM_BASE)
        if (target != sender && sender.lacksPermission(PERM_OTHERS))
            return context.sendNoPermission(sender, PERM_OTHERS)

        if (target is ConsoleCommandSender)
            return sender.sendColored("&4Console does not have a gamemode to alter")

        if (sub.equalsIc("toggle", "t", "cycle" ))
            return cycleGameMode(sender, target)
        else if (sub.equalsIc("available", "list", "ls"))
            return sender.spigot().sendMessage(*getAvailableGameModesAsComponent(sender, false))
        else if (sub.equalsIc("usage"))
            return sender.spigot().sendMessage(*getCommandUsageAsComponent(sender))

        val mode = when (sub.lowercase()) {
            "0", "survival", "surv", "sur", "s" -> {
                if (sender.lacksPermission(PERM_SURVIVAL))
                    return context.sendNoPermission(sender, PERM_SURVIVAL)
                SURVIVAL
            }
            "1", "creative", "create", "creat", "crtv", "crt", "c" -> {
                if (sender.lacksPermission(PERM_CREATIVE))
                    return context.sendNoPermission(sender, PERM_CREATIVE)
                CREATIVE
            }
            "2", "adventure", "adv", "a" -> {
                if (sender.lacksPermission(PERM_ADVENTURE))
                    return context.sendNoPermission(sender, PERM_ADVENTURE)
                ADVENTURE
            }
            "spectator", "spect", "spec", "sp" -> {
                if (sender.lacksPermission(PERM_SPECTATOR))
                    return context.sendNoPermission(sender, PERM_SPECTATOR)
                SPECTATOR
            }
            else -> {
                sender.sendColored("&4Unknown gamemode &c\"$sub\"")
                return sender.spigot().sendMessage(*getAvailableGameModesAsComponent(sender, true))
            }
        }

        setGameMode(sender, target, mode)

        val mn = mode.name.capitalizeAllCaps()
        val tn = target.name

        target.sendColored("&6Your gamemode has been set to &c$mn")
        if (sender != target)
            sender.sendColored("&6You set &c$tn's &6gamemode to &c$mn")
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> {
        val tab = mutableListOf<String>()
        val len = args.size

        if (sender.lacksPermission(PERM_BASE) && sender.lacksPermission(PERM_OTHERS))
            return tab

        val validModeSpecifiers = listOf(
            "toggle", "0", "survival", "surv", "sur", "s", "1",
            "creative", "create", "creat", "crtv", "crt", "c",
            "2", "adventure", "adv", "a",
            "spectator", "spect", "spec", "sp"
        )
        val validShorthandLabels = listOf(
            "gma", "gmc", "gms", "gmsp", "egma", "egmc", "egms",
            "egmsp", "gm0", "gm1", "gm2", "gmt", "gmtoggle"
        )
        val validExtendedLabels = listOf(
            "gamemode", "gm", "egamemode", "egm"
        )
        val allGamemodes = listOf("adventure", "creative", "survival", "spectator")
        val suggestFlags = mutableListOf("--gui")
        val suggestModes = mutableListOf("toggle")
        val permittedModes = allGamemodes.filter { sender.hasPermission("essentials.gamemode.$it") }
        suggestModes.addAll(permittedModes)

        if (len == 0)
            return tab

        val arg0 = args[0]

        if (len == 1) {
            if (label.equalsIc(validShorthandLabels) && sender.hasPermission(PERM_OTHERS)) {
                StringUtil.copyPartialMatches(arg0, getOnlinePlayerNamesToMutableList(), tab)
            } else if (label.equalsIc(validExtendedLabels)) {
                StringUtil.copyPartialMatches(arg0, suggestFlags, tab)
                StringUtil.copyPartialMatches(arg0, suggestModes, tab)
            }
        } else if (len == 2) {
            if (label.equalsIc(validExtendedLabels) && arg0.equalsIc(validModeSpecifiers) && sender.hasPermission(PERM_OTHERS)) {
                StringUtil.copyPartialMatches(args[1], getOnlinePlayerNamesToMutableList(), tab)
            }
        }

        return tab.sorted().toMutableList()
    }
}