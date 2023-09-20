package io.github.tsgrissom.pluginapi.extension

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

fun OutlinePane.addItems(vararg item: GuiItem) {
    for (i in item) {
        this.addItem(i)
    }
}

fun Gui.click(who: HumanEntity) {
    if (who is Player) {
        who.playSound(who.location, Sound.UI_BUTTON_CLICK, 1F, 1F)
    }
}