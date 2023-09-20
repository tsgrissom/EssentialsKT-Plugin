package io.github.tsgrissom.pluginapi.command.help

import io.github.tsgrissom.pluginapi.extension.appendc
import net.md_5.bungee.api.ChatColor.GRAY
import net.md_5.bungee.api.ChatColor.YELLOW
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.permissions.Permission

class SubcommandHelp(val name: String) {

    companion object {
        fun compose(name: String) = SubcommandHelp(name)
    }

    val arguments: MutableList<SubcommandArgumentHelp> = mutableListOf()
    val description: MutableList<String> = mutableListOf()
    private var aliases: MutableList<String> = mutableListOf()
    var permission: Permission? = null

    fun withAliases(vararg s: String) : SubcommandHelp {
        aliases.addAll(s)
        return this
    }

    fun withArgument(arg: SubcommandArgumentHelp) : SubcommandHelp {
        arguments.add(arg)
        return this
    }

    fun withDescription(vararg s: String) : SubcommandHelp {
        description.addAll(s)
        return this
    }

    fun withPermission(s: String) : SubcommandHelp {
        permission = Permission(s)
        return this
    }

    fun withPermission(p: Permission) : SubcommandHelp {
        permission = p
        return this
    }

    fun getNameAsComponent() : Array<BaseComponent> {
        val comp = ComponentBuilder(name)

        if (aliases.isEmpty()) {
            return comp.create()
        }

        val hoverBuilder = ComponentBuilder()

        hoverBuilder.appendc("Aliases: ", GRAY)

        for ((i, alias) in aliases.withIndex()) {
            hoverBuilder.appendc(alias, YELLOW)
            if (i != (aliases.size - 1))
                hoverBuilder.appendc(",", GRAY)
        }

        val e = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(hoverBuilder.create()))
        comp.event(e)

        return comp.create()
    }
}