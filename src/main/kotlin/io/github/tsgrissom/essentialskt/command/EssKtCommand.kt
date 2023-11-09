package io.github.tsgrissom.essentialskt.command

import BukkitChatColor
import BungeeChatColor
import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.essentialskt.gui.ConfigureChatColorGui
import io.github.tsgrissom.pluginapi.chat.ClickFormattedListBuilder
import io.github.tsgrissom.pluginapi.chat.FormattedListBuilder
import io.github.tsgrissom.pluginapi.chat.HoverFormattedListBuilder
import io.github.tsgrissom.pluginapi.chat.PlainFormattedListBuilder
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.command.flag.CommandFlagParser
import io.github.tsgrissom.pluginapi.command.flag.ValidCommandFlag
import io.github.tsgrissom.pluginapi.command.help.CommandHelpBuilder
import io.github.tsgrissom.pluginapi.command.help.SubcHelpBuilder
import io.github.tsgrissom.pluginapi.extension.kt.BooleanFormat
import io.github.tsgrissom.pluginapi.extension.bukkit.appendc
import io.github.tsgrissom.pluginapi.extension.bukkit.lacksPermission
import io.github.tsgrissom.pluginapi.extension.bukkit.sendChatComponents
import io.github.tsgrissom.pluginapi.extension.kt.capitalizeEachWordAllCaps
import io.github.tsgrissom.pluginapi.extension.kt.equalsIc
import io.github.tsgrissom.pluginapi.extension.kt.fmt
import io.github.tsgrissom.pluginapi.extension.kt.resolveChatColor
import io.github.tsgrissom.pluginapi.func.NonFormattingChatColorPredicate
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class EssKtCommand : CommandBase() {

    private fun getPlugin() =
        EssentialsKTPlugin.instance ?: error("Plugin not instantiated!")
    private fun getConfig() =
        getPlugin().getConfigManager()

    companion object {
        const val PERM = "essentialskt.essentialskt"
    }

    // MARK: Text Helper Functions
    private fun getHelpAsComponents(context: CommandContext) : Array<BaseComponent> {
        val help = CommandHelpBuilder(context)
            .withSubcommands(
                SubcHelpBuilder("version")
                    .withAliases("v")
                    .withDescription("View plugin version")
            )

        return help.toComponents()
    }

    // MARK: Handlers
    private fun handleSubcVersion(context: CommandContext) {
        val version = getPlugin().description.version
        context.sender.sendMessage("EssentialsKT version: $version")
    }

    private fun handleSubcReload(context: CommandContext) {
        val sender = context.sender
        val ccPrimary = getConfig().getChatColor(ChatColorKey.Primary)
        sender.sendMessage("${ccPrimary}In progress")
        // TODO Reload command
    }

    private fun handleSubcConfig(context: CommandContext) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccSec = conf.getChatColor(ChatColorKey.Secondary)
        val ccVal = conf.getChatColor(ChatColorKey.Value)
        val ccReset = BukkitChatColor.RESET

        val args = context.args
        val sender = context.sender
        val len = args.size

        fun listColorKeys() {
            val colors = ChatColorKey.entries
            var list = "Configurable Colors: "

            for ((i, color) in colors.withIndex()) {
                val cc = getConfig().getChatColor(color)
                val name = "$cc${color.name}${ccReset}"
                list += name
                if (i != (colors.size - 1)) {
                    list += ", "
                }
            }

            sender.sendMessage(list)
        }

        fun listConfiguredColors() {
            val colors = ChatColorKey.entries
            sender.sendMessage("Color Configuration:")

            for (color in colors) {
                val key = color.name
                val value = getConfig().getChatColor(color)
                sender.sendMessage(" - $value$key Color")
            }
        }

        fun listChatColors() {
            val validColors = BukkitChatColor.entries
                .filter { NonFormattingChatColorPredicate().test(it) }
            sender.sendMessage("Valid Chat Colors:")

            for (c in validColors) {
                val cc = c.toString()
                val name = c.name.capitalizeEachWordAllCaps()
                val inputName = name.replace(" ", "_")
                sender.sendMessage(" - $cc$name$ccReset = \"$inputName\"")
            }
        }

        if (len == 1)
            return sender.sendMessage("${ccSec}Options: ${ccVal}colors")

        val arg1 = args[1]

        fun handleConfigurationAreaColors() {
            if (len == 2)
                return listConfiguredColors()

            val arg2 = args[2]

            fun handleSet() {
                if (len == 3) {
                    sender.sendMessage("${ccErr}Usage: ${ccErrDetl}/esskt conf color set <Key>")
                    listColorKeys()
                    return
                }

                val arg3 = args[3]
                val textUnknown = "${ccErr}Unknown color key: ${ccErrDetl}\"$arg3\"${ccErr}. Do ${ccErrDetl}/esskt conf colors ${ccErr}to view valid keys."
                val configurableColor = conf.getKeyedChatColorByName(arg3)
                    ?: return sender.sendMessage(textUnknown) // Ensure color key is valid
                val key = configurableColor.name

                if (len == 4) { // Ex: "/esskt conf color set Detail"
                    // TODO Open gui

                    if (sender is Player) {
                        ConfigureChatColorGui(configurableColor).show(sender)
                    } else if (sender is ConsoleCommandSender) {
                        sender.sendMessage("${ccErr}Console cannot open GUIs. Use ${ccErrDetl}/esskt conf color set <Key> <Color> ${ccErr}to alter configurable chat colors from console.")
                    }

                    return
                }

                val arg4 = args[4]

                // TODO Permissions

                val new: BukkitChatColor = arg4.resolveChatColor()
                    ?: return sender.sendMessage("${ccErr}Unknown chat color ${ccErrDetl}\"$arg4\"")

                if (new.isFormat || new == BukkitChatColor.RESET)
                    return sender.sendMessage("${ccErr}You cannot use a formatting code for a configurable color.")

                val fc = getPlugin().config
                val path = "Colors.$key"

                fc.set(path, new.name)
                getPlugin().saveConfig()

                return sender.sendMessage("Color \"$key\" has been updated to: $new\"Example\"")
            }

            fun handleReset() {
                val fc = getPlugin().config
                fun resetKey(key: ChatColorKey) : Boolean {
                    val path = "Colors.${key.name}"
                    val def = key.defaultBukkit
                    val current = fc.getString(path)
                    fun setDefAndReturnTrue() : Boolean {
                        fc.set(path, def.name)
                        return true
                    }

                    if (current == null)
                        return setDefAndReturnTrue()

                    val resolved = current.resolveChatColor()
                        ?: return setDefAndReturnTrue()

                    if (resolved != def)
                        return setDefAndReturnTrue()

                    return false
                }

                val flagConfirm = ValidCommandFlag("Confirm")
                val flags = CommandFlagParser(args, flagConfirm)

                if (flags.wasPassed(flagConfirm)) {
                    var alteredCount = 0

                    for (key in ChatColorKey.entries) {
                        if (resetKey(key)) {
                            alteredCount++
                        }
                    }

                    if (alteredCount > 0) {
                        getPlugin().saveConfig()
                        sender.sendMessage("${ccPrim}All ${ccVal}$alteredCount ${ccPrim}customized chat colors were reset to their default values.")
                    } else {
                        sender.sendMessage("${ccErr}There was nothing to change. All colors match their default values.")
                    }

                    return
                }

                sender.sendMessage(
                    "${ccErr}Are you sure you want to reset the \"Colors\" section of the EssentialsKT configuration? This operation is irreversible.",
                    "${ccErr}Execute the command again with the ${ccErrDetl}--confirm ${ccErr}flag if you are sure."
                )
            }

            when (arg2.lowercase()) {
                "alter", "change", "configure", "set" -> handleSet()
                "list", "ls" -> listConfiguredColors()
                "listvalid", "listcolors", "listcolor" -> listChatColors()
                "reset", "resetall" -> handleReset()
                else -> {
                    sender.sendMessage("${ccErr}Unknown ${ccErrDetl}/esskt conf color ${ccErr}sub-command ${ccErrDetl}\"$arg2\"")
                }
            }
        }

        when (arg1.lowercase()) {
            "colors", "color" -> handleConfigurationAreaColors()
            else -> {
                sender.sendMessage("${ccErr}Unknown ${ccErrDetl}/esskt ${ccErr}sub-command ${ccErrDetl}\"$arg1\"")
            }
        }
    }

    private fun handleSubcDebug(context: CommandContext) {
        val conf = getConfig()
        val ccErr = conf.getBungeeChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getBungeeChatColor(ChatColorKey.ErrorDetail)
        val ccPrim = conf.getBungeeChatColor(ChatColorKey.Primary)
        val ccSec = conf.getBungeeChatColor(ChatColorKey.Secondary)
        val ccTert = conf.getBungeeChatColor(ChatColorKey.Tertiary)
        val ccVal = conf.getBungeeChatColor(ChatColorKey.Value)
        val ccReset = BungeeChatColor.RESET

        val args = context.args
        val len = args.size
        val sender = context.sender

        fun printDebugState() {
            val b = conf.isDebuggingActive().fmt(BooleanFormat.ENABLED_DISABLED, capitalize=true)
            sender.sendMessage("${ccSec}EssentialsKT Debugging${ccTert}: $ccReset$b")
        }

        fun listDebugSubcommands(negative: Boolean = false) {
            val subc = setOf("toggle", "true", "false")
            val prim = if (negative) ccErr else ccPrim
            val sec = if  (negative) ccErr else ccSec
            val punc = if (negative) ccErr else ccTert
            val valu = if (negative) ccErrDetl else ccVal
            val list = FormattedListBuilder("Debug Sub-Commands")
                .colors(prim, sec, punc, valu)
                .format(subc)
            sender.sendChatComponents(list)
        }

        fun handleToggle() {
            conf.toggleDebugging()
            printDebugState()
        }

        fun handleOn() {
            conf.setDebugging(true)
            printDebugState()
        }

        fun handleOff() {
            conf.setDebugging(false)
            printDebugState()
        }

        if (len == 1) {
            printDebugState()
            listDebugSubcommands()
            return
        }

        val arg1 = args[1]

        when (arg1.lowercase()) {
            "toggle", "t" -> handleToggle()
            "true", "on", "enable", "yes" -> handleOn()
            "false", "off", "disable", "no" -> handleOff()
            else -> {
                sender.sendMessage("${ccErr}Unknown debug sub-command ${ccErrDetl}\"$arg1\"")
                listDebugSubcommands(negative=true)
            }
        }
    }

    // MARK: Command Body
    override fun execute(context: CommandContext) {
        val sender = context.sender
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)

        if (context.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        val args = context.args
        val len = context.args.size

        if (len == 0 || (len == 1 && args[0].equalsIc(KEYS_SUBC_HELP)))
            return sender.sendChatComponents(getHelpAsComponents(context))

        val sub = args[0]

        when (sub.lowercase()) {
            "conf", "config", "configure" -> handleSubcConfig(context)
            "debug", "d" -> handleSubcDebug(context)
            "reload", "refresh" -> handleSubcReload(context)
            "version", "v" -> handleSubcVersion(context)
            else -> {
                sender.sendMessage("${ccErr}Unknown subcommand: ${ccErrDetl}\"$sub\" ${ccErr}Do ${ccErrDetl}/esskt ${ccErr}for help.")
            }
        }
    }

    // MARK: Tab Completion Handler
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> {
        val tab = mutableListOf<String>()
        fun tabSorted() =
            tab.sorted().toMutableList()

        val suggestArg0 = mutableListOf("conf", "debug", "reload", "version")
        val suggestConfigAreas = mutableListOf("color")
        val suggestConfigColorSubc = listOf("set", "list", "listcolors", "reset")
        val suggestDebugSubc = mutableListOf(" toggle")
        val validSubcDebugAliases = listOf("debug", "d")
        val validSubcConfigureAliases = listOf("conf", "config", "configure")
        val validConfigColorAliases = listOf("colors", "color")
        val validSetColorSubc = listOf("alter", "change", "set", "configure")

        if (sender.lacksPermission(PERM))
            return tab

        val len = args.size

        if (len == 1) {
            StringUtil.copyPartialMatches(args[0], suggestArg0, tab)
            return tabSorted()
        }

        val arg0 = args[0]

        if (len == 2) { // Example /esskt debug toggle
            if (arg0.equalsIc(validSubcConfigureAliases)) {
                StringUtil.copyPartialMatches(args[1], suggestConfigAreas, tab)
            } else if (arg0.equalsIc(validSubcDebugAliases)) {
                val isDebugging = getConfig().isDebuggingActive()
                suggestDebugSubc.add(if (isDebugging) "disable" else "enable")

                StringUtil.copyPartialMatches(args[1], suggestDebugSubc, tab)
            }
            return tabSorted()
        }

        val arg1 = args[1]

        if (len == 3) {
            // /esskt conf colors set

            if (arg0.equalsIc(validSubcConfigureAliases) && arg1.equalsIc(validConfigColorAliases)) {
                StringUtil.copyPartialMatches(args[2], suggestConfigColorSubc, tab)
            }
            return tabSorted()
        }

        val arg2 = args[2]
        val colorKeys = ChatColorKey.entries
            .map { it.name }
            .toList()

        if (len == 4) {
            if (arg0.equalsIc(validSubcConfigureAliases) &&
                arg1.equalsIc(validConfigColorAliases) &&
                arg2.equalsIc(validSetColorSubc)) { // esskt conf colors set <partial>
                StringUtil.copyPartialMatches(args[3], colorKeys, tab)
            }
            return tabSorted()
        }

        val arg3 = args[3]
        fun String.isValidColorKey() =
            getConfig().getKeyedChatColorByName(this) != null
        val allChatColors = BukkitChatColor.entries
            .filter { NonFormattingChatColorPredicate().test(it) }
            .map { it.name }
            .toList()

        if (len == 5) {
            if (arg0.equalsIc(validSubcConfigureAliases) &&
                arg1.equalsIc(validConfigColorAliases) &&
                arg2.equalsIc(validSetColorSubc) &&
                arg3.isValidColorKey()) {
                StringUtil.copyPartialMatches(args[4], allChatColors, tab)
            }
        }

        return tabSorted()
    }
}