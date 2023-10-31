package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.gui.GameModeSelectorGui
import io.github.tsgrissom.essentialskt.misc.EssPlayer
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.command.help.CommandHelpGenerator
import io.github.tsgrissom.pluginapi.command.help.SubcommandArgumentHelp
import io.github.tsgrissom.pluginapi.command.help.SubcommandHelp
import io.github.tsgrissom.pluginapi.extension.*
import io.github.tsgrissom.pluginapi.chat.ClickableText
import io.github.tsgrissom.pluginapi.command.flag.CommandFlagParser
import io.github.tsgrissom.pluginapi.command.flag.ValidCommandFlag
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.ChatColor.DARK_GRAY as D_GRAY
import net.md_5.bungee.api.ChatColor.DARK_RED as D_RED
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

    // MARK: Static Declarations
    companion object {
        const val PERM_BASE      = "essentials.gamemode"
        const val PERM_OTHERS    = "essentials.gamemode.others"
        const val PERM_DEFENSIVE = "essentials.gamemode.noalter"
        const val PERM_ADVENTURE = "essentials.gamemode.adventure"
        const val PERM_CREATIVE  = "essentials.gamemode.creative"
        const val PERM_SURVIVAL  = "essentials.gamemode.survival"
        const val PERM_SPECTATOR = "essentials.gamemode.spectator"

        fun getAvailableGameModesAsComponent(
            sender: CommandSender,
            negative: Boolean = true
        ) : Array<BaseComponent> {
            val available: Set<GameMode> = if (sender is Player)
                EssPlayer(sender).getAvailableGameModes()
            else
                GameMode.entries.toSet()
            val mainColor = if (negative) D_RED else GRAY
            val detailColor = if (negative) RED else YELLOW

            val builder = ComponentBuilder()
                .appendc("Available gamemodes", mainColor)
                .appendc(":", D_GRAY)

            if (available.isEmpty()) {
                builder.appendc(" None", RED)
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
                    .append(clickText.toComponent())

                if (i != (available.size - 1))
                    builder.appendc(",", D_GRAY)
            }

            return builder.create()
        }
    }

    // MARK: Text Helper Functions
    private fun getCommandUsageAsComponent(sender: CommandSender) : Array<BaseComponent> {
        val isConsole = sender is ConsoleCommandSender
        val available: Set<GameMode> = if (sender is Player)
            EssPlayer(sender).getAvailableGameModes()
        else
            GameMode.entries.toSet()
        val availableNames = available.map { it.name.lowercase() }.toSet()
        val builder = ComponentBuilder()
            .appendc(if (isConsole) "Console Usage: " else "Usage: ", D_RED)
            .appendc("/gm <", RED)

        if (available.isNotEmpty()) {
            for ((i, mode) in availableNames.withIndex()) {
                val clickText = ClickableText
                    .compose(mode)
                    .color(RED)
                    .action(ClickEvent.Action.SUGGEST_COMMAND)
                    .value("/gm $mode ")

                builder
                    .append(clickText.toComponent())

                if (i != (available.size - 1))
                    builder.appendc("/", D_GRAY)
            }
        } else {
            builder.append("No Available Gamemodes")
        }

        builder
            .appendc("> ", RED)
            .appendc(if (isConsole) "<Target>" else "[Target]", RED)

        return builder.create()
    }

    private fun getHelp(context: CommandContext) : Array<BaseComponent> {
        val sender = context.sender
        val descVerbiage = if (sender.hasPermission(PERM_OTHERS)) "a player's" else "your"
        val suggestionPostfix = if (sender.hasPermission(PERM_OTHERS)) " " else ""
        val subcSurvival = SubcommandHelp
            .compose("survival")
            .withAliases("0", "surv", "sur", "s")
            .withDescription(
                "${GRAY}Set $descVerbiage gamemode to ${AQUA}Survival"
            )
            .withPermission(PERM_SURVIVAL)
            .withSuggestion("/gm survival${suggestionPostfix}")
        val subcCreative = SubcommandHelp
            .compose("creative")
            .withAliases("1", "create", "creat", "crtv", "crt", "c")
            .withDescription(
                "${GRAY}Set $descVerbiage gamemode to ${AQUA}Creative"
            )
            .withPermission(PERM_CREATIVE)
            .withSuggestion("/gm creative${suggestionPostfix}")
        val subcAdventure = SubcommandHelp
            .compose("adventure")
            .withAliases("2", "adv", "a")
            .withDescription(
                "${GRAY}Set $descVerbiage gamemode to ${AQUA}Adventure"
            )
            .withPermission(PERM_ADVENTURE)
            .withSuggestion("/gm adventure${suggestionPostfix}")
        val subcSpectator = SubcommandHelp
            .compose("spectator")
            .withAliases("spect", "spec", "sp")
            .withDescription(
                "${GRAY}Set $descVerbiage gamemode to ${AQUA}Spectator"
            )
            .withPermission(PERM_SPECTATOR)
            .withSuggestion("/gm spectator${suggestionPostfix}")

        if (sender.hasPermission(PERM_OTHERS)) {
            val targetingRequired = context.sender !is Player
            val targetingArgument = SubcommandArgumentHelp
                .compose("Target")
                .required(targetingRequired)
                .hoverText(
                    "${GRAY}If provided, will apply the specified",
                    " ${GRAY}gamemode to the targeted player"
                )
            subcSurvival.withArgument(targetingArgument)
            subcCreative.withArgument(targetingArgument)
            subcAdventure.withArgument(targetingArgument)
            subcSpectator.withArgument(targetingArgument)
        }

        val help = CommandHelpGenerator(context)
            .withAliases("gamemode", "gm", "egamemode", "egm")
            .withSubcommand(subcSurvival)
            .withSubcommand(subcCreative)
            .withSubcommand(subcAdventure)
            .withSubcommand(subcSpectator)

        return help.toComponents()
    }

    // MARK: Command Body
    override fun execute(context: CommandContext) {
        val sender = context.sender
        val args = context.args
        val flags = CommandFlagParser(args, ValidCommandFlag.FLAG_GRAPHICAL)

        if (args.isEmpty())
            return sender.spigot().sendMessage(*getCommandUsageAsComponent(sender))

        val sub = args[0]

        if (args.size == 1 && flags.wasPassed("gui")) {
            if (sender is ConsoleCommandSender)
                return sender.sendMessage("${D_RED}Console cannot open GUIs")
            if (sender !is Player)
                return

            GameModeSelectorGui(sender, sender).show(sender)
            return
        }

        val target: Player = if (args.size == 1) {
            if (sender is ConsoleCommandSender)
                return sender.sendMessage(
                    "${D_RED}Console Usage: ${RED}/gm <adventure|creative|survival|spectator> <Target>"
                )
            if (sender !is Player)
                return

            sender
        } else {
            val tn = args[1]
            Bukkit.getPlayer(tn)
                ?: return sender.sendMessage("${D_RED}Could not find player ${RED}$tn")
        }

        if (target == sender && sender.lacksPermission(PERM_BASE))
            return context.sendNoPermission(sender, PERM_BASE)
        if (target != sender && sender.lacksPermission(PERM_OTHERS))
            return context.sendNoPermission(sender, PERM_OTHERS)

        if (target is ConsoleCommandSender)
            return sender.sendMessage("${D_RED}Console does not have a gamemode to alter")

        val eTarget = EssPlayer(target)

        if (sub.equalsIc("toggle", "t", "cycle" ))
            return eTarget.cycleGameMode(sender)
        else if (sub.equalsIc("available", "list", "ls"))
            return sender.sendChatComponents(getAvailableGameModesAsComponent(sender, false))
        else if (sub.equalsIc("usage"))
            return sender.sendChatComponents(getCommandUsageAsComponent(sender))
        else if (sub.equalsIc("help", "h", "?"))
            return sender.sendChatComponents(getHelp(context))

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
                sender.sendMessage("${D_RED}Unknown gamemode ${RED}\"$sub\"")
                return sender.spigot().sendMessage(*getAvailableGameModesAsComponent(sender, true))
            }
        }

        eTarget.setGameMode(sender, mode)

        val mn = mode.name.capitalizeAllCaps()
        val tn = target.name

        target.sendMessage("${GOLD}Your gamemode has been set to ${RED}$mn")
        if (sender != target)
            sender.sendMessage("${GOLD}You set ${RED}$tn's ${GOLD}gamemode to ${RED}$mn")
    }

    // MARK: Tab Completion Handler
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
            if (label.equalsIc(validExtendedLabels)) {
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