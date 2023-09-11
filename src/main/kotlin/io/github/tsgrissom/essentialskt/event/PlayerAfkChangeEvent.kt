package io.github.tsgrissom.essentialskt.event

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerAfkChangeEvent(
    private val player: Player,
    private val goneAfk: Boolean
) : Event(), Cancellable {

    private var isCancelled = false;

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }

    companion object {
        private val HANDLERS = HandlerList()

        //I just added this.
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }

    override fun isCancelled(): Boolean = this.isCancelled

    override fun setCancelled(cancel: Boolean) {
        this.isCancelled = cancel
    }

    fun getPlayer() = this.player
    fun isAway() = this.goneAfk
    fun hasReturned() = !isAway()
}