package io.github.tsgrissom.pluginapi.command

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

abstract class CommandBase : CommandExecutor, TabCompleter {

    companion object {
        fun createFlagPair(str: String) : Pair<String, String> {
            val sLow = str.lowercase()
            val sShort = sLow.substring(0, 1)
            return Pair(sLow, sShort)
        }

        val KEYS_SUBC_HELP = listOf("help", "h", "?", "--help", "-h")
        val KEYS_SUBC_HELP_OR_USAGE = listOf("help", "h", "?", "--help", "-h", "usage")
        val FLAG_GRAPHICAL = createFlagPair("gui")
    }

    fun getOnlinePlayerNamesToMutableList() : MutableList<String> =
        Bukkit.getOnlinePlayers()
            .map { it.name }
            .toMutableList()

    fun getWorldNamesToMutableList() : MutableList<String> =
        Bukkit.getWorlds()
            .map { it.name }
            .toMutableList()

    abstract fun execute(context: CommandContext)

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        execute(CommandContext(sender, command, label, args))
        return true
    }
}