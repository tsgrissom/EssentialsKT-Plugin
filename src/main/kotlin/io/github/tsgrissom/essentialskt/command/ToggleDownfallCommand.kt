package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.getCurrentWorldOrDefault
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import io.github.tsgrissom.pluginapi.extension.toggleRain
import org.bukkit.World

class ToggleDownfallCommand : CommandBase() {

    private val perm = "essentials.toggledownfall"

    override fun execute(context: CommandContext) {
        val sender = context.sender

        if (sender.lacksPermission(perm))
            return context.sendNoPermission(sender, perm)

        val w: World = sender.getCurrentWorldOrDefault()
        val wn = w.name
        val targetCondition = if (w.isClearWeather) "Rain" else "Clear"

        w.toggleRain()
        sender.sendColored("&6Weather of world &c${wn} &6set to &c${targetCondition}")
    }
}