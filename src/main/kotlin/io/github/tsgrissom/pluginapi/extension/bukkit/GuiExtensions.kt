package io.github.tsgrissom.pluginapi.extension.bukkit

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
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

/**
 * Calculates the index of the previous page of the PaginatedPane. If the pagination is on the page with the first index
 * (0), the index returned is that of the last page (jumps to the end of the pages.)
 */
fun PaginatedPane.getPreviousIndex() =
    if (this.page > 0)
        this.page - 1
    else
        this.pages - 1

/**
 * Calculates the index of the next page of the PaginatedPane. If the pagination is on the last page of the pages, the
 * index returned is 0, or the index of the first page.
 */
fun PaginatedPane.getNextIndex() =
    if ((this.pages - 1) > this.page)
        this.page + 1
    else
        0

/**
 * Represents the position in the pages of the PaginatedPane as a String containing the ratio of the current index to
 * the maximum number of pages.
 */
fun PaginatedPane.getPageIndexString() =
    "${this.page + 1}/${this.pages}"