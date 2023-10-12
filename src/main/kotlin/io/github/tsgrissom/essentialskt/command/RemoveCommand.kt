package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.pluginapi.command.*
import io.github.tsgrissom.pluginapi.command.help.CommandHelpGenerator
import io.github.tsgrissom.pluginapi.command.help.SubcommandArgumentHelp
import io.github.tsgrissom.pluginapi.command.help.SubcommandHelp
import io.github.tsgrissom.pluginapi.extension.*
import io.github.tsgrissom.pluginapi.chat.ClickableText
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.ChatColor.DARK_GRAY as D_GRAY
import net.md_5.bungee.api.ChatColor.DARK_RED as D_RED
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class RemoveCommand : CommandBase() {

    // MARK: Dependency Injection
    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getEntityUtility() =
        getPlugin().getEntityUtility()

    // MARK: Static Declarations
    companion object {
        const val PERM = "essentials.remove"
    }

    // MARK: Text Helper Functions
    private fun generateHelpText(context: CommandContext) : Array<BaseComponent> {
        return CommandHelpGenerator(context)
            .withAliases("killall", "eremove")
            .withSubcommand(
                SubcommandHelp
                    .compose("")
                    .withArgument(
                        SubcommandArgumentHelp
                            .compose("Type")
                            .required(true)
                            .hoverText(
                                "${GRAY}Type${D_GRAY}: ${RED}Required",
                                "${GRAY}Type provided can either be a valid entity type",
                                " ${GRAY}or a preset entity group"
                            )
                    )
                    .withArgument(
                        SubcommandArgumentHelp
                            .compose("Radius OR World")
                            .required(false)
                            .hoverText(
                                "${GRAY}Type${D_GRAY}: ${GREEN}Optional",
                                "${GRAY}One of either${D_GRAY}:",
                                " ${D_GRAY}- ${GRAY}An integer or decimal radius within which",
                                "   ${GRAY}to clear entities",
                                " ${D_GRAY}- ${GRAY}A world name to clear the entities in"
                            )
                    )
                    .withDescription(
                        "Kill entities of a specified type. By default all",
                        "that match in the world, otherwise within a set",
                        "radius or another world"
                    )
            )
            .withSubcommand(
                SubcommandHelp
                    .compose("types")
                    .withDescription("List valid special grouped types")
            )
            .toComponents()
    }

    private fun getValidGroupedTypes() =
        arrayOf(
            "all", "tamed", "named", "drops", "arrows",
            "boats", "minecarts", "exp", "paintings",
            "itemframes", "endercrystals", "monsters",
            "animals", "ambient", "mobs"
        )

    private fun getGroupedTypesAsComponents(context: CommandContext) : Array<BaseComponent> {
        val label = context.label
        val builder = ComponentBuilder()
            .appendc("Types", GOLD)
            .appendc(": ", D_GRAY)

        for ((i, type) in getValidGroupedTypes().withIndex()) {
            val clickText = ClickableText
                .compose(type)
                .color(YELLOW)
                .action(ClickEvent.Action.SUGGEST_COMMAND)
                .value("/$label $type ")
            builder.append(clickText.toComponent())

            if (i != (getValidGroupedTypes().size - 1))
                builder.appendc(",", GRAY)
        }

        val click = ClickableText
            .compose("/list mobs")
            .color(YELLOW)
            .action(ClickEvent.Action.RUN_COMMAND)
            .value("/list mobs")

        builder
            .append("\n")
            .appendc("Do ", GOLD)
            .append(click.toComponent())
            .appendc(" to display valid mob specifiers", GOLD)


        return builder.create()
    }

    // MARK: Operational Helper Functions
    private fun removeEntities(
        sender: CommandSender,
        world: World,
        targetedType: String,
        radius: Double
    ) {
        val validGroups = getValidGroupedTypes()
        val validMobs = getEntityUtility().getMobTypes()
            .map { it.name.lowercase() }
            .toSet()

        if (!validGroups.contains(targetedType.lowercase()) && !validMobs.contains(targetedType.lowercase())) {
            sender.sendMessage(
                "${D_RED}Unknown entity type ${RED}\"$targetedType\"",
                "${D_RED}Do ${RED}/remove types ${D_RED}to view valid groups, or ${RED}/list mobs ${D_RED}to view valid mobs."
            )
            return
        }

        val radiusStr: String = if (radius < 0) "infinite" else radius.toString()

        Bukkit.broadcastMessage("TODO: Remove entities in world ${world.name} of type $targetedType within $radiusStr radius")
    }

    // MARK: Command Body
    override fun execute(context: CommandContext) {
        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        if (args.isEmpty())
            return sender.sendChatComponents(generateHelpText(context))

        if (args.size == 1)
            handleOneArgument(context)
        else
            handleMoreThanOneArgument(context)
    }

    // MARK: Handlers
    private fun handleOneArgument(context: CommandContext) {
        val args = context.args
        val label = context.label
        val sender = context.sender
        val sub = args[0]

        if (sub.equalsIc("help", "h", "?")) {
            return sender.sendChatComponents(generateHelpText(context))
        } else if (sub.equalsIc("types", "groups", "list", "ls")) {
            val types = getGroupedTypesAsComponents(context)
            return sender.spigot().sendMessage(*types)
        }

        if (sender is ConsoleCommandSender)
            return sender.sendMessage("${D_RED}Console Usage: ${RED}/${label} <Type> <World>")
        if (sender !is Player)
            return

        val p: Player = sender
        val w = p.getCurrentWorldOrDefault()

        removeEntities(sender, w, sub, -1.0)
    }

    private fun handleMoreThanOneArgument(context: CommandContext) {
        val args = context.args
        val label = context.label
        val sender = context.sender
        val sub = args[0]
        val arg1 = args[1]

        var arg1d: Double? = null

        try {
            arg1d = arg1.toDoubleOrNull()

            if (arg1d != null && arg1d <= 0.0)
                return sender.sendMessage("${D_RED}Removal range should be greater than zero. Do ${RED}/${label} ? ${D_RED}for help.")
        } catch (ignored: NumberFormatException) {}

        val radius = arg1d ?: -1.0
        var world = sender.getCurrentWorldOrDefault()

        if (radius < 0) {
            world = Bukkit.getWorld(arg1)
                ?: return sender.sendMessage("${D_RED}Could not find world ${RED}\"$arg1\"")
        }

        removeEntities(sender, world, sub, radius)
    }

    // MARK: Tab Completion Handler
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val tab = mutableListOf<String>()

        if (sender.lacksPermission(PERM))
            return tab

        val suggestSub = mutableListOf<String>()
        suggestSub.addAll(getValidGroupedTypes())
        suggestSub.addAll(getEntityUtility().getAllMobKeys())

        val len = args.size

        if (len > 0) {
            val sub = args[0]

            if (len == 1) {
                StringUtil.copyPartialMatches(sub, suggestSub, tab)
            } else if (len == 2) {
                if (suggestSub.contains(sub.lowercase())) {
                    StringUtil.copyPartialMatches(args[1], getWorldNamesToMutableList(), tab)
                }
            }
        }

        return tab.sorted().toMutableList()
    }
}