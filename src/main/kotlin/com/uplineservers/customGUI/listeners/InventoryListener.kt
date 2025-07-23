package com.uplineservers.customGUI.listeners

import com.uplineservers.customGUI.storage.GUIStorage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

/**
 * Handles inventory events for custom GUIs
 */
class InventoryListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryOpen(event: org.bukkit.event.inventory.InventoryOpenEvent) {
        val player = event.player as Player

        // Check if this is a custom GUI using the global registry
        val gui = GUIStorage.getByPlayer(player)
        if (gui != null && gui.onOpen != null) {
            gui.onOpen!!.invoke(player)
            gui.players += player
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked
        val slot = event.slot
        
        // Check if this is a custom GUI using the global registry
        val gui = GUIStorage.getByPlayer(player as Player)
        if (gui != null && gui.onClick != null) {
            gui.onClick!!.invoke(player, slot)
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player

        val gui = GUIStorage.getByPlayer(player as Player)
        if (gui != null && gui.onClose != null) {
            gui.onClose!!.invoke(player)
            gui.players -= player
        }
    }
}
