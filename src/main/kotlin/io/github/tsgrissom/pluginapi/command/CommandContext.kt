package io.github.tsgrissom.pluginapi.command

import io.github.tsgrissom.essentialskt.misc.PluginLogger
import io.github.tsgrissom.pluginapi.data.QuotedStringSearchResult
import io.github.tsgrissom.pluginapi.extension.*
import net.md_5.bungee.api.ChatColor.RED
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission
import net.md_5.bungee.api.ChatColor.DARK_RED as D_RED

class CommandContext(
    val sender: CommandSender,
    val command: Command,
    val label: String,
    val args: Array<out String>
) {

    fun hasPermission(permission: String) =
        sender.hasPermission(permission)
    fun hasPermission(permission: Permission) =
        sender.hasPermission(permission)
    fun lacksPermission(permission: String) =
        !sender.hasPermission(permission)
    fun lacksPermission(permission: Permission) =
        !sender.hasPermission(permission)

    fun sendNoPermission(target: CommandSender, permission: String) {
        var resp = "${D_RED}You do not have access to that command."
        if (sender.hasPermission("essentialskt.disclosepermission"))
            resp += " ${D_RED}(${RED}$permission${D_RED})"
        target.sendMessage(resp)
    }

    fun sendNoPermission(target: CommandSender, permission: Permission) =
        sendNoPermission(target, permission.name)
    fun sendNoPermission(permission: String) =
        sendNoPermission(this.sender, permission)
    fun sendNoPermission(permission: Permission) =
        sendNoPermission(this.sender, permission)

    fun getExecutedString(withLabel: Boolean = true, startIndex: Int, endIndex: Int) : String {
        var builder =
            if (withLabel) this.label
            else ""

        if (args.isEmpty())
            return builder

        if (startIndex > args.size)
            error("startIndex is out of bounds of arguments")
        if (endIndex > args.size)
            error("endIndex is out of bounds of arguments")

        for (i in startIndex..endIndex)
            builder += " ${args[i]}"

        return builder.trim()
    }

    fun getExecutedString(withLabel: Boolean = true) : String {
        val len = args.size
        val endIndex = if (len > 0) args.size - 1 else 0
        return getExecutedString(withLabel=withLabel, 0, endIndex)
    }

    fun getQuotedStringFromArgsRange(startIndex: Int, endIndex: Int) : QuotedStringSearchResult? {
        val range = "Range[${startIndex}->${endIndex}]"
        fun warnWithRange(str: String) : QuotedStringSearchResult? {
            PluginLogger.warn("Cannot retrieve String from range of args of $range: $str")
            return null
        }

        if (args.isEmpty())
            return warnWithRange("Args are empty")
        if (endIndex < 0)
            return warnWithRange("endIndex is less than 0")
        if (startIndex < 0)
            return warnWithRange("startIndex is less than 0")
        if (endIndex < startIndex)
            return warnWithRange("endIndex is less than startIndex")

        val len = args.size

        if (startIndex > len)
            return warnWithRange("startIndex is out of bounds (> args size)")
        if (endIndex > len)
            return warnWithRange("endIndex is out of bounds (> args size)")
        if (!isRangedArgsToStringInQuotes(startIndex, endIndex))
            return warnWithRange("Not a quoted string! Check with CommandContext#isQuotedStringForRange or use CommandContext#getAnyQuotedString")

        val executed = getExecutedString(withLabel=false, startIndex, endIndex)

        PluginLogger.info(
            "Debug Print for getQuotedStringFromArgsRange(${startIndex}, ${endIndex})",
            "Executed String for $range=${executed}",
            "Is Quoted=${executed.isQuoted()}",
            "Is Single Quoted=${executed.isSingleQuoted()}",
            "Is Double Quoted=${executed.isDoubleQuoted()}"
        )

        return QuotedStringSearchResult(executed, 0, len)
    }

    fun isRangedArgsToStringInQuotes(startIndex: Int, endIndex: Int) =
        getExecutedString(withLabel=false, startIndex, endIndex).isQuoted()

    fun getAnyQuotedString() : QuotedStringSearchResult? {
        fun String.startsWithQuote() : String? {
            if (this.startsWith("'"))
                return "'"
            else if (this.startsWith("\""))
                return "\""
            return null
        }
        fun String.endsWithQuote() : String? {
            if (this.endsWith("'"))
                return "'"
            else if (this.endsWith("\""))
                return "\""
            return null
        }

        if (args.isEmpty())
            return null
        if (args.size == 1) {
            val arg = args[0]

            if (!arg.isQuoted())
                return null

            return QuotedStringSearchResult(arg, 0, 0)
        }

        // args.size > 1

        val fullString = getExecutedString(withLabel=false)
        var startIndex: Int? = null
        var endIndex: Int? = null
        var searchingFor: String? = null

        for (i in 1..args.size) {
            val n = i - 1 // The index of the current element
            val arg = args[n]

            if (searchingFor == null) { // We have not found a leading quote to match to a trailing one
                val leadingMark = arg.startsWithQuote()
                    ?: continue

                PluginLogger.info(
                    "Found leading quotation mark (${leadingMark}) in args[${n}](=${arg}) of potential quoted String(=${fullString})"
                )

                searchingFor = leadingMark
                startIndex = n
                val trailingMark = arg.endsWithQuote()
                    ?: continue // It is not a one arg quoted String if continue

                if (trailingMark == leadingMark)// A quoted String consisting of one argument
                    return QuotedStringSearchResult(arg, n, n)
            } else { // We have a leading character to match
                val trailingMark = arg.endsWithQuote()

                if (trailingMark == null) {
                    PluginLogger.info(
                        "Any trailing quote mark not found at args[$n](=$arg), continuing"
                    )
                    continue
                }

                if (trailingMark == searchingFor) {
                    endIndex = n
                }
            }
        }

        if (startIndex == null) {
            PluginLogger.warn("Search loop terminated without finding a startIndex")
            return null
        } else if (endIndex == null) {
            PluginLogger.warn("Search loop terminated without finding an endIndex")
            return null
        }

        val range = "Range[$startIndex, $endIndex]"

        PluginLogger.info(
            "getAnyQuotedString has found potential quoted String for $range. Passing to getQuotedStringForRange method."
        )
        return getQuotedStringFromArgsRange(startIndex, endIndex)
    }

    /**
     * Checks if there is any quoted String to be parsed from the full executed String. To be valid, there needs to be
     * a leading quotation mark, either an apostrophe or a double-quote which is the first character of an argument, and
     * a trailing quotation mark which is the ending character of an argument.
     *
     * Might not be performant enough unless you ONLY need to check for the presence of a quoted String because it
     * inverse null-checks result of <code>#getAnyQuotedString</code>. Use that method and manually check
     * <code>!= null</code> if you need to check AND use the result of the parsed quoted String.
     *
     * @return Whether the full executed String of the CommandContext contains any quoted Strings.
     */
    fun containsAnyQuotedString() : Boolean =
        getAnyQuotedString() != null

    fun performCommand(command: String) {
        when (sender) {
            is ConsoleCommandSender -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
            is Player -> sender.performCommand(command)
            else -> {
                sender.sendMessage("${RED}Cannot performCommand for a CommandSender who is neither Player nor Console")
            }
        }
    }
}