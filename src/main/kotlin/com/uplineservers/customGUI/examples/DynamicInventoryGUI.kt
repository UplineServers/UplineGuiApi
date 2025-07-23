package com.uplineservers.customGUI.examples

import com.uplineservers.customGUI.CustomGUI
import com.uplineservers.customGUI.entities.CustomInventory
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * Example of a dynamic inventory that updates itself
 */
object DynamicInventoryGUI {
    
    /**
     * Creates a dynamic inventory that updates when interacted with
     */
    fun create(): CustomInventory {
        val gui = CustomInventory(
            title = "Dynamic Inventory",
            size = 18
        )
        
        var clickCount = 0
        
        // Initial items
        val counterItem = ItemStack(Material.REDSTONE).apply {
            val meta = itemMeta
            meta?.setDisplayName("§eClick Counter: $clickCount")
            meta?.lore = listOf("§7Click to increment!")
            itemMeta = meta
        }
        
        val resetItem = ItemStack(Material.BARRIER).apply {
            val meta = itemMeta
            meta?.setDisplayName("§cReset Counter")
            meta?.lore = listOf("§7Click to reset to 0")
            itemMeta = meta
        }
        
        val randomItem = ItemStack(Material.GOLDEN_APPLE).apply {
            val meta = itemMeta
            meta?.setDisplayName("§6Random Item")
            meta?.lore = listOf("§7Click for a surprise!")
            itemMeta = meta
        }
        
        gui.setItem(8, counterItem)
        gui.setItem(17, resetItem)
        gui.setItem(4, randomItem)
        
        gui.onClickListener = { player, inventory, slot, _ ->
            when (slot) {
                8 -> { // Counter item
                    clickCount++
                    val newItem = ItemStack(Material.REDSTONE).apply {
                        val meta = itemMeta
                        meta?.setDisplayName("§eClick Counter: $clickCount")
                        meta?.lore = listOf("§7Click to increment!")
                        itemMeta = meta
                    }
                    inventory.setItem(8, newItem)
                    
                    // Update the actual inventory display
                    CustomGUI.getInstance().guiManager.updateInventoryItem(inventory, 8)
                    
                    player.sendMessage("§aClicked $clickCount times!")
                }
                
                17 -> { // Reset button
                    clickCount = 0
                    val resetCounterItem = ItemStack(Material.REDSTONE).apply {
                        val meta = itemMeta
                        meta?.setDisplayName("§eClick Counter: $clickCount")
                        meta?.lore = listOf("§7Click to increment!")
                        itemMeta = meta
                    }
                    inventory.setItem(8, resetCounterItem)
                    
                    // Update the actual inventory display
                    CustomGUI.getInstance().guiManager.updateInventoryItem(inventory, 8)
                    
                    player.sendMessage("§cCounter reset!")
                }
                
                4 -> { // Random item
                    val randomMaterials = listOf(
                        Material.DIAMOND, Material.EMERALD, Material.GOLD_INGOT,
                        Material.IRON_INGOT, Material.COAL, Material.REDSTONE,
                        Material.LAPIS_LAZULI, Material.QUARTZ
                    )
                    val randomMaterial = randomMaterials.random()
                    
                    val newRandomItem = ItemStack(randomMaterial).apply {
                        val meta = itemMeta
                        meta?.setDisplayName("§6Random Item")
                        meta?.lore = listOf("§7Click for a surprise!", "§7Last roll: §f${randomMaterial.name}")
                        itemMeta = meta
                    }
                    inventory.setItem(4, newRandomItem)
                    
                    // Update the actual inventory display
                    CustomGUI.getInstance().guiManager.updateInventoryItem(inventory, 4)
                    
                    player.sendMessage("§6You got: §f${randomMaterial.name}!")
                }
            }
        }
        
        gui.onOpenListener = { player, _ ->
            player.sendMessage("§bWelcome to the dynamic inventory!")
            player.sendMessage("§7Try clicking the different items to see them change!")
        }
        
        return gui
    }
}
