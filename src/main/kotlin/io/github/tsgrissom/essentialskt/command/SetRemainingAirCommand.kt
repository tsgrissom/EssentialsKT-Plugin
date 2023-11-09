package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.bukkit.lacksPermission
import io.github.tsgrissom.pluginapi.extension.bukkit.sendChatComponents
import io.github.tsgrissom.pluginapi.extension.kt.isPercentage
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

class SetRemainingAirCommand : CommandBase(){

    private fun getPlugin() =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()

    private val suggestedLevels = mutableListOf("25%", "75%")

    init {
        for (i in 10..100 step 10)
            suggestedLevels.add("$i%")
    }

    companion object {
        const val PERM         = "essentialskt.setremainingair"
        const val PERM_OTHERS  = "essentialskt.setremainingair.others"
        const val PERM_PERCENT = "essentialskt.setremainingair.percent"
    }

    private fun generateUsageAsComponent(context: CommandContext) : Array<BaseComponent> {
        val conf = getConfig()
        val ccErr = conf.getBungeeChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getBungeeChatColor(ChatColorKey.ErrorDetail)
        val ccVal = conf.getBungeeChatColor(ChatColorKey.Value)
        val ccSec = conf.getBungeeChatColor(ChatColorKey.Secondary)
        val ccSucc = conf.getBungeeChatColor(ChatColorKey.Success)
        val ccTert = conf.getBungeeChatColor(ChatColorKey.Tertiary)
        val sender = context.sender

        val root = TextComponent("${ccErr}Usage: ")
        root.color = ccErrDetl

        val arg0 = TextComponent("<")
        val arg0Inner = TextComponent("Target")
        arg0.color = ccErrDetl
        arg0Inner.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("${ccSec}Required: ${ccSucc}Yes\n"),
            Text("${ccSec}The player whose oxygen level to set")
        )
        arg0.addExtra(arg0Inner)
        arg0.addExtra("> ")

        val permittedToPercent = sender.hasPermission(PERM_PERCENT)
        val l3Append = if (permittedToPercent)
            " ${ccSec}or a percentage"
        else
            ""

        val arg1 = TextComponent("<")
        val arg1Inner = TextComponent(if (permittedToPercent) "AmountOrPercent" else "Amount")
        arg1.color = ccErrDetl
        arg1Inner.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("${ccSec}Required: ${ccSucc}Yes\n"),
            Text("${ccTert}- ${ccSec}The amount to set the player's oxygen level to\n"),
            Text("${ccTert}- ${ccSec}Must be a positive integer less than or equal to ${ccVal}300$l3Append")
        )
        arg1.addExtra(arg1Inner)
        arg1.addExtra("> ")

        root.addExtra("/${context.label} ")
        root.addExtra(arg0)
        root.addExtra(arg1)

        return ComponentBuilder(root).create()
    }

    override fun execute(context: CommandContext) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccTer = conf.getChatColor(ChatColorKey.Tertiary)
        val ccDetl = conf.getChatColor(ChatColorKey.Detail)

        val args = context.args
        val len = args.size
        val sender = context.sender

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        if (len == 0)
            return sender.sendChatComponents(generateUsageAsComponent(context))

        val sub = args[0]
        val target: Player = Bukkit.getPlayer(sub)
            ?: return sender.sendMessage("${ccErr}Could not find player ${ccErrDetl}\"$sub\"${ccErr}.")

        if (len == 1) {
            val verbiage = if (sender.hasPermission(PERM_PERCENT))
                "amount or percentage"
            else
                "amount"
            sender.sendChatComponents(generateUsageAsComponent(context))
            sender.sendMessage("${ccErr}Please provide an $verbiage to set oxygen level to.")
            return
        }

        val arg1 = args[1]

        if (arg1.isPercentage())
            return handlePercentageInput(context, target, arg1)

        val verbiage = if (sender.hasPermission(PERM_PERCENT))
            "integer or percent value"
        else
            "integer value"
        val arg1d = arg1.toIntOrNull()
            ?: return sender.sendMessage("${ccErr}Invalid $verbiage for ${ccErrDetl}\"$arg1\"${ccErr}.")

        if (arg1d < 0)
            return sender.sendMessage("${ccErr}New oxygen level should be a positive $verbiage.")
        if (arg1d > 300)
            return sender.sendMessage("${ccErr}The maximum oxygen level is ${ccErrDetl}300${ccErr}.")

        if (target == sender && sender.lacksPermission(PERM))
            return context.sendNoPermission(PERM)
        if (target != sender && sender.lacksPermission(PERM_OTHERS))
            return context.sendNoPermission(PERM_OTHERS)

        val targetName = if (target == sender)
            "your"
        else
            "${ccDetl}${target.name}'s"

        target.remainingAir = arg1d
        sender.sendMessage("${ccPrim}You set $targetName ${ccPrim}oxygen level to ${ccDetl}$arg1${ccTer}/${ccDetl}300${ccPrim}.")
    }

    private fun handlePercentageInput(context: CommandContext, target: Player, input: String) {
        val sender = context.sender

        if (sender.lacksPermission(PERM_PERCENT))
            return context.sendNoPermission(PERM_PERCENT)

        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccTer = conf.getChatColor(ChatColorKey.Tertiary)
        val ccDetl = conf.getChatColor(ChatColorKey.Detail)

        val sansPercent = input.removeSuffix("%")
        val value = sansPercent.toIntOrNull()
            ?: return sender.sendMessage("${ccErrDetl}\"$input\" ${ccErr}is not a valid percentage value. Must be a positive integer.")

        if (value < 0)
            return sender.sendMessage("${ccErr}A percentage of max oxygen level must be a positive integer.")

        val chunk = value / 100.0
        val amount = chunk * 300

        val targetName = if (target == sender)
            "your"
        else
            "${ccDetl}${target.name}'s"
        val amountRounded = amount.roundToInt()
        val asPoints = "${ccTer}(${ccDetl}${amountRounded}${ccTer}/${ccDetl}300${ccTer})"

        target.remainingAir = amountRounded
        sender.sendMessage("${ccPrim}You set $targetName ${ccPrim}oxygen level to ${ccDetl}${input} $asPoints${ccPrim}.")
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> {
        val tab = mutableListOf<String>()
        fun finish() = tab.sorted().toMutableList()

        val len = args.size

        if (len == 1) {
            StringUtil.copyPartialMatches(args[0], getOnlinePlayerNamesToMutableList(), tab)
        } else if (len == 2) {
            StringUtil.copyPartialMatches(args[1], suggestedLevels, tab)
        }

        return finish()
    }
}