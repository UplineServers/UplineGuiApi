package com.uplineservers.customGUI.listeners

import com.uplineservers.customGUI.services.GUIManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

/**
 * Handles inventory events for custom GUIs
 */
class InventoryListener(private val guiManager: GUIManager) : Listener {
    
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked
        val inventory = event.inventory
        val slot = event.slot
        
        // Check if this is a custom GUI
        val customGUIInventory = guiManager.getByInventory(inventory)
        if (customGUIInventory != null && player is Player) {
            // Cancel the event by default to prevent item movement
            // You can modify this behavior in your onClick listener if needed
            event.isCancelled = true
            
            // Only handle clicks within the custom inventory bounds
            if (slot >= 0 && slot < inventory.size) {
                guiManager.handleClick(player, inventory, slot)
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player
        val inventory = event.inventory
        
        // Check if this is a custom GUI
        val customGUIInventory = guiManager.getByInventory(inventory)
        if (customGUIInventory != null && player is Player) {
            guiManager.onClose(player, inventory)
        }
    }
}
