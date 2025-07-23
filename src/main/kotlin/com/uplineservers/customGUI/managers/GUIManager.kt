package com.uplineservers.customGUI.managers

import com.uplineservers.customGUI.entities.CustomInventory

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.Plugin
import java.util.concurrent.ConcurrentHashMap

class GUIManager(private val plugin: Plugin) {
    
    // Maps to track open inventories and their associated CustomGUIInventory instances
    private val playerInventories: ConcurrentHashMap<Player, CustomInventory> = ConcurrentHashMap()
    private val inventoryMap: ConcurrentHashMap<Inventory, CustomInventory> = ConcurrentHashMap()

    /**
     * Creates a new CustomInventory
     */
    fun createCustomInventory(
        title: String,
        size: Int = 9,
        type: InventoryType
    ): CustomInventory {
        return CustomInventory(title = title, size = size, type = type)
    }

    /**
     * Opens a CustomGUIInventory for a player
     */
    fun openGUI(player: Player, CustomGUIInventory: CustomInventory) {
        val inventory = CustomGUIInventory.build()
        playerInventories[player] = CustomGUIInventory
        inventoryMap[inventory] = CustomGUIInventory
        
        // Open the inventory for the player
        player.openInventory(inventory);
        
        // Trigger the onOpen event
        CustomGUIInventory.onOpen(player)
    }
    
    /**
     * Closes the GUI for a player
     */
    fun closeGUI(player: Player) {
        val CustomGUIInventory = playerInventories[player]
        if (CustomGUIInventory != null) {
            player.closeInventory()
            // The actual cleanup will be handled by the inventory close event
        }
    }
    
    /**
     * Gets the CustomGUIInventory associated with a player
     */
    fun getPlayerGUI(player: Player): CustomInventory? {
        return playerInventories[player]
    }
    
    /**
     * Gets the CustomGUIInventory associated with a player
     */
    fun getCustomGUIInventory(player: Player): CustomInventory? {
        return playerInventories[player]
    }
    
    /**
     * Gets the CustomGUIInventory associated with a Bukkit inventory
     */
    fun getCustomGUIInventory(inventory: Inventory): CustomInventory? {
        return inventoryMap[inventory]
    }
    
    /**
     * Handles inventory close events
     */
    fun handleInventoryClose(player: Player, inventory: Inventory) {
        val CustomGUIInventory = inventoryMap[inventory]
        if (CustomGUIInventory != null) {
            // Trigger the onClose event
            CustomGUIInventory.onClose(player)
            
            // Clean up tracking maps
            playerInventories.remove(player)
            inventoryMap.remove(inventory)
        }
    }
    
    /**
     * Handles inventory click events
     * @return true if the click was handled by a GUI, false otherwise
     */
    fun handleInventoryClick(player: Player, inventory: Inventory, slot: Int): Boolean {
        val CustomGUIInventory = inventoryMap[inventory]
        return if (CustomGUIInventory != null) {
            // Trigger the onClick event
            CustomGUIInventory.onClick(player, slot)
            true
        } else {
            false
        }
    }
    
    /**
     * Checks if a player has a GUI open
     */
    fun hasGUIOpen(player: Player): Boolean {
        return playerInventories.containsKey(player)
    }
    
    /**
     * Updates an item in the open inventory
     */
    fun updateInventoryItem(CustomGUIInventory: CustomInventory, slot: Int) {
        // Find the Bukkit inventory associated with this CustomGUIInventory
        val bukkitInventory = inventoryMap.entries.find { it.value == CustomGUIInventory }?.key
        if (bukkitInventory != null) {
            val item = CustomGUIInventory.getItem(slot)
            bukkitInventory.setItem(slot, item)
        }
    }
}
