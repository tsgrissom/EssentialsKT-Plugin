package io.github.tsgrissom.essentialskt.command

import BungeeChatColor
import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.command.help.CommandHelpBuilder
import io.github.tsgrissom.pluginapi.command.help.CommandUsageBuilder
import io.github.tsgrissom.pluginapi.command.help.SubcHelpBuilder
import io.github.tsgrissom.pluginapi.command.help.SubcParameterBuilder
import io.github.tsgrissom.pluginapi.extension.bukkit.getCurrentWorldOrDefault
import io.github.tsgrissom.pluginapi.extension.bukkit.lacksPermission
import io.github.tsgrissom.pluginapi.extension.bukkit.sendChatComponents
import io.github.tsgrissom.pluginapi.extension.kt.*
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission
import org.bukkit.util.StringUtil

class TimeCommand : CommandBase() {

    // MARK: Dependency Injection
    private fun getPlugin() =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()

    // MARK: Static Declarations
    companion object {
        const val PERM_BASE       = "essentials.time"
        const val PERM_SET        = "essentials.time.set"
        const val PERM_ALL_WORLDS = "essentials.time.world.all"

        const val TIME_DAY        = 1000L
        const val TIME_NOON       = 6000L
        const val TIME_SUNSET     = 12000L
        const val TIME_NIGHT      = 15000L
        const val TIME_MIDNIGHT   = 18000L
        const val TIME_SUNRISE    = 23000L

        fun getTimeSetPerWorldPermission(world: World) =
            Permission("essentials.time.world.${world.name}")

        fun hasPermissionToSetWorldTime(who: CommandSender, world: World) =
            who.hasPermission(PERM_ALL_WORLDS) || who.hasPermission(getTimeSetPerWorldPermission(world))
        fun lacksPermissionToSetWorldTime(who: CommandSender, world: World) =
            !hasPermissionToSetWorldTime(who, world)
    }

    // MARK: Text Helper Functions
    private fun getHelpAsComponent(context: CommandContext) : Array<BaseComponent> {
        val conf = getConfig()
        val ccSec = conf.getBungeeChatColor(ChatColorKey.Secondary)
        val ccTert = conf.getBungeeChatColor(ChatColorKey.Tertiary)
        val ccVal = conf.getBungeeChatColor(ChatColorKey.Value)
        val ccWhite = BungeeChatColor.WHITE
        val label = context.label

        val help = CommandHelpBuilder(context)
            .withSubcommands(
                SubcHelpBuilder("add")
                    .withArgument(
                        SubcParameterBuilder("#", required=true)
                            .hoverText(
                                "${ccSec}How many ticks to add to the world's time",
                                "${ccSec}Ticks ${ccTert}= ${ccSec}AmountOfFullSeconds ${ccTert}* ${ccSec}20"
                            )
                    )
                    .withDescription("Add ticks to a world's current time")
                    .withSuggestion("/$label add "),
                SubcHelpBuilder("query day")
                    .withDescription("Displays the amount of days of the game world")
                    .withSuggestion("/$label query day"),
                SubcHelpBuilder("query daytime")
                    .withDescription("Displays the time of day of the game world", "in ticks")
                    .withSuggestion("/$label query daytime"),
                SubcHelpBuilder("query full")
                    .withDescription("Displays the full time of the game world in", "ticks")
                    .withSuggestion("/$label query full"),
                SubcHelpBuilder("query gametime")
                    .withDescription("Displays the age of the game world in ticks")
                    .withSuggestion("/$label query gametime"),
                SubcHelpBuilder("set")
                    .withArgument(
                        SubcParameterBuilder("# or preset", required=true)
                            .hoverText(
                                "${ccVal}One of the following:",
                                "${ccWhite}1. ${ccSec}How many ticks to set the world's time to",
                                " ${ccTert}- ${ccSec}Ticks ${ccTert}= ${ccVal}x ${ccTert}times ${ccSec}20",
                                " ${ccTert}- ${ccSec}Where ${ccVal}x ${ccSec}is the desired amount of full seconds",
                                "${ccWhite}2. ${ccSec}A preset like ${ccVal}day${ccSec}, ${ccVal}night${ccSec}, ${ccVal}midnight${ccSec}, etc."
                            )
                    )
                    .withDescription(
                        "Set a world's current time to an amount",
                        "of ticks or an available preset"
                    )
                    .withSuggestion("/$label set ")
            )

        return help.toComponents()
    }

    private fun getAddUsage(context: CommandContext) : Array<BaseComponent> {
        val conf = getConfig()
        val ccErr = conf.getBungeeChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getBungeeChatColor(ChatColorKey.ErrorDetail)
        val ccSec = conf.getBungeeChatColor(ChatColorKey.Secondary)
        val ccTert = conf.getBungeeChatColor(ChatColorKey.Tertiary)

        return CommandUsageBuilder(context, "add")
            .colors(ccErr, ccErrDetl)
            .withParameters(
                SubcParameterBuilder("Amount")
                    .required()
                    .hoverText(
                        "${ccTert}- ${ccSec}An amount of time to add to the world's",
                        "   ${ccSec}current time",
                        "${ccTert}- ${ccSec}Usually ticks, which is full seconds multiplied by 20",
                        "${ccTert}- ${ccSec}Otherwise, an amount of time followed by s or m"
                    ),
                SubcParameterBuilder("World")
                    .optional()
            )
            .toComponents()
    }

    private fun getSetUsage(context: CommandContext) : Array<BaseComponent> {
        val conf = getConfig()
        val ccErr = conf.getBungeeChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getBungeeChatColor(ChatColorKey.ErrorDetail)
        val ccSec = conf.getBungeeChatColor(ChatColorKey.Secondary)
        val ccTert = conf.getBungeeChatColor(ChatColorKey.Tertiary)
        val ccVal = conf.getBungeeChatColor(ChatColorKey.Value)
        val ccUnderline = BungeeChatColor.UNDERLINE

        return CommandUsageBuilder(context, "set")
            .colors(ccErr, ccErrDetl)
            .withParameters(
                SubcParameterBuilder("Amount OR Preset")
                    .required()
                    .hoverText(
                        "${ccErrDetl}${ccUnderline}AMOUNT${ccSec}:",
                        "${ccTert}- ${ccSec}An amount of ticks to set the world's",
                        "   ${ccSec}current time to",
                        "${ccTert}- ${ccSec}Usually ticks, which is full seconds multiplied by 20",
                        "${ccErrDetl}${ccUnderline}PRESET${ccSec}:",
                        "${ccTert}- ${ccSec}One of the following: ${ccVal}day${ccTert},${ccVal}noon${ccTert},${ccVal}sunset",
                        "   ${ccVal}night${ccTert},${ccVal}midnight${ccTert},${ccVal}sunrise"
                    ),
                SubcParameterBuilder("World")
                    .optional()
            )
            .toComponents()
    }

    private fun displayWorldTime(sender: CommandSender) {
        val conf = getConfig()
        val ccDetl = conf.getChatColor(ChatColorKey.Detail)
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccSec = conf.getChatColor(ChatColorKey.Secondary)
        val ccTert = conf.getChatColor(ChatColorKey.Tertiary)
        val ccVal = conf.getChatColor(ChatColorKey.Value)

        val world = sender.getCurrentWorldOrDefault()

        val wn = world.name
        val worldTicks = world.time
        val wtAsSecs = world.time / 20
        val wtAsMins = wtAsSecs/60.0

        val percent = (worldTicks/24000.0) * 100

        sender.sendMessage(
            "${ccPrim}Time info for world ${ccDetl}$wn",
            "${ccTert}> ${ccVal}$worldTicks${ccSec}/${ccVal}24000 ticks",
            "${ccTert}> ${ccVal}$wtAsSecs${ccSec}/${ccVal}1200 seconds",
            "${ccTert}> ${ccVal}${wtAsMins.roundToDigits(1)}${ccSec}/${ccVal}20 minutes",
            "${ccTert}> ${ccVal}${percent.roundToDigits(1)}%"
        )
    }

    // MARK: Command Body
    override fun execute(context: CommandContext) {
        val conf = getConfig()
        val ccErr = conf.getBungeeChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getBungeeChatColor(ChatColorKey.ErrorDetail)

        val args = context.args
        val sender = context.sender

        if (args.isEmpty())
            return handleEmptyArgs(context)

        when (val sub = args[0]) {
            "help", "?", "h" -> sender.sendChatComponents(getHelpAsComponent(context))
            "add" -> handleAddSubcommand(context)
            "query" -> handleQuerySubcommand(context)
            "set" -> handleSetSubcommand(context)
            else -> sender.sendMessage("${ccErr}Unknown subcommand ${ccErrDetl}\"$sub\"${ccErr}, do ${ccErrDetl}/time ? ${ccErr}for help.")
        }
    }

    // MARK: Handlers
    private fun handleEmptyArgs(context: CommandContext) = displayWorldTime(context.sender)

    private fun handleAddSubcommand(context: CommandContext) {
        val conf = getConfig()
        val ccDetl = conf.getChatColor(ChatColorKey.Detail)
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccTert = conf.getChatColor(ChatColorKey.Tertiary)

        val args = context.args
        val sender = context.sender

        if (args.size == 1)
            return sender.sendChatComponents(getAddUsage(context))

        val arg1 = args[1]
        if (arg1.isInputInSeconds())
            return handleAddSeconds(context, arg1)
        else if (arg1.isInputInMinutes())
            return handleAddMinutes(context, arg1)

        val addend: Long

        try {
            addend = arg1.toLong()
        } catch (ignored: NumberFormatException) {
            return sender.sendMessage("${ccErrDetl}\"$arg1\" ${ccErr}should be an integer as game ticks or a preset.")
        }

        var world = sender.getCurrentWorldOrDefault()

        if (args.size > 2) {
            val arg2 = args[2]
            world = Bukkit.getWorld(arg2)
                ?: return sender.sendMessage("${ccErr}Unknown world ${ccErrDetl}\"${arg2}\"${ccErr}.")
        }

        if (lacksPermissionToSetWorldTime(sender, world))
            return context.sendNoPermission(sender, getTimeSetPerWorldPermission(world))

        val oldTime = world.time

        world.time = oldTime + addend

        val newTime = world.time
        val percent = ((newTime / 24000.0) * 100).roundToDigits(2)
        val wn = world.name
        val oldTimeF = oldTime.convertTicksTo24Hour(withColor=true)
        val newTimeF = newTime.convertTicksTo24Hour(withColor=true)

        // TODO Move away from withColor var of util function so it can be configured

        sender.sendMessage(
            "${ccTert}> ${ccDetl}$addend ticks ${ccPrim}added to world ${ccDetl}${wn}'s ${ccPrim}time",
            "${ccTert}> ${ccPrim}New time is ${ccDetl}$newTime ticks${ccPrim}, or ${ccDetl}${percent}% ${ccPrim}of the day",
            "${ccTert}> ${ccPrim}Converted to real time $oldTimeF ${ccTert}-> $newTimeF"
        )
    }

    private fun handleAddSeconds(context: CommandContext, input: String) {
        val conf = getConfig()
        val ccDetl = conf.getChatColor(ChatColorKey.Detail)
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccTert = conf.getChatColor(ChatColorKey.Tertiary)

        val args = context.args
        val sender = context.sender
        val sansSuffix = input.lowercase().removeSuffix("s")
        val value = sansSuffix.toIntOrNull()
            ?: return sender.sendMessage("${ccErr}Time in seconds must be an integer followed by the letter ${ccErrDetl}s${ccErr}.")
        var world = sender.getCurrentWorldOrDefault()

        if (args.size > 2) {
            val arg2 = args[2]
            world = Bukkit.getWorld(arg2)
                ?: return sender.sendMessage("${ccErr}Unknown world ${ccErrDetl}\"$arg2\"${ccErr}.")
        }

        if (lacksPermissionToSetWorldTime(sender, world))
            return context.sendNoPermission(sender, getTimeSetPerWorldPermission(world))

        val addend = value * 20
        val oldTime = world.time

        world.time = oldTime + addend

        val newTime = world.time
        val percent = ((newTime / 24000.0) * 100).roundToDigits(2)
        val wn = world.name
        val oldTimeF = oldTime.convertTicksTo24Hour(withColor=true)
        val newTimeF = newTime.convertTicksTo24Hour(withColor=true)

        // TODO Needs to be converted

        sender.sendMessage(
            "${ccTert}> ${ccDetl}$value seconds ${ccTert}(${ccDetl}$addend ticks${ccTert}) ${ccPrim}added to world ${ccDetl}${wn}'s ${ccPrim}time",
            "${ccTert}> ${ccPrim}New time is ${ccDetl}$newTime ticks${ccPrim}, or ${ccDetl}${percent}% ${ccPrim}of the day",
            "${ccTert}> ${ccPrim}Converted to real time $oldTimeF ${ccTert}-> $newTimeF"
        )
    }

    private fun handleAddMinutes(context: CommandContext, input: String) {
        val conf = getConfig()
        val ccDetl = conf.getChatColor(ChatColorKey.Detail)
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccTert = conf.getChatColor(ChatColorKey.Tertiary)

        val args = context.args
        val sender = context.sender
        val sansSuffix = input.lowercase().removeSuffix("m")
        val value = sansSuffix.toIntOrNull()
            ?: return sender.sendMessage("${ccErr}Time in minutes must be an integer followed by the letter ${ccErrDetl}m${ccErr}.")

        var world = sender.getCurrentWorldOrDefault()

        if (args.size > 2) {
            val arg2 = args[2]
            world = Bukkit.getWorld(arg2)
                ?: return sender.sendMessage("${ccErr}Unknown world ${ccErrDetl}\"$arg2\"${ccErr}.")
        }

        if (lacksPermissionToSetWorldTime(sender, world))
            return context.sendNoPermission(sender, getTimeSetPerWorldPermission(world))

        val addend = value * 60 * 20
        val oldTime = world.time

        world.time = oldTime + addend

        val newTime = world.time
        val percent = ((newTime / 24000.0) * 100).roundToDigits(2)
        val wn = world.name
        val oldTimeF = oldTime.convertTicksTo24Hour(withColor=true)
        val newTimeF = newTime.convertTicksTo24Hour(withColor=true)

        // TODO Convert away from using withColor util function

        sender.sendMessage(
            "${ccTert}> ${ccDetl}$value minutes ${ccTert}(${ccDetl}$addend ticks${ccTert}) ${ccPrim}added to world ${ccDetl}${wn}'s ${ccPrim}time",
            "${ccTert}> ${ccPrim}New time is ${ccDetl}$newTime ticks${ccPrim}, or ${ccDetl}${percent}% ${ccPrim}of the day",
            "${ccTert}> ${ccPrim}Converted to real time $oldTimeF ${ccTert}-> $newTimeF"
        )
    }

    private fun handleQuerySubcommand(context: CommandContext) {
        val conf = getConfig()
        val ccDetl = conf.getChatColor(ChatColorKey.Detail)
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccTert = conf.getChatColor(ChatColorKey.Tertiary)

        val args = context.args
        val len = args.size
        val sender = context.sender

        if (len == 1)
            return sender.sendChatComponents(getHelpAsComponent(context))

        val arg1 = args[1]
        var world = sender.getCurrentWorldOrDefault()

        if (len == 3) {
            val arg2 = args[2]
            world = Bukkit.getWorld(arg2)
                ?: return sender.sendMessage("${ccErr}Unknown world ${ccErrDetl}\"$arg2\"${ccErr}.")
        }

        val query = when (arg1.lowercase()) {
            "day" -> world.fullTime / 24000
            "daytime" -> world.time
            "full" -> world.fullTime
            "gametime" -> world.gameTime
            else -> {
                return sender.sendChatComponents(getHelpAsComponent(context))
            }
        }

        val qn = arg1.lowercase().capitalize()

        sender.sendMessage("${ccPrim}$qn${ccTert}: ${ccDetl}$query")

        val dayLength = "${ccTert}- ${ccDetl}24,000 ticks ${ccPrim}is the length of a full Minecraft day"

        if (arg1.equalsIc("day")) {
            sender.sendMessage(
                "${ccTert}- ${ccPrim}Day is the result of world's ${ccDetl}full time ${ccPrim}\u00F7 ${ccDetl}24,000",
                dayLength
            )
        } else if (arg1.equalsIc("daytime")) {
            sender.sendMessage(
                "${ccTert}- ${ccPrim}Daytime is the amount of ticks since the day began ${ccTert}(${ccDetl}0 ticks${ccTert})",
                dayLength
            )
        } else if (arg1.equalsIc("full")) {
            // TODO Print info about full time
        } else if (arg1.equalsIc("gametime")) {
            sender.sendMessage(
                "${ccTert}- ${ccPrim}Gametime is the age of the game world in ticks",
                dayLength
            )
        }
    }

    private fun handleSetSubcommand(context: CommandContext) {
        val conf = getConfig()
        val ccDetl = conf.getChatColor(ChatColorKey.Detail)
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccTert = conf.getChatColor(ChatColorKey.Tertiary)

        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(PERM_SET))
            return context.sendNoPermission(sender, PERM_SET)

        if (args.size == 1)
            return sender.sendChatComponents(getSetUsage(context))

        val arg1 = args[1]

        if (arg1.isPercentage()) {
            return handleSetToPercentage(context)
        }

        if (arg1.equalsIc("day", "noon", "sunset", "dusk", "night", "midnight", "sunrise", "dawn")) {
            return handleSetToPresetSubcommand(context)
        }

        val newTicks = arg1.toLongOrNull()
            ?: return sender.sendMessage("${ccErrDetl}\"$arg1\" ${ccErr}should be an integer of game ticks (${ccErrDetl}20 per full second${ccErr}).")

        if (newTicks < 0)
            return sender.sendMessage("${ccErr}New time cannot be negative ticks. Specify a positive value of at least 0.")

        if (newTicks > 24000)
            return sender.sendMessage("${ccErr}New time should not exceed ${ccErrDetl}24,000 ticks${ccErr}, the length of a full day.")

        var world = sender.getCurrentWorldOrDefault()

        if (args.size > 2) {
            val arg2 = args[2]
            world = Bukkit.getWorld(arg2)
                ?: return sender.sendMessage("${ccErr}Unknown world ${ccErrDetl}\"$arg2\"")
        }

        if (lacksPermissionToSetWorldTime(sender, world))
            return context.sendNoPermission(sender, getTimeSetPerWorldPermission(world))

        val oldTime = world.time
        world.time = newTicks

        sender.sendMessage("${ccPrim}World ${ccDetl}${world.name}'s ${ccPrim}time went from ${ccDetl}$oldTime${ccTert}->${ccDetl}$newTicks${ccPrim}.")
    }

    private fun handleSetToPresetSubcommand(context: CommandContext) {
        val conf = getConfig()
        val ccDetl = conf.getChatColor(ChatColorKey.Detail)
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccTert = conf.getChatColor(ChatColorKey.Tertiary)

        val args = context.args
        val sender = context.sender
        val arg1 = args[1]

        val newTicks = when (arg1.lowercase()) {
            "day" -> TIME_DAY
            "noon" -> TIME_NOON
            "sunset", "dusk" -> TIME_SUNSET
            "night" -> TIME_NIGHT
            "midnight" -> TIME_MIDNIGHT
            "sunrise", "dawn" -> TIME_SUNRISE
            else -> error("Unhandled time preset \"$arg1\" reached function")
        }

        var world = sender.getCurrentWorldOrDefault()

        if (args.size > 2) {
            val arg2 = args[2]
            world = Bukkit.getWorld(arg2)
                ?: return sender.sendMessage("${ccErr}Unknown world ${ccErrDetl}\"$arg2\"${ccErr}.")
        }

        if (lacksPermissionToSetWorldTime(sender, world))
            return context.sendNoPermission(sender, getTimeSetPerWorldPermission(world))

        val oldTime = world.time
        world.time = newTicks

        sender.sendMessage("${ccPrim}World ${ccDetl}${world.name}'s ${ccPrim}time went from ${ccDetl}$oldTime${ccTert}->${ccDetl}$newTicks${ccPrim}.")
    }

    private fun handleSetToPercentage(context: CommandContext) {
        val conf = getConfig()
        val ccDetl = conf.getChatColor(ChatColorKey.Detail)
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccTert = conf.getChatColor(ChatColorKey.Tertiary)

        val args = context.args
        val sender = context.sender
        val input = args[1]

        val sansPercent = input.removeSuffix("%")
        val value = sansPercent.toLongOrNull()
            ?: return sender.sendMessage("${ccErrDetl}\"$input\" ${ccErr}is not a valid percentage value (decimal percentages are invalid).")
        if (value <= 0)
            return sender.sendMessage("${ccErr}Percentage input must be a nonzero positive integer.")
        if (value > 100)
            return sender.sendMessage("${ccErr}Percentage input must be between ${ccErrDetl}0 -> 100${ccErr}.")

        val quotient = value / 100.0
        val amount = quotient * 24000

        var world = sender.getCurrentWorldOrDefault()

        if (args.size > 2) {
            val arg2 = args[2]
            world = Bukkit.getWorld(arg2)
                ?: return sender.sendMessage("${ccErr}Unknown world ${ccErrDetl}\"$arg2\"${ccErr}.")
        }

        val oldTime = world.time
        val newTicks = amount.toLong()
        world.time = newTicks

        sender.sendMessage("${ccPrim}World ${ccDetl}${world.name}'s ${ccPrim}time went from ${ccDetl}$oldTime${ccTert}->${ccDetl}$newTicks${ccPrim}.")
    }

    // MARK: Tab Completion Handler
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val tab = mutableListOf<String>()

        if (sender.lacksPermission(PERM_BASE) && sender.lacksPermission(PERM_SET))
            return tab

        val suggestSub = mutableListOf("help")

        if (sender.hasPermission(PERM_BASE))
            suggestSub.add("query")
        if (sender.hasPermission(PERM_SET))
            suggestSub.addAll(listOf("add", "set"))

        val addLabels = listOf("add")
        val setLabels = listOf("set")
        val queryLabels = listOf("query")
        val suggestQueryArg1 = listOf("day", "daytime", "full", "gametime")
        val suggestSetArg1 = listOf("day", "noon", "sunset", "night", "midnight", "sunrise")

        val len = args.size

        if (len > 0) {
            val sub = args[0]

            if (len == 1) {
                StringUtil.copyPartialMatches(sub, suggestSub, tab)
            } else if (len == 2) {
                if (sub.equalsIc(queryLabels)) {
                    StringUtil.copyPartialMatches(args[1], suggestQueryArg1, tab)
                } else if (sub.equalsIc(setLabels)) {
                    StringUtil.copyPartialMatches(args[1], suggestSetArg1, tab)
                }
            } else if (len == 3) {
                if (sub.equalsIc(queryLabels) && args[1].equalsIc(suggestQueryArg1)) {
                    StringUtil.copyPartialMatches(args[2], getWorldNamesToMutableList(), tab)
                } else if ((sub.equalsIc(addLabels) || sub.equalsIc(setLabels)) && args[1].isNotEmpty() && sender.hasPermission(PERM_ALL_WORLDS)) {
                    StringUtil.copyPartialMatches(args[2], getWorldNamesToMutableList(), tab)
                }
            }
        }

        return tab.sorted().toMutableList()
    }
}