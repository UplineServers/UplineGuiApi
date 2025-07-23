package com.uplineservers.customGUI.services

import com.uplineservers.customGUI.entities.GUIEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap

/**
 * Main GUI management service
 * Handles GUI lifecycle, player interactions, and inventory management
 */
class GUIManager(
    private val guiBuild: GUIBuild,
    private val guiGet: GUIGet,
    private val guiPlayer: GUIPlayer
) {
    
    // Store active GUIs
    private val activeGUIs = ConcurrentHashMap<String, GUIEntity>()
    private val inventoryToGUI = ConcurrentHashMap<Inventory, GUIEntity>()
    
    /**
     * Create and register a new GUI
     */
    fun createGUI(gui: GUIEntity): GUIEntity {
        guiBuild.build(gui)
        activeGUIs[gui.id] = gui
        gui.inventory?.let { inventoryToGUI[it] = gui }
        return gui
    }
    
    /**
     * Open GUI for a player
     */
    fun openGUI(player: Player, gui: GUIEntity) {
        if (gui.inventory == null) {
            guiBuild.build(gui)
            gui.inventory?.let { inventoryToGUI[it] = gui }
        }
        
        guiPlayer.addPlayer(gui, player)
        player.openInventory(gui.inventory!!)
        
        // Call onOpen callback
        gui.onOpen?.invoke(player, gui)
    }
    
    /**
     * Close GUI for a player
     */
    fun closeGUI(player: Player, gui: GUIEntity) {
        guiPlayer.removePlayer(gui, player)
        player.closeInventory()
        
        // Call onClose callback
        gui.onClose?.invoke(player, gui)
    }
    
    /**
     * Handle inventory click events
     */
    fun handleClick(player: Player, inventory: Inventory, slot: Int) {
        val gui = inventoryToGUI[inventory] ?: return
        val item = guiGet.getItem(gui, slot)
        
        // Call onClick callback
        gui.onClick?.invoke(player, gui, slot, item)
    }
    
    /**
     * Handle inventory close events
     */
    fun onClose(player: Player, inventory: Inventory) {
        val gui = inventoryToGUI[inventory] ?: return
        guiPlayer.removePlayer(gui, player)
        
        // Call onClose callback
        gui.onClose?.invoke(player, gui)
    }
    
    /**
     * Get GUI by inventory
     */
    fun getByInventory(inventory: Inventory): GUIEntity? {
        return inventoryToGUI[inventory]
    }
    
    /**
     * Get GUI by ID
     */
    fun getGUI(id: String): GUIEntity? {
        return activeGUIs[id]
    }
    
    /**
     * Remove and cleanup a GUI
     */
    fun removeGUI(id: String) {
        val gui = activeGUIs.remove(id)
        gui?.inventory?.let { inventoryToGUI.remove(it) }
        
        // Close inventory for all players using this GUI
        gui?.players?.forEach { player ->
            player.closeInventory()
        }
    }
    
    /**
     * Get all active GUIs
     */
    fun getAllGUIs(): Collection<GUIEntity> {
        return activeGUIs.values
    }
    
    /**
     * Clear all GUIs (used on plugin disable)
     */
    fun clearAll() {
        activeGUIs.values.forEach { gui ->
            gui.players.forEach { player ->
                player.closeInventory()
            }
        }
        activeGUIs.clear()
        inventoryToGUI.clear()
    }
}
