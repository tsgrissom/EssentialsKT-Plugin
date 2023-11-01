package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.enum.ChatColorKey
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.command.help.CommandHelpGenerator
import io.github.tsgrissom.pluginapi.command.help.SubcommandArgumentHelp
import io.github.tsgrissom.pluginapi.command.help.SubcommandHelp
import io.github.tsgrissom.pluginapi.extension.*
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.ChatColor
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
    private fun getTimeUtility() =
        getPlugin().getTimeUtility()

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
        val ccWhite = ChatColor.WHITE

        val label = context.label
        val help = CommandHelpGenerator(context)
            .withSubcommand(
                SubcommandHelp
                    .compose("add")
                    .withArgument(
                        SubcommandArgumentHelp
                            .compose("#")
                            .required(true)
                            .hoverText(
                                "${ccSec}How many ticks to add to the world's time",
                                "${ccSec}Ticks ${ccTert}= ${ccSec}AmountOfFullSeconds ${ccTert}* ${ccSec}20"
                            )
                    )
                    .withDescription("Add ticks to a world's current time")
                    .withSuggestion("/$label add ")
            )
            .withSubcommand(
                SubcommandHelp
                    .compose("query day")
                    .withDescription("Displays the amount of days of the game world")
                    .withSuggestion("/$label query day")
            )
            .withSubcommand(
                SubcommandHelp
                    .compose("query daytime")
                    .withDescription("Displays the time of day of the game world", "in ticks")
                    .withSuggestion("/$label query daytime")
            )
            .withSubcommand(
                SubcommandHelp
                    .compose("query full")
                    .withDescription("Displays the full time of the game world in", "ticks")
                    .withSuggestion("/$label query full")
            )
            .withSubcommand(
                SubcommandHelp
                    .compose("query gametime")
                    .withDescription("Displays the age of the game world in ticks")
                    .withSuggestion("/$label query gametime")
            )
            .withSubcommand(
                SubcommandHelp
                    .compose("set")
                    .withArgument(
                        SubcommandArgumentHelp
                            .compose("${ccVal}# ${ccTert}or ${ccVal}preset")
                            .required(true)
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

    private fun getAddUsage() : Array<BaseComponent> {
        val conf = getConfig()
        val ccErr = conf.getBungeeChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getBungeeChatColor(ChatColorKey.ErrorDetail)
        val ccSec = conf.getBungeeChatColor(ChatColorKey.Secondary)
        val ccTert = conf.getBungeeChatColor(ChatColorKey.Tertiary)
        val ccSucc = conf.getBungeeChatColor(ChatColorKey.Success)

        val comp = TextComponent("${ccErr}Usage: ${ccErrDetl}/time ")

        val subcComp = TextComponent("add ")
        subcComp.color = ccErrDetl
        subcComp.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/time add ")
        subcComp.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("${ccSec}Click to suggest command"))

        val arg0Comp = TextComponent("<Amount> ")
        arg0Comp.color = ccErrDetl
        arg0Comp.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("${ccSec}Required: ${ccSucc}Yes\n"),
            Text("${ccTert}- ${ccSec}An amount of time to add to the world's\n"),
            Text("   ${ccSec}current time\n"),
            Text("${ccTert}- ${ccSec}Usually ticks, which is full seconds multiplied by 20\n"),
            Text("${ccTert}- ${ccSec}Otherwise, an amount of time followed by s or m")
        )

        val arg1Comp = TextComponent("[World]")
        arg1Comp.color = ccErrDetl

        comp.addExtra(subcComp)
        comp.addExtra(arg0Comp)
        comp.addExtra(arg1Comp)

        return ComponentBuilder(comp).create()
    }

    private fun getSetUsage() : Array<BaseComponent> {
        val conf = getConfig()
        val ccErr = conf.getBungeeChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getBungeeChatColor(ChatColorKey.ErrorDetail)
        val ccSec = conf.getBungeeChatColor(ChatColorKey.Secondary)
        val ccTert = conf.getBungeeChatColor(ChatColorKey.Tertiary)
        val ccSucc = conf.getBungeeChatColor(ChatColorKey.Success)
        val ccVal = conf.getBungeeChatColor(ChatColorKey.Value)

        val comp = TextComponent("${ccErr}Usage: ${ccErrDetl}/time ")

        val subcComp = TextComponent("set ")
        subcComp.color = ccErrDetl
        subcComp.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/time set ")
        subcComp.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("${ccSec}Click to suggest command"))

        val arg0Comp = TextComponent("<")
        arg0Comp.color = ccErrDetl
        val arg0FirstComp = TextComponent("Amount")
        arg0FirstComp.color = ccErrDetl
        arg0FirstComp.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("${ccSec}Required: ${ccSucc}Yes ${ccSec}or ${ccSucc}Preset\n"),
            Text("${ccTert}- ${ccSec}An amount of ticks to set the world's\n"),
            Text("   ${ccSec}current time to\n"),
            Text("${ccTert}- ${ccSec}Usually ticks, which is full seconds multiplied by 20")
        )
        val arg0Between = TextComponent("/")
        arg0Between.color = ccTert
        val arg0SecondComp = TextComponent("Preset")
        arg0SecondComp.color = ccErrDetl
        arg0SecondComp.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("${ccSec}Required: ${ccSucc}Yes ${ccSec}or ${ccSucc}Amount\n"),
            Text("${ccSec}One of the following: ${ccVal}day${ccTert},${ccVal}noon${ccTert},${ccVal}sunset\n"),
            Text("  ${ccVal}night${ccTert},${ccVal}midnight${ccTert},${ccVal}sunrise")
        )
        val arg0Postfix = TextComponent("> ")
        arg0Postfix.color = ccErrDetl

        arg0Comp.addExtra(arg0FirstComp)
        arg0Comp.addExtra(arg0Between)
        arg0Comp.addExtra(arg0SecondComp)
        arg0Comp.addExtra(arg0Postfix)

        val arg1Comp = TextComponent("[World]")
        arg1Comp.color = ccErrDetl

        comp.addExtra(subcComp)
        comp.addExtra(arg0Comp)
        comp.addExtra(arg1Comp)

        return ComponentBuilder(comp).create()
    }

    private fun displayWorldTime(sender: CommandSender) {
        val conf = getConfig()
        val ccDetl = conf.getChatColor(ChatColorKey.Detail)
        val ccSec = conf.getChatColor(ChatColorKey.Secondary)
        val ccTert = conf.getChatColor(ChatColorKey.Tertiary)
        val ccSucc = conf.getChatColor(ChatColorKey.Success)
        val ccVal = conf.getChatColor(ChatColorKey.Value)

        val world = sender.getCurrentWorldOrDefault()

        val wn = world.name
        val worldTicks = world.time
        val wtAsSecs = world.time / 20
        val wtAsMins = wtAsSecs/60.0

        val percent = (worldTicks/24000.0) * 100

        sender.sendMessage(
            "${ccSucc}Time info for world ${ccDetl}$wn",
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
            else -> sender.sendMessage("${ccErr}Unknown subcommand ${ccErrDetl}\"$sub\"${ccErr}, do ${ccErrDetl}/time ? ${ccErr}for help")
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
            return sender.sendChatComponents(getAddUsage())

        val arg1 = args[1]
        val util = getTimeUtility()
        if (util.isInputInSeconds(arg1))
            return handleAddSeconds(context, arg1)
        else if (util.isInputInMinutes(arg1))
            return handleAddMinutes(context, arg1)

        val addend: Long

        try {
            addend = arg1.toLong()
        } catch (ignored: NumberFormatException) {
            return sender.sendMessage("${ccErrDetl}\"$arg1\" ${ccErr}should be an integer as game ticks or a preset")
        }

        var world = sender.getCurrentWorldOrDefault()

        if (args.size > 2) {
            val arg2 = args[2]
            world = Bukkit.getWorld(arg2)
                ?: return sender.sendMessage("${ccErr}Unknown world ${ccErrDetl}\"${arg2}\"")
        }

        if (lacksPermissionToSetWorldTime(sender, world))
            return context.sendNoPermission(sender, getTimeSetPerWorldPermission(world))

        val oldTime = world.time

        world.time = oldTime + addend

        val newTime = world.time
        val percent = ((newTime / 24000.0) * 100).roundToDigits(2)
        val wn = world.name
        val oldTimeF = util.convertTicksTo24Hour(oldTime, withColor=true)
        val newTimeF = util.convertTicksTo24Hour(newTime, withColor=true)

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
            ?: return sender.sendMessage("${ccErr}Time in seconds must be an integer followed by the letter s")
        var world = sender.getCurrentWorldOrDefault()

        if (args.size > 2) {
            val arg2 = args[2]
            world = Bukkit.getWorld(arg2)
                ?: return sender.sendMessage("${ccErr}Unknown world ${ccErrDetl}\"$arg2\"")
        }

        if (lacksPermissionToSetWorldTime(sender, world))
            return context.sendNoPermission(sender, getTimeSetPerWorldPermission(world))

        val addend = value * 20
        val oldTime = world.time

        world.time = oldTime + addend

        val newTime = world.time
        val percent = ((newTime / 24000.0) * 100).roundToDigits(2)
        val wn = world.name
        val util = getTimeUtility()
        val oldTimeF = util.convertTicksTo24Hour(oldTime, withColor=true)
        val newTimeF = util.convertTicksTo24Hour(newTime, withColor=true)

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
            ?: return sender.sendMessage("${ccErr}Time in minutes must be an integer followed by the letter m")

        var world = sender.getCurrentWorldOrDefault()

        if (args.size > 2) {
            val arg2 = args[2]
            world = Bukkit.getWorld(arg2)
                ?: return sender.sendMessage("${ccErr}Unknown world ${ccErrDetl}\"$arg2\"")
        }

        if (lacksPermissionToSetWorldTime(sender, world))
            return context.sendNoPermission(sender, getTimeSetPerWorldPermission(world))

        val addend = value * 60 * 20
        val oldTime = world.time

        world.time = oldTime + addend

        val newTime = world.time
        val percent = ((newTime / 24000.0) * 100).roundToDigits(2)
        val wn = world.name
        val util = getTimeUtility()
        val oldTimeF = util.convertTicksTo24Hour(oldTime, withColor=true)
        val newTimeF = util.convertTicksTo24Hour(newTime, withColor=true)

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
                ?: return sender.sendMessage("${ccErr}Unknown world ${ccErrDetl}\"$arg2\"")
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
            return sender.sendChatComponents(getSetUsage())

        val arg1 = args[1]

        if (arg1.isPercentage()) {
            return handleSetToPercentage(context)
        }

        if (arg1.equalsIc("day", "noon", "sunset", "dusk", "night", "midnight", "sunrise", "dawn")) {
            return handleSetToPresetSubcommand(context)
        }

        val newTicks = arg1.toLongOrNull()
            ?: return sender.sendMessage("${ccErrDetl}\"$arg1\" ${ccErr}should be an integer of game ticks (20 per full second)")

        if (newTicks < 0)
            return sender.sendMessage("${ccErr}New time cannot be negative ticks. Specify a positive value of at least 0.")

        if (newTicks > 24000)
            return sender.sendMessage("${ccErr}New time should not exceed ${ccErrDetl}24,000 ticks${ccErr}, the length of a full day")

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

        sender.sendMessage("${ccPrim}World ${ccDetl}${world.name}'s ${ccPrim}time went from ${ccDetl}$oldTime${ccTert}->${ccDetl}$newTicks")
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
                ?: return sender.sendMessage("${ccErr}Unknown world ${ccErrDetl}\"$arg2\"")
        }

        if (lacksPermissionToSetWorldTime(sender, world))
            return context.sendNoPermission(sender, getTimeSetPerWorldPermission(world))

        val oldTime = world.time
        world.time = newTicks

        sender.sendMessage("${ccPrim}World ${ccDetl}${world.name}'s ${ccPrim}time went from ${ccDetl}$oldTime${ccTert}->${ccDetl}$newTicks")
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
            ?: return sender.sendMessage("${ccErrDetl}\"$input\" ${ccErr}is not a valid percentage value (decimal percentages are invalid)")
        if (value <= 0)
            return sender.sendMessage("${ccErr}Percentage input must be a nonzero positive integer")
        if (value > 100)
            return sender.sendMessage("${ccErr}Percentage input must be between 0 -> 100")

        val quotient = value / 100.0
        val amount = quotient * 24000

        var world = sender.getCurrentWorldOrDefault()

        if (args.size > 2) {
            val arg2 = args[2]
            world = Bukkit.getWorld(arg2)
                ?: return sender.sendMessage("${ccErr}Unknown world ${ccErrDetl}\"$arg2\"")
        }

        val oldTime = world.time
        val newTicks = amount.toLong()
        world.time = newTicks

        sender.sendMessage("${ccPrim}World ${ccDetl}${world.name}'s ${ccPrim}time went from ${ccDetl}$oldTime${ccTert}->${ccDetl}$newTicks")
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