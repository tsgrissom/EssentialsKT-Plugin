package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.pluginapi.chat.ClickTextBuilder
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.chat.TextBoxBuilder
import io.github.tsgrissom.pluginapi.extension.bukkit.getIPString
import io.github.tsgrissom.pluginapi.extension.bukkit.ipString
import io.github.tsgrissom.pluginapi.extension.bukkit.lacksPermission
import io.github.tsgrissom.pluginapi.extension.bukkit.sendChatComponents
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class IPAddressCommand : CommandBase() {

    private fun getPlugin() =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()

    companion object {
        const val PERM = "essentialskt.ipaddress"
    }

    override fun execute(context: CommandContext) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)

        val args = context.args
        val label = context.label
        val sender = context.sender

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        if (args.isEmpty())
            return sender.sendMessage("${ccErr}Usage: ${ccErrDetl}/$label <Target>")

        val sub = args[0]
        val t: Player = Bukkit.getPlayer(sub)
            ?: return sender.sendMessage("${ccErr}Could not find player ${ccErrDetl}\"$sub\"")

        sender.sendChatComponents(generateTextBox(t))
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        val tab = mutableListOf<String>()
        val len = args.size

        if (len == 1) {
            StringUtil.copyPartialMatches(args[0], getOnlinePlayerNamesToMutableList(), tab)
        }

        return tab.sorted().toMutableList()
    }

    private fun generateTextBox(t: Player) : Array<BaseComponent> {
        val conf = getConfig()
        val ccPrim = conf.getBungeeChatColor(ChatColorKey.Primary)
        val ccSec = conf.getBungeeChatColor(ChatColorKey.Secondary)
        val ccTert = conf.getBungeeChatColor(ChatColorKey.Tertiary)
        val ccVal = conf.getBungeeChatColor(ChatColorKey.Value)

        val data = ClickTextBuilder(t.ipString)
            .action(ClickEvent.Action.COPY_TO_CLIPBOARD)
            .color(YELLOW)
            .underline(true)
            .value(t.getIPString())
            .toComponent()
        val l1 = TextComponent("IP Address of ")
        val l1Name = TextComponent(t.name)
        val l2 = TextComponent("> ")

        l1.color = ccSec
        l1Name.color = ccVal
        l1.addExtra(l1Name)
        l2.color = ccPrim
        l2.addExtra(data)

        return TextBoxBuilder(decorationColor=ccTert)
            .withLine(l1, l2)
            .toComponents()
    }
}