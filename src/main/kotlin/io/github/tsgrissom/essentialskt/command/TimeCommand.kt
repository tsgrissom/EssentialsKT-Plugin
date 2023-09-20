package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.command.help.CommandHelpGenerator
import io.github.tsgrissom.pluginapi.command.help.SubcommandArgumentHelp
import io.github.tsgrissom.pluginapi.command.help.SubcommandHelp
import io.github.tsgrissom.pluginapi.extension.*
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TimeCommand : CommandBase() {

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
    }

    private fun getPlugin() = EssentialsKTPlugin.instance ?: error("plugin instance is null")

    /*
     * /time add <long>
     * /time query <day|daytime|gametime>
     * /time set <long|preset>
     */

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
                    .withSuggestion("/$label query day")
            )
            .withSubcommand(
                SubcommandHelp
                    .compose("query daytime")
                    .withSuggestion("/$label query daytime")
            )
            .withSubcommand(
                SubcommandHelp
                    .compose("query gametime")
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

        return help.getHelpAsComponent()
    }

    private fun displayWorldTime(sender: CommandSender) {
        var world = Bukkit.getWorlds()[0]

        if (sender is Player) {
            world = sender.world
        }

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

    private fun handleEmptyArgs(context: CommandContext) = displayWorldTime(context.sender)

    private fun handleAddSubcommand(context: CommandContext) {
        val usage = "&4Usage: &c/time add <AmountInTicks> [WorldName]"
        val args = context.args
        val sender = context.sender

        if (args.size == 1)
            return sender.sendColored(usage)

        val arg1 = args[1]
        val addend: Long

        try {
            addend = arg1.toLong()
        } catch (ignored: NumberFormatException) {
            return sender.sendColored("&c\"$arg1\" &4should be an integer in game ticks (20 per second)")
        }

        var world = sender.getCurrentWorldOrDefault()

        if (args.size > 2) {
            val arg2 = args[2]
            world = Bukkit.getWorld(arg2)
                ?: return sender.sendColored("&4Unknown world &c\"${arg2}\"")
        }

        val currentTime = world.time

        world.time = currentTime + addend

        val newTime = world.time
        val percent = (newTime/24000.0) * 100

        sender.sendColored("&8&l> &c${addend} ticks &6added to world &c${world.name}'s &6time")
        sender.sendColored("&8&l> &6New time is &c${newTime} ticks&6, or &c${percent.roundToDigits(2)}% &6 of the day")
    }

    private fun handleQuerySubcommand(context: CommandContext) {
        // TODO Time query subcommand
    }

    private fun handleSetSubcommand(context: CommandContext) {
        val usage = "&4Usage: &c/time set <AmountInTicks|Preset> [WorldName]"
        val args = context.args
        val sender = context.sender

        if (args.size == 1)
            return sender.sendColored(usage)

        val arg1 = args[1]

        /*
         * day, midnight, night, noon
         */

        // TODO

        val newTicks = arg1.toLongOrNull()
            ?: return sender.sendColored("&c\"$arg1\" &4should be an integer in game ticks (20 per second)")

        if (newTicks < 0)
            return sender.sendColored("&4New time cannot be negative ticks. Specify a positive value of at least 0.")

        if (newTicks > 24000)
            return sender.sendColored("&4New time should not exceed &c24,000 ticks&4, the length of a full day")

        var world = sender.getCurrentWorldOrDefault()

        if (args.size > 2) {
            val arg2 = args[2]
            world = Bukkit.getWorld(arg2)
                ?: return sender.sendColored("&4Unknown world &c\"${arg2}\"")
        }

        val oldTime = world.time
        world.time = newTicks

        sender.sendColored("&8&l> &6World &c${world.name}'s &6time went from &c$oldTime&8->&c$newTicks")
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

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        // TODO Implement /time tab completion
        return mutableListOf()
    }
}