package io.github.tsgrissom.essentialskt.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.pluginapi.extension.kt.capitalizeEachWordAllCaps
import io.github.tsgrissom.pluginapi.extension.bukkit.click
import io.github.tsgrissom.pluginapi.extension.bukkit.getRepresentativeMaterial
import io.github.tsgrissom.pluginapi.extension.bukkit.lore
import io.github.tsgrissom.pluginapi.extension.bukkit.name
import io.github.tsgrissom.pluginapi.func.NonFormattingChatColorPredicate
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ConfigureChatColorGui(
    private val key: ChatColorKey
) : ChestGui(2, "New color of ${key.name}") {

    private fun getPlugin() =
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
        val ccSucc = ChatColor.GREEN
        val ccVal = ChatColor.YELLOW
        val ccSec = ChatColor.GRAY
        val ccTer = ChatColor.DARK_GRAY

        val currentColor = conf.getChatColor(key)

        val validColors = ChatColor.entries
            .filter { NonFormattingChatColorPredicate().test(it) }
            .toList()
        val pane = OutlinePane(0, 0, 9, 2)

        for (color in validColors) {
            val cc = color.toString()
            val name = color.name.capitalizeEachWordAllCaps()
            var material = color.getRepresentativeMaterial()
            val text = if (currentColor == color) "${ccSucc}Currently set to this color" else "${ccVal}Click to set"
            val ccName = if (currentColor == color) ChatColor.GREEN else ChatColor.GRAY

            if (currentColor == color)
                material = Material.NETHER_STAR

            val item = GuiItem(
                ItemStack(material)
                    .name("$ccName$name")
                    .lore(
                        "${ccSec}Example${ccTer}: ${ccSec}\"${cc}Some text${ccSec}\"",
                        text
                    )
            ) {
                val who = it.whoClicked
                alterKeyedChatColor(who, color)
            }
            pane.addItem(item)
        }

        setOnGlobalClick {
            it.isCancelled = true
        }
        setOnClose {
            click(it.player)
        }

        addPane(pane)
    }
}