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

        if(gui == null || gui.isUpdating) return

        // Remove the player from the GUI first
        GUIStorage.removePlayer(player)

        if (gui.onClose != null)
            gui.onClose!!.invoke(event)

        if (GUIStorage.findPlayers(gui.id).isEmpty()) {
            plugin.logger.info("Removing GUI after close: ${gui.title}")
            if(gui.removalDelay > 0)
                GUIStorage.scheduleRemoval(gui, plugin)
            else
                GUIStorage.remove(gui)
        }
    }
}
