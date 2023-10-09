package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.command.help.CommandHelpGenerator
import io.github.tsgrissom.pluginapi.command.help.SubcommandArgumentHelp
import io.github.tsgrissom.pluginapi.command.help.SubcommandHelp
import io.github.tsgrissom.pluginapi.extension.*
import net.md_5.bungee.api.ChatColor.*
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
import org.bukkit.util.StringUtil

class TimeCommand : CommandBase() {

    private fun getPlugin() =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getTimeUtility() =
        getPlugin().getTimeUtility()

    companion object {
        const val PERM_BASE = "essentials.time"
        const val PERM_SET = "essentials.time.set"
        const val PERM_ALL_WORLDS = "essentials.time.world.all"

        const val TIME_DAY: Long = 1000
        const val TIME_NOON: Long = 6000
        const val TIME_SUNSET: Long = 12000
        const val TIME_NIGHT: Long = 15000
        const val TIME_MIDNIGHT: Long = 18000
        const val TIME_SUNRISE: Long = 23000

        fun getTimeSetPerWorldPermission(world: World) =
            "essentials.time.world.${world.name}"

        fun hasPermissionToSetWorldTime(who: CommandSender, world: World) =
            who.hasPermission(PERM_ALL_WORLDS) || who.hasPermission(getTimeSetPerWorldPermission(world))

        fun lacksPermissionToSetWorldTime(who: CommandSender, world: World) =
            !hasPermissionToSetWorldTime(who, world)
    }

    private fun getHelpAsComponent(context: CommandContext) : Array<BaseComponent> {
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
                                "&7How many ticks to add to the world's time",
                                "&7Ticks &8= &7AmountOfFullSeconds &8* &720"
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
                            .compose("&e# &8or &epreset".translateColor())
                            .required(true)
                            .hoverText(
                                "&eOne of the following:",
                                "&f1. &7How many ticks to set the world's time to",
                                " &8- &7Ticks &8= &ex &8times &720",
                                " &8- &7Where &ex &7is the desired amount of full seconds",
                                "&f2. &7A preset like &eday&7, &enight&7, &emidnight&7, etc."
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

    override fun execute(context: CommandContext) {
        val args = context.args
        val sender = context.sender

        if (args.isEmpty())
            return handleEmptyArgs(context)

        when (val sub = args[0]) {
            "help", "?", "h" -> sender.sendChatComponents(getHelpAsComponent(context))
            "add" -> handleAddSubcommand(context)
            "query" -> handleQuerySubcommand(context)
            "set" -> handleSetSubcommand(context)
            else -> sender.sendColored("&4Unknown subcommand &c\"$sub\"&4, do &c/time ? &4for help")
        }
    }

    private fun displayWorldTime(sender: CommandSender) {
        val world = sender.getCurrentWorldOrDefault()

        val wn = world.name
        val worldTicks = world.time
        val wtAsSecs = world.time / 20
        val wtAsMins = wtAsSecs/60.0

        val percent = (worldTicks/24000.0) * 100

        sender.sendColored("&6Time info for world &c$wn")
        sender.sendColored("&8&l> &e$worldTicks&7/&e24000 ticks")
        sender.sendColored("&8&l> &e$wtAsSecs&7/&e1200 seconds")
        sender.sendColored("&8&l> &e${wtAsMins.roundToDigits(1)}&7/&e20 minutes")
        sender.sendColored("&8&l> &e${percent.roundToDigits(1)}%")
    }

    private fun getAddUsage() : Array<BaseComponent> {
        val comp = TextComponent("${DARK_RED}Usage: ${RED}/time ")

        val subcComp = TextComponent("add ")
        subcComp.color = RED
        subcComp.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/time add ")
        subcComp.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("${GRAY}Click to suggest command"))

        val arg0Comp = TextComponent("<Amount> ")
        arg0Comp.color = RED
        arg0Comp.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("${GRAY}Required: ${GREEN}Yes\n"),
            Text("${DARK_GRAY}- ${GRAY}An amount of time to add to the world's\n"),
            Text("   ${GRAY}current time\n"),
            Text("${DARK_GRAY}- ${GRAY}Usually ticks, which is full seconds multiplied by 20\n"),
            Text("${DARK_GRAY}- ${GRAY}Otherwise, an amount of time followed by s or m")
        )

        val arg1Comp = TextComponent("[World]")
        arg1Comp.color = RED

        comp.addExtra(subcComp)
        comp.addExtra(arg0Comp)
        comp.addExtra(arg1Comp)

        return ComponentBuilder(comp).create()
    }

    private fun getSetUsage() : Array<BaseComponent> {
        val comp = TextComponent("${DARK_RED}Usage: ${RED}/time ")

        val subcComp = TextComponent("set ")
        subcComp.color = RED
        subcComp.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/time set ")
        subcComp.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("${GRAY}Click to suggest command"))

        val arg0Comp = TextComponent("<")
        arg0Comp.color = RED
        val arg0FirstComp = TextComponent("Amount")
        arg0FirstComp.color = RED
        arg0FirstComp.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("${GRAY}Required: ${GREEN}Yes ${GRAY}or ${GREEN}Preset\n"),
            Text("${DARK_GRAY}- ${GRAY}An amount of ticks to set the world's\n"),
            Text("   ${GRAY}current time to\n"),
            Text("${DARK_GRAY}- ${GRAY}Usually ticks, which is full seconds multiplied by 20")
        )
        val arg0Between = TextComponent("/")
        arg0Between.color = DARK_GRAY
        val arg0SecondComp = TextComponent("Preset")
        arg0SecondComp.color = RED
        arg0SecondComp.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("${GRAY}Required: ${GREEN}Yes ${GRAY}or ${GREEN}Amount\n"),
            Text("${GRAY}One of the following: ${YELLOW}day${DARK_GRAY},${YELLOW}noon${DARK_GRAY},${YELLOW}sunset\n"),
            Text("  ${YELLOW}night${DARK_GRAY},${YELLOW}midnight${DARK_GRAY},${YELLOW}sunrise")
        )
        val arg0Postfix = TextComponent("> ")
        arg0Postfix.color = RED

        arg0Comp.addExtra(arg0FirstComp)
        arg0Comp.addExtra(arg0Between)
        arg0Comp.addExtra(arg0SecondComp)
        arg0Comp.addExtra(arg0Postfix)

        val arg1Comp = TextComponent("[World]")
        arg1Comp.color = RED

        comp.addExtra(subcComp)
        comp.addExtra(arg0Comp)
        comp.addExtra(arg1Comp)

        return ComponentBuilder(comp).create()
    }

    private fun handleEmptyArgs(context: CommandContext) = displayWorldTime(context.sender)

    private fun handleAddSubcommand(context: CommandContext) {
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
            return sender.sendColored("&c\"$arg1\" &4should be an integer as game ticks or a preset")
        }

        var world = sender.getCurrentWorldOrDefault()

        if (args.size > 2) {
            val arg2 = args[2]
            world = Bukkit.getWorld(arg2)
                ?: return sender.sendColored("&4Unknown world &c\"${arg2}\"")
        }

        if (lacksPermissionToSetWorldTime(sender, world))
            return context.sendNoPermission(sender, "essentials.time.world.${world.name}")

        val oldTime = world.time

        world.time = oldTime + addend

        val newTime = world.time
        val percent = ((newTime / 24000.0) * 100).roundToDigits(2)
        val wn = world.name
        val oldTimeF = util.convertTicksTo24Hour(oldTime, withColor=true)
        val newTimeF = util.convertTicksTo24Hour(newTime, withColor=true)

        sender.sendColored(
            "&8&l> &c$addend ticks &6added to world &c${wn}'s &6time",
            "&8&l> &6New time is &c$newTime ticks&6, or &c${percent}% &6 of the day",
            "&8&l> &6Converted to real time $oldTimeF &8-> $newTimeF"
        )
    }

    private fun handleAddSeconds(context: CommandContext, input: String) {
        val args = context.args
        val sender = context.sender
        val sansSuffix = input.lowercase().removeSuffix("s")
        val value = sansSuffix.toIntOrNull()
            ?: return sender.sendColored("&4Time in seconds must be an integer followed by the letter s")
        var world = sender.getCurrentWorldOrDefault()

        if (args.size > 2) {
            val arg2 = args[2]
            world = Bukkit.getWorld(arg2)
                ?: return sender.sendColored("&4Unknown world &c\"$arg2\"")
        }

        if (lacksPermissionToSetWorldTime(sender, world))
            return context.sendNoPermission(sender, "essentials.time.world.${world.name}")

        val addend = value * 20
        val oldTime = world.time

        world.time = oldTime + addend

        val newTime = world.time
        val percent = ((newTime / 24000.0) * 100).roundToDigits(2)
        val wn = world.name
        val util = getTimeUtility()
        val oldTimeF = util.convertTicksTo24Hour(oldTime, withColor=true)
        val newTimeF = util.convertTicksTo24Hour(newTime, withColor=true)

        sender.sendColored(
            "&8&l> &c$value seconds &8(&c$addend ticks&8) &6added to world &c${wn}'s &6time",
            "&8&l> &6New time is &c$newTime ticks&6, or &c${percent}% &6 of the day",
            "&8&l> &6Converted to real time $oldTimeF &8-> $newTimeF"
        )
    }

    private fun handleAddMinutes(context: CommandContext, input: String) {
        val args = context.args
        val sender = context.sender
        val sansSuffix = input.lowercase().removeSuffix("m")
        val value = sansSuffix.toIntOrNull()
            ?: return sender.sendColored("&4Time in minutes must be an integer followed by the letter m")

        var world = sender.getCurrentWorldOrDefault()

        if (args.size > 2) {
            val arg2 = args[2]
            world = Bukkit.getWorld(arg2)
                ?: return sender.sendColored("&4Unknown world &c\"$arg2\"")
        }

        if (lacksPermissionToSetWorldTime(sender, world))
            return context.sendNoPermission(sender, "essentials.time.world.${world.name}")

        val addend = value * 60 * 20
        val oldTime = world.time

        world.time = oldTime + addend

        val newTime = world.time
        val percent = ((newTime / 24000.0) * 100).roundToDigits(2)
        val wn = world.name
        val util = getTimeUtility()
        val oldTimeF = util.convertTicksTo24Hour(oldTime, withColor=true)
        val newTimeF = util.convertTicksTo24Hour(newTime, withColor=true)

        sender.sendColored(
            "&8&l> &c$value minutes &8(&c$addend ticks&8) &6added to world &c${wn}'s &6time",
            "&8&l> &6New time is &c$newTime ticks&6, or &c${percent}% &6 of the day",
            "&8&l> &6Converted to real time $oldTimeF &8-> $newTimeF"
        )
    }

    private fun handleQuerySubcommand(context: CommandContext) {
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
                ?: return sender.sendColored("&4Unknown world &c\"$arg2\"")
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

        sender.sendColored("&6$qn&8: &c$query")

        val dayLength = "&8- &c24,000 ticks &6is the length of a full Minecraft day"

        if (arg1.equalsIc("day")) {
            sender.sendColored(
                "&8- &6Day is the result of world's &cfull time &6\u00F7 &c24,000",
                dayLength
            )
        } else if (arg1.equalsIc("daytime")) {
            sender.sendColored(
                "&8- &6Daytime is the amount of ticks since the day began &8(&c0 ticks&8)",
                dayLength
            )
        } else if (arg1.equalsIc("full")) {
            // TODO Print info about full time
        } else if (arg1.equalsIc("gametime")) {
            sender.sendColored(
                "&8- &6Gametime is the age of the game world in ticks",
                dayLength
            )
        }
    }

    private fun handleSetSubcommand(context: CommandContext) {
        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(PERM_SET))
            return context.sendNoPermission(sender, PERM_SET)

        if (args.size == 1)
            return sender.sendChatComponents(getSetUsage())

        val arg1 = args[1]

        if (arg1.equalsIc("day", "noon", "sunset", "dusk", "night", "midnight", "sunrise", "dawn")) {
            return handleSetToPresetSubcommand(context)
        }

        val newTicks = arg1.toLongOrNull()
            ?: return sender.sendColored("&c\"$arg1\" &4should be an integer of game ticks (20 per full second)")

        if (newTicks < 0)
            return sender.sendColored("&4New time cannot be negative ticks. Specify a positive value of at least 0.")

        if (newTicks > 24000)
            return sender.sendColored("&4New time should not exceed &c24,000 ticks&4, the length of a full day")

        var world = sender.getCurrentWorldOrDefault()

        if (args.size > 2) {
            val arg2 = args[2]
            world = Bukkit.getWorld(arg2)
                ?: return sender.sendColored("&4Unknown world &c\"$arg2\"")
        }

        if (lacksPermissionToSetWorldTime(sender, world))
            return context.sendNoPermission(sender, "essentials.time.world.${world.name}")

        val oldTime = world.time
        world.time = newTicks

        sender.sendColored("&6World &c${world.name}'s &6time went from &c$oldTime&8->&c$newTicks")
    }

    private fun handleSetToPresetSubcommand(context: CommandContext) {
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
                ?: return sender.sendColored("&4Unknown world &c\"$arg2\"")
        }

        if (lacksPermissionToSetWorldTime(sender, world))
            return context.sendNoPermission(sender, "essentials.time.world.${world.name}")

        val oldTime = world.time
        world.time = newTicks

        sender.sendColored("&6World &c${world.name}'s &6time went from &c$oldTime&8->&c$newTicks")
    }

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