package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.command.help.CommandUsageBuilder
import io.github.tsgrissom.pluginapi.command.help.SubcParameterBuilder
import io.github.tsgrissom.pluginapi.extension.bukkit.lacksPermission
import io.github.tsgrissom.pluginapi.extension.bukkit.sendChatComponents
import io.github.tsgrissom.pluginapi.extension.kt.equalsIc
import io.github.tsgrissom.pluginapi.extension.kt.isPercentage
import io.github.tsgrissom.pluginapi.extension.kt.roundToDigits
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class DamageCommand : CommandBase() {

    private fun getPlugin() =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()

    companion object {
        const val PERM = "essentialskt.damage"
        const val PERM_PERCENT = "essentialskt.damage.percent"
    }

    private fun getUsage(context: CommandContext) : Array<BaseComponent> {
        val sender = context.sender
        val paramTarget = SubcParameterBuilder("Target", required=true)
        var paramAmountName = "Amount"
        if (sender.hasPermission(PERM_PERCENT)) {
            paramAmountName += " OR Percentage"
        }
        val paramAmount = SubcParameterBuilder(paramAmountName, required=true)

        return CommandUsageBuilder(context)
            .withParameters(
                paramTarget,
                paramAmount
            )
            .toComponents()
    }

    private fun sendUsage(context: CommandContext) =
        context.sender.sendChatComponents(getUsage(context))

    override fun execute(context: CommandContext) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)
        val ccPrimary = conf.getChatColor(ChatColorKey.Primary)
        val ccDetail = conf.getChatColor(ChatColorKey.Detail)

        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        if (args.isEmpty())
            return sendUsage(context)

        val sub = args[0]
        val target: Player = Bukkit.getPlayer(sub)
            ?: return sender.sendMessage("${ccErr}Could not find player ${ccErrDetl}\"$sub\"${ccErr}.")

        if (args.size == 1) {
            if (sub.equalsIc("help", "h", "?", "usage")) {
                return sendUsage(context)
            }

            return sender.sendMessage("${ccErr}You must specify an amount of damage.")
        }

        val arg1 = args[1]

        if (arg1.isPercentage()) {
            return handlePercentageInput(context, target, arg1)
        }

        if (arg1.toDoubleOrNull() == null) {
            return sender.sendMessage("${ccErr}Your damage amount must be a positive nonzero number.")
        }

        val amount = arg1.toDouble()

        if (amount <= 0) {
            return sender.sendMessage("${ccErr}Your damage amount must be a positive nonzero number.")
        }

        target.damage(amount)

        val text = "${ccPrimary}Damaged ${ccDetail}${target.name} ${ccPrimary}for ${ccDetail}${amount} hearts${ccPrimary}."
        sender.sendMessage(text)
    }

    private fun handlePercentageInput(context: CommandContext, target: Player, input: String) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccDetail = conf.getChatColor(ChatColorKey.Detail)
        val ccPrimary = conf.getChatColor(ChatColorKey.Primary)
        val ccTert = conf.getChatColor(ChatColorKey.Tertiary)

        val sender = context.sender

        if (sender.lacksPermission(PERM_PERCENT))
            return context.sendNoPermission(sender, PERM_PERCENT)

        val sansPercent = input.removeSuffix("%")

        if (sansPercent.toDoubleOrNull() == null) {
            val text = "${ccErr}Your damage percentage must be a decimal or integer value followed by a percent symbol."
            return sender.sendMessage(text)
        }

        val percent = sansPercent.toDouble()

        if (percent <= 0) {
            val text = "${ccErr}A percent of max health to damage must be a positive nonzero number."
            return sender.sendMessage(text)
        }

        val chunk = percent / 100
        val attr = target.getAttribute(Attribute.GENERIC_MAX_HEALTH)
        val maxHealth = attr?.value ?: 20.0
        val amount = chunk * maxHealth

        target.damage(amount)

        val text = "${ccPrimary}You damaged ${ccDetail}${target.name} ${ccPrimary}for ${ccDetail}${percent}% ${ccPrimary}of their max health ${ccTert}(${ccDetail}${amount.roundToDigits(2)}${ccTert})${ccPrimary}."
        sender.sendMessage(text)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> {
        val tab = mutableListOf<String>()
        val len = args.size

        if (len > 0) {
            val sub = args[0]

            if (len == 1) {
                StringUtil.copyPartialMatches(sub, getOnlinePlayerNamesToMutableList(), tab)
            }
        }

        return tab.sorted().toMutableList()
    }
}