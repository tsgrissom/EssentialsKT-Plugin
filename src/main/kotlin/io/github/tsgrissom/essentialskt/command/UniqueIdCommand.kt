package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.*
import io.github.tsgrissom.pluginapi.chat.ClickableText
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class UniqueIdCommand : CommandBase() {

    private fun getPlugin() =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()

    companion object {
        const val PERM = "essentialskt.uniqueid"
    }

    override fun execute(context: CommandContext) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)

        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        val t: Player = if (args.size == 1) {
            val sub = args[0]
            Bukkit.getPlayer(sub)
                ?: return sender.sendMessage("${ccErr}Could not find player ${ccErrDetl}\"${sub}\"")
        } else {
            if (sender is ConsoleCommandSender)
                return sender.sendMessage("${ccErr}Console Usage: ${ccErrDetl}/uuid <Player>")
            if (sender !is Player)
                return

            sender
        }

        sender.sendChatComponents(generateDisplayUniqueIdAsTextComponent(t))
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

        val len = args.size

        if (len == 1) {
            StringUtil.copyPartialMatches(args[0], getOnlinePlayerNamesToMutableList(), tab)
        }

        return tab.sorted().toMutableList()
    }

    private fun generateDisplayUniqueIdAsTextComponent(t: Player) : Array<BaseComponent> {
        val conf = getConfig()
        val ccPrim = conf.getBungeeChatColor(ChatColorKey.Primary)
        val ccSec = conf.getBungeeChatColor(ChatColorKey.Secondary)
        val ccTert = conf.getBungeeChatColor(ChatColorKey.Tertiary)
        val ccUser = conf.getBungeeChatColor(ChatColorKey.Username)

        val uuid = t.getUniqueString()
        val data = ClickableText
            .compose(uuid)
            .color(ccUser)
            .action(ClickEvent.Action.COPY_TO_CLIPBOARD)
            .value(uuid)
            .toComponent()
        val builder = ComponentBuilder()
            .appendc(" ---------------------------------------\n", ccTert)
            .appendc(" | ", ccTert).appendc("UUID of ", ccSec).appendc(t.name, ccUser).append("\n")
            .appendc(" | ", ccTert).appendc("> ", ccPrim).bold(true).append(data).bold(false).append("\n").reset()
            .appendc(" ---------------------------------------", ccTert)

        return builder.create()
    }
}