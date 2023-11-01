package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.* 
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

    private fun sendUsage(context: CommandContext) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetail = conf.getChatColor(ChatColorKey.ErrorDetail)

        val label = context.label
        val sender = context.sender
        var usage = "${ccErr}Usage: ${ccErrDetail}/$label <Target> <AmountAsDecimal"

        usage += if (sender.hasPermission(PERM_PERCENT)) {
            "OrPercentage>"
        } else {
            ">"
        }

        sender.sendMessage(usage)
    }

    override fun execute(context: CommandContext) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetail = conf.getChatColor(ChatColorKey.ErrorDetail)
        val ccPrimary = conf.getChatColor(ChatColorKey.Primary)
        val ccDetail = conf.getChatColor(ChatColorKey.Detail)

        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        if (args.isEmpty())
            return sendUsage(context)

        val sub = args[0]
        val t: Player = Bukkit.getPlayer(sub)
            ?: return sender.sendMessage("${ccErr}Could not find player ${ccErrDetail}\"$sub\"")

        if (args.size == 1) {
            if (sub.equalsIc("help", "h", "?", "usage"))
                return sendUsage(context)

            return sender.sendMessage("${ccErr}You must specify an amount of damage")
        }

        val arg1 = args[1]

        if (arg1.isPercentage()) {
            return handlePercentageInput(context, t, arg1)
        }

        val amount: Double

        try {
            amount = arg1.toDouble()

            if (amount <= 0)
                return sender.sendMessage("${ccErr}Your damage amount must be a positive nonzero number")
        } catch (ignored: NumberFormatException) {
            return sender.sendMessage("${ccErr}Your damage amount must be a decimal or integer value")
        }

        t.damage(amount)
        sender.sendMessage("${ccPrimary}Damaged ${ccDetail}${t.name} ${ccPrimary}for ${ccDetail}${amount} hearts")
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
        val percent: Double

        try {
            percent = sansPercent.toDouble()

            if (percent <= 0)
                return sender.sendMessage("${ccErr}A percent of max health to damage must be a positive nonzero number")
        } catch (ignored: NumberFormatException) {
            val text = "${ccErr}Your damage percentage must be a decimal or integer value followed by a percent symbol"
            return sender.sendMessage(text)
        }

        val chunk = percent / 100
        val attr = target.getAttribute(Attribute.GENERIC_MAX_HEALTH)
        val maxHealth = attr?.value ?: 20.0
        val amount = chunk * maxHealth

        target.damage(amount)
        sender.sendMessage("${ccPrimary}You damaged ${ccDetail}${target.name} ${ccPrimary}for ${ccDetail}${percent}% ${ccPrimary}of their max health ${ccTert}(${ccDetail}${amount.roundToDigits(2)}${ccTert})")
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> {

        val tab =  mutableListOf<String>()
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