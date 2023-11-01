package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.essentialskt.gui.ListEntitiesGui
import io.github.tsgrissom.essentialskt.gui.ListOnlinePlayersGui
import io.github.tsgrissom.pluginapi.chat.HoverableText
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.command.flag.CommandFlagParser
import io.github.tsgrissom.pluginapi.command.flag.ValidCommandFlag
import io.github.tsgrissom.pluginapi.extension.*
import io.github.tsgrissom.pluginapi.utility.EntityUtility
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

// TODO Support config-driven chat colors
class ListCommand : CommandBase() {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()

    // MARK: Static Declarations
    companion object {
        const val PERM          = "essentialskt.list"
        const val PERM_ENTITIES = "essentialskt.list.entities"
        const val PERM_MOBS     = "essentialskt.list.mobs"
        const val PERM_PLAYERS  = "essentialskt.list.players"

        private val VALID_SUBC = listOf(
            "entities", "entity", "entitytype",
            "mobs", "mob",
            "players", "online",
            "worlds", "world"
        )
    }

    // MARK: Text Helper Functions
    private fun getAvailableLists() : Array<String> {
        val conf = getConfig()
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccTert = conf.getChatColor(ChatColorKey.Tertiary)
        val ccType = conf.getChatColor(ChatColorKey.Type)
        val ccVal = conf.getChatColor(ChatColorKey.Value)

        return arrayOf(
            "${ccPrim}Available Lists",
            "${ccType}Optional flag available to players",
            "${ccTert}> ${ccVal}entities ${ccType}--gui",
            "${ccTert}> ${ccVal}mobs ${ccType}--gui",
            "${ccTert}> ${ccVal}players ${ccType}--gui",
            "${ccTert}> ${ccVal}worlds ${ccType}--gui"
        )
    }

    private fun generatePlayerListAsTextComponents() : Array<BaseComponent> {
        val conf = getConfig()
        val ccErrDetl = conf.getBungeeChatColor(ChatColorKey.ErrorDetail)
        val ccSec = conf.getBungeeChatColor(ChatColorKey.Secondary)
        val ccTert = conf.getBungeeChatColor(ChatColorKey.Tertiary)
        val ccVal = conf.getBungeeChatColor(ChatColorKey.Value)
        
        val onlinePlayers = Bukkit.getOnlinePlayers()
        val count = onlinePlayers.size
        val max = Bukkit.getMaxPlayers()
        val builder = ComponentBuilder()
            .appendc("Player List ", ccSec)

        if (onlinePlayers.isEmpty()) {
            builder
                .appendc("(", ccTert)
                .appendc("None", ccErrDetl)
                .appendc(")", ccTert)

            return builder.create()
        }

        builder
            .appendc("(", ccTert)
            .appendc("$count", ccVal)
            .appendc("/", ccTert)
            .appendc("$max", ccVal)
            .appendc(") ", ccTert)

        for ((index, p) in onlinePlayers.withIndex()) {
            val loc = p.location
            val x = loc.x.roundToDigits(1)
            val y = loc.y.roundToDigits(1)
            val z = loc.z.roundToDigits(1)
            val dn = p.displayName
            val uuid = p.getUniqueString()
            val wn = p.world.name
            builder.append(
                HoverableText
                    .compose(p.name)
                    .color(YELLOW)
                    .hoverText(
                        "${ccTert}> ${ccSec}Nickname${ccTert}: ${RESET}$dn",
                        "${ccTert}> ${ccSec}UUID${ccTert}: ${ccVal}$uuid",
                        "${ccTert}> ${ccSec}Current World${ccTert}: ${ccVal}$wn",
                        "${ccTert}> ${ccSec}Location ${RED}X${GREEN}Y${AQUA}Z${ccTert}: ${RED}$x ${GREEN}$y ${AQUA}$z"
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
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)

        val args = context.args
        val sender = context.sender
        val flagGui = ValidCommandFlag.FLAG_GRAPHICAL
        val flags = CommandFlagParser(args, flagGui)

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        if (args.isEmpty())
            return getAvailableLists().forEach { sender.sendMessage(it) }

        val sub = args[0]

        when (sub.lowercase()) {
            "entities", "entity", "entitytype" -> handleSubcEntities(context, flags)
            "mobs", "mob" -> handleSubcMobs(context, flags)
            "players", "pl", "online" -> handleSubcPlayers(context, flags)
            "worlds", "world" -> handleSubcWorlds(context, flags)
            else -> {
                sender.sendMessage("${ccErr}Unknown list type ${ccErrDetl}\"$sub\"${ccErr}. Do ${ccErrDetl}/ls ${ccErr}to view valid types.")
            }
        }
    }

    // MARK: Handlers
    private fun handleSubcPlayers(context: CommandContext, flags: CommandFlagParser) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)

        val sender = context.sender

        if (sender.lacksPermission(PERM_PLAYERS))
            return context.sendNoPermission(sender, PERM_PLAYERS)

        val hasGraphicalFlag = flags.wasPassed(ValidCommandFlag.FLAG_GRAPHICAL)

        if (sender is ConsoleCommandSender) {
            handleSubcPlayersText(context)

            if (hasGraphicalFlag)
                sender.sendMessage("${ccErr}Console cannot view GUIs")

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

    private fun handleSubcEntities(context: CommandContext, flags: CommandFlagParser) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)

        val sender = context.sender

        if  (sender.lacksPermission(PERM_ENTITIES))
            return context.sendNoPermission(sender, PERM_ENTITIES)

        if (flags.wasPassed(ValidCommandFlag.FLAG_GRAPHICAL))
            if (sender is Player)
                return ListEntitiesGui().show(sender)
            else if (sender is ConsoleCommandSender)
                return sender.sendMessage("${ccErr}Console cannot open GUIs")

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

    private fun handleSubcMobs(context: CommandContext, flags: CommandFlagParser) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)

        val sender = context.sender

        if (sender.lacksPermission(PERM_MOBS))
            return context.sendNoPermission(sender, PERM_MOBS)

        if (flags.wasPassed(ValidCommandFlag.FLAG_GRAPHICAL))
            if (sender is Player)
                return ListEntitiesGui(EntityUtility().getMobTypes(), "Mobs").show(sender)
            else if (sender is ConsoleCommandSender)
                return sender.sendMessage("${ccErr}Console cannot open GUIs")

        sender.sendMessage("TODO Display mobs as text")
        // TODO Display mobs as text components
    }

    private fun handleSubcWorlds(context: CommandContext, flags: CommandFlagParser) {
        var command = "worlds"

        if (flags.wasPassed(ValidCommandFlag.FLAG_GRAPHICAL))
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