package com.uplineservers.customGUI.listeners

import com.uplineservers.customGUI.storage.GUIStorage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.plugin.java.JavaPlugin

class InventoryClose(private val plugin: JavaPlugin) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player
        val gui = GUIStorage.getByPlayer(player as Player)

        if(gui == null) return
        if(gui.isUpdating) return

        // Remove the player from the GUI first
        GUIStorage.removePlayer(player)

        if (gui.onClose != null) {
            gui.onClose!!.invoke(event)
        }

        // If no players are left, schedule delayed removal instead of immediate removal
        if (gui.players.isEmpty()) {
            plugin.logger.info("All players have closed the GUI: ${gui.title}. Scheduling removal in ${gui.removalDelay} seconds.")
            if(gui.removalDelay > 0)
                GUIStorage.scheduleRemoval(gui, plugin)
            else
                GUIStorage.remove(gui)
        } else {
            plugin.logger.info("Player ${player.name} closed the GUI: ${gui.title}. Remaining players: ${gui.players.size}")
        }
    }
}
