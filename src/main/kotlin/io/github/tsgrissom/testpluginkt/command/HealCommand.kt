package io.github.tsgrissom.testpluginkt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext

class HealCommand : CommandBase() {

    override fun execute(context: CommandContext) {
        context.sender.sendMessage("In progress!")
    }
}