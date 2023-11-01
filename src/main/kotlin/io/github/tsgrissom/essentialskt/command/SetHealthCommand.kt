package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.essentialskt.misc.EssPlayer
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.command.flag.CommandFlagParser
import io.github.tsgrissom.pluginapi.command.flag.ValidCommandFlag
import io.github.tsgrissom.pluginapi.extension.isPercentage
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class SetHealthCommand : CommandBase() {

    private fun getPlugin() =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()

    companion object {
        const val PERM         = "essentialskt.sethealth"
        const val PERM_MAX     = "essentialskt.sethealth.max"
        const val PERM_PERCENT = "essentialskt.sethealth.percent"
    }

    private val flagMax = ValidCommandFlag("max")

    override fun execute(context: CommandContext) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccDetl = conf.getChatColor(ChatColorKey.Detail)

        val args = context.args
        val len = args.size
        val sender = context.sender
        val flags = CommandFlagParser(args, flagMax)

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        if (len == 0)
            return sender.sendMessage("${ccErr}Usage: ${ccErrDetl}/sethealth <Target> <AmountAsDouble>")

        val sub = args[0]
        val t: Player = Bukkit.getPlayer(sub)
            ?: return sender.sendMessage("${ccErr}Could not find player ${ccErrDetl}\"$sub\"")

        if (len == 1)
            return sender.sendMessage("${ccErr}Please provide an amount of health to set to")

        val arg1 = args[1]

        if (arg1.isPercentage()) {
            return handlePercentageInput(context, t, arg1)
        }

        val arg1d = arg1.toDoubleOrNull()
            ?: return sender.sendMessage("${ccErr}Invalid decimal value for ${ccErr}\"$arg1\"")

        if (arg1d <= 0)
            return sender.sendMessage("${ccErr}New health value must be a nonzero positive number")

        if (flags.wasPassed(flagMax)) {
            return handleChangeMaxHealth(context, t, arg1d)
        }

        val max = EssPlayer(t).getMaxHealth()

        if (arg1d > max)
            return sender.sendMessage("${ccErrDetl}$arg1d ${ccErr}would exceed ${ccErrDetl}${t.name}'s ${ccErr}max health of ${ccErrDetl}$max")

        t.health = arg1d
        sender.sendMessage("${ccPrim}You set ${ccDetl}${t.name}'s ${ccPrim}health to ${ccDetl}$arg1")
    }

    private fun handleChangeMaxHealth(context: CommandContext, t: Player, maxHealth: Double) {
        val conf = getConfig()
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccDetl = conf.getChatColor(ChatColorKey.Detail)

        val sender = context.sender

        if (sender.lacksPermission(PERM_MAX))
            return context.sendNoPermission(sender, PERM_MAX)

        t.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = maxHealth
        t.health = maxHealth
        sender.sendMessage("${ccPrim}You set ${ccDetl}${t.name}'s ${ccPrim}maximum health to ${ccDetl}$maxHealth")
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
        val value = sansPercent.toDoubleOrNull()
            ?: return sender.sendMessage("${ccErrDetl}\"$input\" ${ccErr}is not a valid percentage value")

        if (value <= 0)
            return sender.sendMessage("${ccErr}A percentage of max health must be a positive nonzero number")

        val chunk = value / 100
        val attr = t.getAttribute(Attribute.GENERIC_MAX_HEALTH)
        val max = attr?.value ?: 20.0
        val amount = chunk * max

        t.health = amount
        sender.sendMessage("${ccPrim}You set ${ccDetl}${t.name}'s ${ccPrim}health to ${ccDetl}${input} ${ccPrim}of their max health")
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
            } else if (len > 2) {
                StringUtil.copyPartialMatches(args[2], listOf("--max", "-m"), tab)
            }
        }

        return tab.sorted().toMutableList()
    }
}