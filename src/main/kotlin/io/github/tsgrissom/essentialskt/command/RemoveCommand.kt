package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.equalsIc
import io.github.tsgrissom.pluginapi.extension.getCurrentWorldOrDefault
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import io.github.tsgrissom.pluginapi.misc.ClickableText
import net.md_5.bungee.api.ChatColor
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

    /*
     * /remove <Type> [Radius/World]
     * Valid Types:
     * all (confirm))
     * tamed
     * named
     * drops
     * arrows
     * boats
     * minecarts
     * xp
     * paintings
     * itemframes
     * endercrystals
     * monsters
     * animals
     * ambient
     * mobs
     * [mobType]
     */

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getEntityUtility() =
        getPlugin().entityUtility

    private val perm = "essentials.remove"

    private fun getHelpText(label: String) : Array<String> =
        arrayOf(
            "   &6Command Help &8-> &e/${label}",
            "&8&l> &e/${label} <Type> [Radius OR World]",
            "    &7Kill entities of a specified type. By default all",
            "     &7that match in the world, otherwise within a set",
            "     &7radius or another world",
            "&8&l> &e/${label} types",
            "    &7List valid special grouped types",
            "&8&l> &e/ls mobs",
            "    &7List valid mob types"
        )
    private fun sendHelp(context: CommandContext) =
        getHelpText(context.label).forEach { context.sender.sendColored(it) }

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
            .append("Types").color(ChatColor.GOLD)
            .append(": ").color(ChatColor.DARK_GRAY)

        for ((i, type) in getValidGroupedTypes().withIndex()) {
            val clickText = ClickableText
                .compose("&e$type")
                .action(ClickEvent.Action.SUGGEST_COMMAND)
                .value("/$label $type ")
            builder.append(clickText.toTextComponent())

            if (i != (getValidGroupedTypes().size - 1))
                builder.append(",").color(ChatColor.GRAY)
        }

        val clickText = ClickableText
            .compose("&e/list mobs")
            .action(ClickEvent.Action.RUN_COMMAND)
            .value("/list mobs")

        builder
            .append("\n")
            .append("Do ").color(ChatColor.GOLD)
            .append(clickText.toTextComponent())
            .append(" to display valid mob specifiers").color(ChatColor.GOLD)


        return builder.create()
    }

    override fun execute(context: CommandContext) {
        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(perm))
            return context.sendNoPermission(sender, perm)

        if (args.isEmpty())
            return sendHelp(context)

        if (args.size == 1)
            handleOneArgument(context)
        else
            handleMoreThanOneArgument(context)
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val tab = mutableListOf<String>()

        if (sender.lacksPermission(perm))
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

    private fun handleOneArgument(context: CommandContext) {
        val args = context.args
        val label = context.label
        val sender = context.sender
        val sub = args[0]

        if (sub.equalsIc("help", "h", "?")) {
            return sendHelp(context)
        } else if (sub.equalsIc("types", "groups", "list", "ls")) {
            val types = getGroupedTypesAsComponents(context)
            return sender.spigot().sendMessage(*types)
        }

        if (sender is ConsoleCommandSender)
            return sender.sendColored("&4Console Usage: &c/${label} <Type> <World>")
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
                return sender.sendColored("&4Removal range should be greater than zero. Do &c/${label} ? &4for help.")
        } catch (ignored: NumberFormatException) {}

        val radius = arg1d ?: -1.0
        var world = sender.getCurrentWorldOrDefault()

        if (radius < 0) {
            world = Bukkit.getWorld(arg1)
                ?: return sender.sendColored("&4Could not find world &c\"$arg1\"")
        }

        removeEntities(sender, world, sub, radius)
    }

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
            sender.sendColored("&4Unknown entity type &c\"$targetedType\"")
            sender.sendColored("&4Do &c/remove types &4to view valid groups, or &c/list mobs &4to view valid mobs.")
            return
        }

        val radiusStr: String = if (radius < 0) "infinite" else radius.toString()

        Bukkit.broadcastMessage("TODO: Remove entities in world ${world.name} of type $targetedType within $radiusStr radius")
    }
}