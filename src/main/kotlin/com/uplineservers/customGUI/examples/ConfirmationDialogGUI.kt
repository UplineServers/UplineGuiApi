package com.uplineservers.customGUI.examples

import com.uplineservers.customGUI.CustomGUI
import com.uplineservers.customGUI.entities.CustomInventory
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

/**
 * Example confirmation dialog with Yes/No buttons
 */
object ConfirmationDialogGUI {
    
    /**
     * Creates a simple confirmation dialog with Yes/No buttons
     */
    fun create(
        title: String = "Confirm Action",
        onConfirm: (Player) -> Unit,
        onCancel: (Player) -> Unit
    ): CustomInventory {
        val gui = CustomInventory(
            title = title,
            type = InventoryType.HOPPER,
            size = 5
        )
        
        // Set up the items
        val yesItem = ItemStack(Material.GREEN_WOOL).apply {
            val meta = itemMeta
            meta?.setDisplayName("§aYes")
            itemMeta = meta
        }
        
        val noItem = ItemStack(Material.RED_WOOL).apply {
            val meta = itemMeta
            meta?.setDisplayName("§cNo")
            itemMeta = meta
        }
        
        gui.setItem(1, yesItem)
        gui.setItem(3, noItem)
        
        // Set up the click listener
        gui.onClickListener = { player: Player, _: CustomInventory, slot: Int, _: ItemStack? ->
            when (slot) {
                1 -> { // Yes button
                    onConfirm(player)
                    CustomGUI.getInstance().guiManager.closeGUI(player)
                }
                3 -> { // No button
                    onCancel(player)
                    CustomGUI.getInstance().guiManager.closeGUI(player)
                }
            }
        }

        return gui
    }
}
