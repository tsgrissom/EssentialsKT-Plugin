package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.pluginapi.chat.ClickTextBuilder
import io.github.tsgrissom.pluginapi.command.*
import io.github.tsgrissom.pluginapi.command.help.CommandHelpBuilder
import io.github.tsgrissom.pluginapi.command.help.SubcHelpBuilder
import io.github.tsgrissom.pluginapi.command.help.SubcParameterBuilder
import io.github.tsgrissom.pluginapi.extension.bukkit.appendc
import io.github.tsgrissom.pluginapi.extension.bukkit.getCurrentWorldOrDefault
import io.github.tsgrissom.pluginapi.extension.bukkit.lacksPermission
import io.github.tsgrissom.pluginapi.extension.bukkit.sendChatComponents
import io.github.tsgrissom.pluginapi.extension.kt.equalsIc
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
    private fun getPlugin() =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()
    private fun getEntityUtility() =
        getPlugin().getEntityUtility()

    // MARK: Static Declarations
    companion object {
        const val PERM = "essentials.remove"
    }

    // MARK: Text Helper Functions
    private fun generateHelpText(context: CommandContext) : Array<BaseComponent> {
        val conf = getConfig()
        val ccErrDetl = conf.getBungeeChatColor(ChatColorKey.ErrorDetail)
        val ccSucc = conf.getBungeeChatColor(ChatColorKey.Success)
        val ccSec = conf.getBungeeChatColor(ChatColorKey.Secondary)
        val ccTert = conf.getBungeeChatColor(ChatColorKey.Tertiary)
        
        return CommandHelpBuilder(context)
            .withAliases("killall", "eremove")
            .withSubcommand(
                SubcHelpBuilder("")
                    .withArgument(
                        SubcParameterBuilder("Type", required=true)
                            .hoverText(
                                "${ccSec}Type${ccTert}: ${ccErrDetl}Required",
                                "${ccSec}Type provided can either be a valid entity type",
                                " ${ccSec}or a preset entity group"
                            )
                    )
                    .withArgument(
                        SubcParameterBuilder("Radius OR World", required=false)
                            .hoverText(
                                "${ccSec}Type${ccTert}: ${ccSucc}Optional",
                                "${ccSec}One of either${ccTert}:",
                                " ${ccTert}- ${ccSec}An integer or decimal radius within which",
                                "   ${ccSec}to clear entities",
                                " ${ccTert}- ${ccSec}A world name to clear the entities in"
                            )
                    )
                    .withDescription(
                        "Kill entities of a specified type. By default all",
                        "that match in the world, otherwise within a set",
                        "radius or another world"
                    )
            )
            .withSubcommand(
                SubcHelpBuilder("types")
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
        val conf = getConfig()
        val ccPrim = conf.getBungeeChatColor(ChatColorKey.Primary)
        val ccSec = conf.getBungeeChatColor(ChatColorKey.Secondary)
        val ccTert = conf.getBungeeChatColor(ChatColorKey.Tertiary)
        val ccVal = conf.getBungeeChatColor(ChatColorKey.Value)
        
        val label = context.label
        val builder = ComponentBuilder()
            .appendc("Types", ccPrim)
            .appendc(": ", ccTert)

        for ((i, type) in getValidGroupedTypes().withIndex()) {
            val data = ClickTextBuilder(type)
                .action(ClickEvent.Action.SUGGEST_COMMAND)
                .color(ccVal)
                .value("/$label $type ")
                .toComponent()
            builder.append(data)

            if (i != (getValidGroupedTypes().size - 1))
                builder.appendc(",", ccSec)
        }

        val suggestedCommand = ClickTextBuilder("/list mobs")
            .action(ClickEvent.Action.RUN_COMMAND)
            .color(ccVal)
            .value("/list mobs")
            .toComponent()

        builder
            .append("\n")
            .appendc("Do ", ccPrim)
            .append(suggestedCommand)
            .appendc(" to display valid mob specifiers", ccPrim)


        return builder.create()
    }

    // MARK: Operational Helper Functions
    private fun removeEntities(
        sender: CommandSender,
        world: World,
        targetedType: String,
        radius: Double
    ) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)
        
        val validGroups = getValidGroupedTypes()
        val validMobs = getEntityUtility().getMobTypes()
            .map { it.name.lowercase() }
            .toSet()

        if (!validGroups.contains(targetedType.lowercase()) && !validMobs.contains(targetedType.lowercase())) {
            sender.sendMessage(
                "${ccErr}Unknown entity type ${ccErrDetl}\"$targetedType\"",
                "${ccErr}Do ${ccErrDetl}/remove types ${ccErr}to view valid groups, or ${ccErrDetl}/list mobs ${ccErr}to view valid mobs."
            )
            return
        }

        val radiusStr: String = if (radius < 0) "infinite" else radius.toString()

        Bukkit.broadcastMessage("TODO: Remove entities in world ${world.name} of type $targetedType within $radiusStr radius")
        // TODO Implement remove entities in world within radius
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
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)

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
            return sender.sendMessage("${ccErr}Console Usage: ${ccErrDetl}/${label} <Type> <World>")
        if (sender !is Player)
            return

        val p: Player = sender
        val w = p.getCurrentWorldOrDefault()

        removeEntities(sender, w, sub, -1.0)
    }

    private fun handleMoreThanOneArgument(context: CommandContext) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)

        val args = context.args
        val label = context.label
        val sender = context.sender
        val sub = args[0]
        val arg1 = args[1]

        var arg1d: Double? = null

        try {
            arg1d = arg1.toDoubleOrNull()

            if (arg1d != null && arg1d <= 0.0)
                return sender.sendMessage("${ccErr}Removal range should be greater than zero. Do ${ccErrDetl}/${label} ? ${ccErr}for help.")
        } catch (ignored: NumberFormatException) {}

        val radius = arg1d ?: -1.0
        var world = sender.getCurrentWorldOrDefault()

        if (radius < 0) {
            world = Bukkit.getWorld(arg1)
                ?: return sender.sendMessage("${ccErr}Could not find world ${ccErrDetl}\"$arg1\"")
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