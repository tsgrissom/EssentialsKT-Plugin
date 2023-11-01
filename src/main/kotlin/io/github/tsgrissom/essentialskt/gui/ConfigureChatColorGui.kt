package io.github.tsgrissom.essentialskt.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.enum.ChatColorKey
import io.github.tsgrissom.pluginapi.extension.capitalizeEachWordAllCaps
import io.github.tsgrissom.pluginapi.extension.click
import io.github.tsgrissom.pluginapi.extension.lore
import io.github.tsgrissom.pluginapi.extension.name
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class ConfigureChatColorGui(
    private val key: ChatColorKey
) : ChestGui(2, "Select new color") {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()

    private fun alterKeyedChatColor(sender: HumanEntity, color: ChatColor) {
        sender.closeInventory()

        if (sender is Player) {
            sender.performCommand("esskt conf color set ${key.name} ${color.name}")
        } else {
            sender.sendMessage("Non-Players cannot alter configured chat color in-game")
        }
    }

    init {
        val conf = getConfig()
        val ccSec = conf.getChatColor(ChatColorKey.Secondary)
        val ccTer = conf.getChatColor(ChatColorKey.Tertiary)
        val ccSucc = conf.getChatColor(ChatColorKey.Success)
        val ccVal = conf.getChatColor(ChatColorKey.Value)
        val ccWhite = ChatColor.WHITE

        val currentColor = conf.getChatColor(key)

        val validColors = ChatColor.entries
            .filter { !it.isFormat && it != ChatColor.RESET }
            .toList()
        val pane = OutlinePane(0, 0, 9, 2)

        // TODO Mark the current chat color
        for (color in validColors) {
            val cc = color.toString()
            val name = color.name.capitalizeEachWordAllCaps()
            val material = if (currentColor == color) Material.MAP else Material.PAPER
            val text = if (currentColor == color) "${ccSucc}Currently set to this color" else "${ccVal}Click to set"

            val item = GuiItem(
                ItemStack(material)
                    .name("$ccWhite$name")
                    .lore(
                        "${ccSec}Example${ccTer}: $cc\"Some text\"",
                        text
                    )
            ) {
                val who = it.whoClicked
                alterKeyedChatColor(who, color)
            }
            pane.addItem(item)
        }

        onGlobalClick = Consumer { e ->
            e.isCancelled = true
        }

        onClose = Consumer { e ->
            click(e.player)
        }

        addPane(pane)
    }
}