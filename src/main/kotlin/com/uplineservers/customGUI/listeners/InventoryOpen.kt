package com.uplineservers.customGUI.listeners

import com.uplineservers.customGUI.storage.GUIStorage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.plugin.java.JavaPlugin

class InventoryOpen(private val plugin: JavaPlugin) : Listener {
    
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryOpen(event: InventoryOpenEvent) {
        val player = event.player as Player
        val gui = GUIStorage.getByInventory(event.inventory)
        if (gui == null) return
        if (gui.isUpdating) return

        plugin.logger.info("Player ${player.name} opened a GUI: ${gui?.title ?: "Unknown"}")
        if (gui.onOpen != null) {
            // Call the onOpen callback if it exists
            GUIStorage.addPlayer(gui, player);
            gui.onOpen!!.invoke(event)
        }
    }
}