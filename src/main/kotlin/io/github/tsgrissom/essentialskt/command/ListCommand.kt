package io.github.tsgrissom.essentialskt.command

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.util.StringUtil

class PlayerListGui : ChestGui(5, "Online Players") {

    init {
        val op = Bukkit.getOnlinePlayers()
        val pane = OutlinePane(0, 0, 9, 5)

        for (p in op) {
            val head = createPlayerHead(p)
            val btn = GuiItem(head) { e ->
                e.isCancelled = true
                e.whoClicked.closeInventory()
                Bukkit.dispatchCommand(e.whoClicked, "whois ${p.name}")
            }
            pane.addItem(btn)
        }

        this.addPane(pane)
    }

    private fun createPlayerHead(p: Player) : ItemStack {
        val x = p.location.x.roundToDigits(1)
        val y = p.location.y.roundToDigits(1)
        val z = p.location.z.roundToDigits(1)
        val item = ItemStack(Material.PLAYER_HEAD)
            .name("&6${p.name}")
            .lore(
                "&7Click to view their &e/whois &7profile",
                "&8> &7Nickname: &e${p.displayName}",
                "&8> &7UUID: &e${p.getUniqueString()}",
                "&8> &7World&8: &e${p.world.name}",
                "&8> &7Location &cX&aY&bZ&8: &c$x &a$y &b$z"
            )
            .flag(ItemFlag.HIDE_ATTRIBUTES)

        val sm = item.itemMeta as SkullMeta
        sm.ownerProfile = p.playerProfile
        item.itemMeta = sm
        return item
    }
}

class ListCommand : CommandBase() {

    private fun getAvailableLists() : Array<String> =
        arrayOf(
            "&6Available Lists",
            "&bOptional flag available to players",
            "&8&l> &emobs",
            "&8&l> &eplayers &b--gui"
        )

    private fun handleSubcPlayers(context: CommandContext) {
        val sender = context.sender
        val guiFlag = Pair("gui", "g")
        val hasGuiFlag = context.hasFlag(guiFlag)

        if (sender is ConsoleCommandSender) {
            handleSubcPlayersText(context)

            if (hasGuiFlag)
                sender.sendColored("&4Console cannot view GUIs")

            return
        }
        if (sender !is Player) {
            return
        }

        return if (hasGuiFlag)
            PlayerListGui().show(sender)
        else
            handleSubcPlayersText(context)
    }

    private fun handleSubcPlayersText(context: CommandContext) {
        context.sender.sendMessage("TODO Display players as text")
    }

    private fun handleSubcMobs(context: CommandContext) {
        context.sender.sendMessage("TODO Display mods as text")
    }

    override fun execute(context: CommandContext) {
        val args = context.args
        val sender = context.sender

        if (args.isEmpty())
            return getAvailableLists().forEach { sender.sendColored(it) }

        val sub = args[0]

        when (sub.lowercase()) {
            "players", "online" -> handleSubcPlayers(context)
            "mobs", "mob" -> handleSubcMobs(context)
            else -> sender.sendColored("&4Unknown list type &c\"$sub\"&4. Do &c/ls &4to view valid types.")
        }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val suggestSub = mutableListOf("players", "online", "mobs")
        val suggestPlayersArg1 = mutableListOf("--gui", "-g")
        val tab = mutableListOf<String>()

        val len = args.size

        if (len > 0) {
            val sub = args[0]

            if (len == 1) {
                if (!suggestSub.contains(sub)) {
                    StringUtil.copyPartialMatches(sub, suggestSub, tab)
                }
            } else if (len == 2) {
                if (sub.equalsIc("players", "online")) {
                    StringUtil.copyPartialMatches(args[1], suggestPlayersArg1, tab)
                }
            }
        }

        return tab.sorted().toMutableList()
    }
}