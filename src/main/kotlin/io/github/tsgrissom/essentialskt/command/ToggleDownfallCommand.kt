package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.bukkit.getCurrentWorldOrDefault
import io.github.tsgrissom.pluginapi.extension.bukkit.lacksPermission
import io.github.tsgrissom.pluginapi.extension.bukkit.toggleRain
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class ToggleDownfallCommand : CommandBase() {

    private fun getPlugin() =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()

    companion object {
        const val PERM = "essentialskt.toggledownfall"
    }

    override fun execute(context: CommandContext) {
        val conf = getConfig()
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccDetl = conf.getChatColor(ChatColorKey.Detail)

        val sender = context.sender

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        val w: World = sender.getCurrentWorldOrDefault()
        val wn = w.name
        val targetCondition = if (w.isClearWeather) "Rain" else "Clear"

        w.toggleRain()
        sender.sendMessage("${ccPrim}Weather of world ${ccDetl}${wn} ${ccPrim}set to ${ccDetl}${targetCondition}")
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> = mutableListOf()
}