package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.isPercentage
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendChatComponents
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.ChatColor.DARK_GRAY as D_GRAY
import net.md_5.bungee.api.ChatColor.DARK_RED as D_RED
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import kotlin.math.roundToInt

class SetFoodLevelCommand : CommandBase() {

    companion object {
        const val PERM         = "essentialskt.setfoodlevel"
        const val PERM_PERCENT = "essentialskt.setfoodlevel.percent"
    }

    private fun generateUsageAsComponent(context: CommandContext) : Array<BaseComponent> {
        val comp = TextComponent("${D_RED}Usage: ")
        comp.color = RED

        val arg0Comp = TextComponent("<")
        val arg0Inner = TextComponent("Target")
        arg0Comp.color = RED
        arg0Inner.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("${GRAY}Required: ${GREEN}Yes\n"),
            Text("${GRAY}The player whose food level to set")
        )
        arg0Comp.addExtra(arg0Inner)
        arg0Comp.addExtra("> ")

        val arg1Comp = TextComponent("<")
        val arg1Inner = TextComponent(if (context.sender.hasPermission(PERM_PERCENT)) "AmountOrPercent" else "Amount")
        arg1Comp.color = RED
        arg1Inner.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("${GRAY}Required: ${GREEN}Yes\n"),
            Text("${D_GRAY}- ${GRAY}The amount to set the player's food level to\n"),
            Text("${D_GRAY}- ${GRAY}Must be an integer less than 20 or a percentage")
        )
        arg1Comp.addExtra(arg1Inner)
        arg1Comp.addExtra("> ")

        comp.addExtra("/${context.label} ")
        comp.addExtra(arg0Comp)
        comp.addExtra(arg1Comp)

        return ComponentBuilder(comp).create()
    }

    override fun execute(context: CommandContext) {
        val args = context.args
        val len = args.size
        val sender = context.sender

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        if (len == 0)
            return sender.sendChatComponents(generateUsageAsComponent(context))

        val sub = args[0]
        val t: Player = Bukkit.getPlayer(sub)
            ?: return sender.sendMessage("${D_RED}Could not find player ${RED}\"$sub\"")

        if (len == 1)
            return sender.sendMessage("${D_RED}Please provide an amount to set food level to")

        val arg1 = args[1]

        if (arg1.isPercentage()) {
            return handlePercentageInput(context, t, arg1)
        }

        var arg1d = arg1.toIntOrNull()
            ?: return sender.sendMessage("${D_RED}Invalid integer value for ${RED}\"$arg1\"")

        if (arg1d < 0)
            return sender.sendMessage("${D_RED}New food level must be a positive number less than 20")

        if (arg1d > 20)
            arg1d = 20

        t.foodLevel = arg1d
        sender.sendMessage("${GOLD}You set ${RED}${t.name}'s ${GOLD}food level to ${RED}$arg1")
    }

    private fun handlePercentageInput(context: CommandContext, t: Player, input: String) {
        val sender = context.sender

        if (sender.lacksPermission(PERM_PERCENT))
            return context.sendNoPermission(sender, PERM_PERCENT)

        val sansPercent = input.removeSuffix("%")
        val value = sansPercent.toIntOrNull()
            ?: return sender.sendMessage("${RED}\"$input\" ${D_RED}is not a valid percentage value")

        if (value <= 0)
            return sender.sendMessage("${D_RED}A percentage of max food level must be a positive nonzero integer")

        val chunk = value / 100.0
        val amount = chunk * 20

        t.foodLevel = amount.roundToInt()
        sender.sendMessage("${GOLD}You set ${RED}${t.name}'s ${GOLD}food level to ${RED}${input}")
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        val tab = mutableListOf<String>()

        if (sender.hasPermission(PERM)) {
            val len = args.size

            if (len == 1) {
                StringUtil.copyPartialMatches(args[0], getOnlinePlayerNamesToMutableList(), tab)
            }
        }

        return tab.sorted().toMutableList()
    }
}