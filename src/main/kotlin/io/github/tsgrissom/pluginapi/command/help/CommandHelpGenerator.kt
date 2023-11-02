package io.github.tsgrissom.pluginapi.command.help

import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.bukkit.appendc
import io.github.tsgrissom.pluginapi.extension.kt.translateColor
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission

private fun CommandSender.hasPermission(permission: Permission?): Boolean {
    return if (permission == null)
        true
    else
        !this.hasPermission(permission)
}

class CommandHelpGenerator(val context: CommandContext) {

    private val label: String = context.label
    private val sender: CommandSender = context.sender
    private val aliases: MutableList<String> = mutableListOf()
    private val subcommands: MutableList<SubcommandHelp> = mutableListOf()

    fun withAliases(vararg s: String) : CommandHelpGenerator {
        aliases.addAll(s)
        return this
    }

    fun withSubcommand(sub: SubcommandHelp) : CommandHelpGenerator {
        subcommands.add(sub)
        return this
    }

    fun toComponents() : Array<BaseComponent> {
        val comp = ComponentBuilder()
            .append(getTitleAsComponent())
            .append(getAllPermittedSubcommandsAsComponent())
        return comp.create()
    }

    private fun getTitleAsComponent() : TextComponent {
        val comp = TextComponent("   ")
        val titleComponent = TextComponent("Command Help")
        val punctuationComponent = TextComponent(": ")
        val labelComponent = TextComponent("/$label")

        titleComponent.color = GOLD
        punctuationComponent.color = GRAY
        labelComponent.color = YELLOW

        if (aliases.isNotEmpty()) {
            val hoverBuilder = ComponentBuilder()
                .appendc("Aliases: ", GRAY)
            for ((i, a) in aliases.withIndex()) {
                hoverBuilder.appendc(a, YELLOW)
                if (i != (aliases.size - 1))
                    hoverBuilder.appendc(",", GRAY)
            }

            val e = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(hoverBuilder.create()))
            labelComponent.hoverEvent = e
        }

        comp.addExtra(titleComponent)
        comp.addExtra(punctuationComponent)
        comp.addExtra(labelComponent)
        comp.addExtra("\n")

        return comp
    }

    private fun getSubcommandAsComponent(sub: SubcommandHelp) : TextComponent {
        val nameAsComponent = sub.toComponent()
        val text = TextComponent()
        val prefix = TextComponent("> ")

        prefix.color = DARK_GRAY
        prefix.isBold = true

        text.color = YELLOW

        text.addExtra(prefix)
        text.addExtra("/$label ")
        text.addExtra(nameAsComponent)
        text.addExtra(if (sub.name.isNotEmpty()) " " else "")

        for (arg in sub.arguments) {
            text.addExtra(TextComponent(*arg.toComponents()))
            text.addExtra(" ")
        }

        if (sub.description.isNotEmpty()) {
            for ((i, line) in sub.description.withIndex()) {
                val spaceCount = if (i > 0) 6 else 5
                var l = String()

                for (inc in 1..spaceCount)
                    l += " "

                l += ChatColor.GRAY.toString()
                l += line.translateColor()

                text.addExtra("\n")
                text.addExtra(l)
            }
        }

        return text
    }

    private fun getAllPermittedSubcommandsAsComponent() : Array<BaseComponent> {
        val comp = ComponentBuilder()
        val permittedSubcommands = subcommands
            .filter { sender.hasPermission(it.permission) }
            .toList()

        for ((i, sub) in permittedSubcommands.withIndex()) {
            comp.append(getSubcommandAsComponent(sub))

            if (i != (permittedSubcommands.size - 1))
                comp.append("\n")
        }

        return comp.create()
    }
}
