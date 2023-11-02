package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.kt.isPercentage
import io.github.tsgrissom.pluginapi.extension.bukkit.lacksPermission
import io.github.tsgrissom.pluginapi.extension.bukkit.sendChatComponents
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

    private fun getPlugin() =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()

    companion object {
        const val PERM         = "essentialskt.setfoodlevel"
        const val PERM_PERCENT = "essentialskt.setfoodlevel.percent"
    }

    private fun generateUsageAsComponent(context: CommandContext) : Array<BaseComponent> {
        val conf = getConfig()
        val ccErr = conf.getBungeeChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getBungeeChatColor(ChatColorKey.ErrorDetail)
        val ccSec = conf.getBungeeChatColor(ChatColorKey.Secondary)
        val ccSucc = conf.getBungeeChatColor(ChatColorKey.Success)
        val ccTert = conf.getBungeeChatColor(ChatColorKey.Tertiary)

        val comp = TextComponent("${ccErr}Usage: ")
        comp.color = ccErrDetl

        val arg0Comp = TextComponent("<")
        val arg0Inner = TextComponent("Target")
        arg0Comp.color = ccErrDetl
        arg0Inner.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("${ccSec}Required: ${ccSucc}Yes\n"),
            Text("${ccSec}The player whose food level to set")
        )
        arg0Comp.addExtra(arg0Inner)
        arg0Comp.addExtra("> ")

        val arg1Comp = TextComponent("<")
        val arg1Inner = TextComponent(if (context.sender.hasPermission(PERM_PERCENT)) "AmountOrPercent" else "Amount")
        arg1Comp.color = ccErrDetl
        arg1Inner.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("${ccSec}Required: ${ccSucc}Yes\n"),
            Text("${ccTert}- ${ccSec}The amount to set the player's food level to\n"),
            Text("${ccTert}- ${ccSec}Must be an integer less than 20 or a percentage")
        )
        arg1Comp.addExtra(arg1Inner)
        arg1Comp.addExtra("> ")

        comp.addExtra("/${context.label} ")
        comp.addExtra(arg0Comp)
        comp.addExtra(arg1Comp)

        return ComponentBuilder(comp).create()
    }

    override fun execute(context: CommandContext) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccDetl = conf.getChatColor(ChatColorKey.Detail)

        val args = context.args
        val len = args.size
        val sender = context.sender

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        if (len == 0)
            return sender.sendChatComponents(generateUsageAsComponent(context))

        val sub = args[0]
        val t: Player = Bukkit.getPlayer(sub)
            ?: return sender.sendMessage("${ccErr}Could not find player ${ccErrDetl}\"$sub\"")

        if (len == 1)
            return sender.sendMessage("${ccErr}Please provide an amount to set food level to")

        val arg1 = args[1]

        if (arg1.isPercentage()) {
            return handlePercentageInput(context, t, arg1)
        }

        var arg1d = arg1.toIntOrNull()
            ?: return sender.sendMessage("${ccErr}Invalid integer value for ${ccErrDetl}\"$arg1\"")

        if (arg1d < 0)
            return sender.sendMessage("${ccErr}New food level must be a positive number less than 20")

        if (arg1d > 20)
            arg1d = 20

        t.foodLevel = arg1d
        sender.sendMessage("${ccPrim}You set ${ccDetl}${t.name}'s ${ccPrim}food level to ${ccDetl}$arg1")
    }

    private fun handlePercentageInput(context: CommandContext, t: Player, input: String) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccDetl = conf.getChatColor(ChatColorKey.Detail)

        val sender = context.sender

        if (sender.lacksPermission(PERM_PERCENT))
            return context.sendNoPermission(sender, PERM_PERCENT)

        val sansPercent = input.removeSuffix("%")
        val value = sansPercent.toIntOrNull()
            ?: return sender.sendMessage("${ccErrDetl}\"$input\" ${ccErr}is not a valid percentage value")

        if (value <= 0)
            return sender.sendMessage("${ccErr}A percentage of max food level must be a positive nonzero integer")

        val chunk = value / 100.0
        val amount = chunk * 20

        t.foodLevel = amount.roundToInt()
        sender.sendMessage("${ccPrim}You set ${ccDetl}${t.name}'s ${ccPrim}food level to ${ccDetl}${input}")
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