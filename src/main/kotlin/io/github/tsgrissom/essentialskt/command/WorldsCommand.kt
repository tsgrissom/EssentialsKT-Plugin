package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.gui.WorldListGui
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendChatComponents
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class WorldsCommand : CommandBase() {

    companion object {
        const val PERM = "essentials.world"
    }

    private fun generateWorldTextComponent(w: World) : TextComponent {
        val comp = TextComponent(w.name)
        comp.color = YELLOW

        return comp
    }

    private fun getWorldsAsComponents() : Array<BaseComponent> {
        val text = TextComponent("Worlds")
        text.color = GRAY;

        val delimiter = TextComponent(": ")
        delimiter.color = DARK_GRAY;

        text.addExtra(delimiter)

        val worlds = Bukkit.getWorlds()

        for ((i, world) in worlds.withIndex()) {
            text.addExtra(generateWorldTextComponent(world))
            if (i != (worlds.size - 1)) {
                val entryDelimiter = TextComponent(", ")
                entryDelimiter.color = GRAY
                text.addExtra(entryDelimiter)
            }
        }

        return ComponentBuilder(text).create()
    }

    private fun getWorldsAsPlainText() : String {
        var text = "Worlds: "
        val worlds = Bukkit.getWorlds()

        for ((i, world) in worlds.withIndex()) {
            text += world.name
            if (i != (worlds.size - 1))
                text += ", "
        }

        return text
    }

    override fun execute(context: CommandContext) {
        val sender = context.sender

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        if (sender is ConsoleCommandSender) {
            return sender.sendMessage(getWorldsAsPlainText())
        }

        if (sender is Player) {
            if (context.hasFlag(FLAG_GRAPHICAL))
                return WorldListGui().show(sender)

            return sender.sendChatComponents(getWorldsAsComponents())
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> {
        val tab = mutableListOf<String>()

        if (sender.lacksPermission(PERM))
            return tab

        tab.addAll(listOf("--gui"))

        return tab.sorted().toMutableList()
    }
}