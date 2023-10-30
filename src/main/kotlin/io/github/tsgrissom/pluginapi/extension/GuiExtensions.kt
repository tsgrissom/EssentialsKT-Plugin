package io.github.tsgrissom.pluginapi.extension

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

/**
 * Add variable number of GuiItems to the current OutlinePane.
 * @param items A variable number of GuiItems to add to the OutlinePane.
 */
fun OutlinePane.addItems(vararg items: GuiItem) =
    items.forEach { this.addItem(it) }

/**
 * Plays a UI click sound for the requisite HumanEntity. Reduces mental overhead of usual playSound method.
 * @param who The HumanEntity (who will only hear the sound if they are a Player) to play the UI button click sound for.
 */
fun Gui.click(who: HumanEntity) {
    if (who is Player)
        who.playSound(Sound.UI_BUTTON_CLICK)
}