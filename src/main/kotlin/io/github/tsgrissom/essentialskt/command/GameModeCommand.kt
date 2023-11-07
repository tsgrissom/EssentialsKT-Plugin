package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.essentialskt.gui.GameModeSelectorGui
import io.github.tsgrissom.essentialskt.misc.EssPlayer
import io.github.tsgrissom.pluginapi.chat.ClickTextBuilder
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.command.help.CommandHelpGenerator
import io.github.tsgrissom.pluginapi.command.help.SubcommandArgumentHelp
import io.github.tsgrissom.pluginapi.command.help.SubcommandHelp
import io.github.tsgrissom.pluginapi.command.flag.CommandFlagParser
import io.github.tsgrissom.pluginapi.command.flag.ValidCommandFlag
import io.github.tsgrissom.pluginapi.extension.bukkit.appendc
import io.github.tsgrissom.pluginapi.extension.bukkit.lacksPermission
import io.github.tsgrissom.pluginapi.extension.bukkit.sendChatComponents
import io.github.tsgrissom.pluginapi.extension.kt.capitalizeAllCaps
import io.github.tsgrissom.pluginapi.extension.kt.equalsIc
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

    private fun getPlugin() =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()

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
            // TODO Replace UI colors

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

                val clickText = ClickTextBuilder("$detailColor$mn")
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
        val conf = getConfig()
        val ccErr = conf.getBungeeChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getBungeeChatColor(ChatColorKey.ErrorDetail)
        val ccTert = conf.getBungeeChatColor(ChatColorKey.Tertiary)
        val isConsole = sender is ConsoleCommandSender
        val available: Set<GameMode> = if (sender is Player)
            EssPlayer(sender).getAvailableGameModes()
        else
            GameMode.entries.toSet()
        val availableNames = available.map { it.name.lowercase() }.toSet()
        val builder = ComponentBuilder()
            .appendc(if (isConsole) "Console Usage: " else "Usage: ", ccErr)
            .appendc("/gm <", ccErrDetl)

        if (available.isNotEmpty()) {
            for ((i, mode) in availableNames.withIndex()) {
                val clickText = ClickTextBuilder(mode)
                    .action(ClickEvent.Action.SUGGEST_COMMAND)
                    .color(ccErrDetl)
                    .value("/gm $mode ")

                builder
                    .append(clickText.toComponent())

                if (i != (available.size - 1))
                    builder.appendc("/", ccTert)
            }
        } else {
            builder.append("No Available Gamemodes")
        }

        builder
            .appendc("> ", ccErrDetl)
            .appendc(if (isConsole) "<Target>" else "[Target]", ccErrDetl)

        return builder.create()
    }

    private fun getHelp(context: CommandContext) : Array<BaseComponent> {
        val conf = getConfig()
        val ccSec = conf.getBungeeChatColor(ChatColorKey.Secondary)
        val ccType = conf.getBungeeChatColor(ChatColorKey.Type)
        val sender = context.sender
        val descVerbiage = if (sender.hasPermission(PERM_OTHERS)) "a player's" else "your"
        val suggestionPostfix = if (sender.hasPermission(PERM_OTHERS)) " " else ""
        val subcSurvival = SubcommandHelp
            .compose("survival")
            .withAliases("0", "surv", "sur", "s")
            .withDescription(
                "${ccSec}Set $descVerbiage gamemode to ${ccType}Survival"
            )
            .withPermission(PERM_SURVIVAL)
            .withSuggestion("/gm survival${suggestionPostfix}")
        val subcCreative = SubcommandHelp
            .compose("creative")
            .withAliases("1", "create", "creat", "crtv", "crt", "c")
            .withDescription(
                "${ccSec}Set $descVerbiage gamemode to ${ccType}Creative"
            )
            .withPermission(PERM_CREATIVE)
            .withSuggestion("/gm creative${suggestionPostfix}")
        val subcAdventure = SubcommandHelp
            .compose("adventure")
            .withAliases("2", "adv", "a")
            .withDescription(
                "${ccSec}Set $descVerbiage gamemode to ${ccType}Adventure"
            )
            .withPermission(PERM_ADVENTURE)
            .withSuggestion("/gm adventure${suggestionPostfix}")
        val subcSpectator = SubcommandHelp
            .compose("spectator")
            .withAliases("spect", "spec", "sp")
            .withDescription(
                "${ccSec}Set $descVerbiage gamemode to ${ccType}Spectator"
            )
            .withPermission(PERM_SPECTATOR)
            .withSuggestion("/gm spectator${suggestionPostfix}")

        if (sender.hasPermission(PERM_OTHERS)) {
            val targetingRequired = context.sender !is Player
            val targetingArgument = SubcommandArgumentHelp
                .compose("Target")
                .required(targetingRequired)
                .hoverText(
                    "${ccSec}If provided, will apply the specified",
                    " ${ccSec}gamemode to the targeted player"
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
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)
        val ccPrimary = conf.getChatColor(ChatColorKey.Primary)
        val ccDetail = conf.getChatColor(ChatColorKey.Detail)

        val sender = context.sender
        val args = context.args
        val flags = CommandFlagParser(args, ValidCommandFlag.FLAG_GRAPHICAL)

        if (args.isEmpty())
            return sender.spigot().sendMessage(*getCommandUsageAsComponent(sender))

        val sub = args[0]

        if (args.size == 1 && flags.wasPassed("gui")) {
            if (sender is ConsoleCommandSender)
                return sender.sendMessage("${ccErr}Console cannot open GUIs")
            if (sender !is Player)
                return

            GameModeSelectorGui(sender, sender).show(sender)
            return
        }

        val target: Player = if (args.size == 1) {
            if (sender is ConsoleCommandSender)
                return sender.sendMessage(
                    "${ccErr}Console Usage: ${ccErrDetl}/gm <adventure|creative|survival|spectator> <Target>"
                )
            if (sender !is Player)
                return

            sender
        } else {
            val tn = args[1]
            Bukkit.getPlayer(tn)
                ?: return sender.sendMessage("${ccErr}Could not find player ${ccErrDetl}$tn")
        }

        if (target == sender && sender.lacksPermission(PERM_BASE))
            return context.sendNoPermission(sender, PERM_BASE)
        if (target != sender && sender.lacksPermission(PERM_OTHERS))
            return context.sendNoPermission(sender, PERM_OTHERS)

        if (target is ConsoleCommandSender)
            return sender.sendMessage("${ccErr}Console does not have a gamemode to alter")

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
                sender.sendMessage("${ccErr}Unknown gamemode ${ccErrDetl}\"$sub\"")
                return sender.spigot().sendMessage(*getAvailableGameModesAsComponent(sender, true))
            }
        }

        eTarget.setGameMode(sender, mode)

        val mn = mode.name.capitalizeAllCaps()
        val tn = target.name

        target.sendMessage("${ccPrimary}Your gamemode has been set to ${ccDetail}$mn")
        if (sender != target)
            sender.sendMessage("${ccPrimary}You set ${ccDetail}$tn's ${ccPrimary}gamemode to ${ccDetail}$mn")
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