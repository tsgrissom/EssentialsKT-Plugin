package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.gui.ListEntitiesGui
import io.github.tsgrissom.essentialskt.gui.ListOnlinePlayersGui
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.*
import io.github.tsgrissom.pluginapi.chat.HoverableText
import io.github.tsgrissom.pluginapi.utility.EntityUtility
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class ListCommand : CommandBase() {

    // MARK: Static Declarations
    companion object {
        const val PERM = "essentialskt.list"
        const val PERM_PLAYERS = "essentialskt.list.players"
        const val PERM_MOBS = "essentialskt.list.mobs"

        private val VALID_SUBC = listOf(
            "entities", "entity", "entitytype",
            "mobs", "mob",
            "players", "online",
            "worlds", "world"
        )
    }

    // MARK: Text Helper Functions
    private fun getAvailableLists() : Array<String> =
        arrayOf(
            "&6Available Lists",
            "&bOptional flag available to players",
            "&8&l> &eentities",
            "&8&l> &emobs",
            "&8&l> &eplayers &b--gui",
            "&8&l> &eworlds &b--gui"
        )

    private fun generatePlayerListAsTextComponents() : Array<BaseComponent> {
        val onlinePlayers = Bukkit.getOnlinePlayers()
        val count = onlinePlayers.size
        val max = Bukkit.getMaxPlayers()
        val builder = ComponentBuilder()
            .appendc("Player List ", GRAY)

        if (onlinePlayers.isEmpty()) {
            builder
                .appendc("(", DARK_GRAY)
                .appendc("None", RED)
                .appendc(")", DARK_GRAY)

            return builder.create()
        }

        builder
            .appendc("(", DARK_GRAY)
            .appendc("$count", GOLD)
            .appendc("/", DARK_GRAY)
            .appendc("$max", GOLD)
            .appendc(") ", DARK_GRAY)

        for ((index, p) in onlinePlayers.withIndex()) {
            val loc = p.location
            val x = loc.x.roundToDigits(1)
            val y = loc.y.roundToDigits(1)
            val z = loc.z.roundToDigits(1)
            builder.append(
                HoverableText
                    .compose(p.name)
                    .color(YELLOW)
                    .hoverText(
                        "&8&l> &7Nickname&8: &r${p.displayName}",
                        "&8&l> &7UUID&8: &e${p.getUniqueString()}",
                        "&8&l> &7Current World&8: &e${p.world.name}",
                        "&8&l> &7Location &cX&aY&bZ&8: &c$x &a$y &b$z"
                    )
                    .toComponent()
            )
            if (index != (onlinePlayers.size - 1))
                builder.append(" ")
        }

        return builder.create()
    }

    // MARK: Command Body
    override fun execute(context: CommandContext) {
        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        if (args.isEmpty())
            return getAvailableLists().forEach { sender.sendColored(it) }

        val sub = args[0]

        when (sub.lowercase()) {
            "entities", "entity", "entitytype" -> handleSubcEntities(context)
            "mobs", "mob" -> handleSubcMobs(context)
            "players", "pl", "online" -> handleSubcPlayers(context)
            "worlds", "world" -> handleSubcWorlds(context)
            else -> sender.sendColored("&4Unknown list type &c\"$sub\"&4. Do &c/ls &4to view valid types.")
        }
    }

    // MARK: Handlers
    private fun handleSubcPlayers(context: CommandContext) {
        val sender = context.sender

        if (sender.lacksPermission(PERM_PLAYERS))
            return context.sendNoPermission(sender, PERM_PLAYERS)

        val hasGraphicalFlag = context.hasFlag(FLAG_GRAPHICAL)

        if (sender is ConsoleCommandSender) {
            handleSubcPlayersText(context)

            if (hasGraphicalFlag)
                sender.sendColored("&4Console cannot view GUIs")

            return
        }
        if (sender !is Player) {
            return
        }

        return if (hasGraphicalFlag)
            ListOnlinePlayersGui().show(sender)
        else
            handleSubcPlayersText(context)
    }

    private fun handleSubcEntities(context: CommandContext) {
        val sender = context.sender

        if (context.hasFlag(FLAG_GRAPHICAL))
            if (sender is Player)
                return ListEntitiesGui(EntityType.entries).show(sender)
            else if (sender is ConsoleCommandSender)
                return sender.sendColored("&4Console cannot open GUIs")

        // TODO Send text list of entities
    }

    private fun handleSubcPlayersText(context: CommandContext) {
        val sender = context.sender

        sender.sendChatComponents(generatePlayerListAsTextComponents())
    }

    private fun createMobListAsTextComponents() : Array<BaseComponent> {
        // TODO Create mob list as text components
        return ComponentBuilder().create()
    }

    private fun handleSubcMobs(context: CommandContext) {
        val sender = context.sender

        if (sender.lacksPermission(PERM_MOBS))
            return context.sendNoPermission(sender, PERM_MOBS)

        if (context.hasFlag(FLAG_GRAPHICAL))
            if (sender is Player)
                return ListEntitiesGui(EntityUtility().getMobTypes()).show(sender)
            else if (sender is ConsoleCommandSender)
                return sender.sendColored("&4Console cannot open GUIs")

        sender.sendMessage("TODO Display mobs as text")
        // TODO Display mobs as text components
    }

    private fun handleSubcWorlds(context: CommandContext) {
        var command = "worlds"

        if (context.hasFlag(FLAG_GRAPHICAL))
            command += " --gui"

        context.performCommand(command)
    }

    // MARK: Tab Completion Handler
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val suggestSub = mutableListOf("entities", "mobs", "players", "worlds")
        val suggestGraphical = mutableListOf("--gui", "-g")
        val tab = mutableListOf<String>()

        val len = args.size

        if (len > 0) {
            val sub = args[0]

            if (len == 1) {
                if (!suggestSub.contains(sub)) {
                    StringUtil.copyPartialMatches(sub, suggestSub, tab)
                }
            } else if (len == 2) {
                if (sub.equalsIc(VALID_SUBC)) {
                    StringUtil.copyPartialMatches(args[1], suggestGraphical, tab)
                }
            }
        }

        return tab.sorted().toMutableList()
    }
}