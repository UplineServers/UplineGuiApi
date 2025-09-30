package com.uplineservers.customGUI.listeners

import com.uplineservers.customGUI.models.GUI
import com.uplineservers.customGUI.models.SHIFT_PROTECTION
import com.uplineservers.customGUI.services.GUIStorage
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class InventoryClick(plugin: JavaPlugin) : Listener {
    val blockedKey = NamespacedKey.minecraft("gui_blocked")

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val gui = GUIStorage.getByPlayer(player) ?: return

        val clickedItem = event.currentItem
        val clickedInventory = event.clickedInventory
        val clickedSlot = event.rawSlot
        val hotbarSlot = event.hotbarButton

        val isClickedDataItem = gui.dataItem != null && clickedItem == gui.dataItem
        val isKeyboardDataItem = event.click.isKeyboardClick &&
                hotbarSlot in 0..8 && // <-- add this check
                gui.dataItem != null &&
                player.inventory.getItem(hotbarSlot) == gui.dataItem

        if (isClickedDataItem || isKeyboardDataItem) {
            event.isCancelled = true
            return
        }

        gui.onClick?.invoke(event)
        if (event.isCancelled) return

        // If updating, just block everything to avoid glitches
        if (gui.isUpdating) {
            event.isCancelled = true
            return
        }

        // Check if the clicked inventory is within the Inventory
        if (clickedInventory == null || clickedSlot < 0){
            event.isCancelled = true
            return
        }

        // Block blocked items
        if (isBlocked(clickedItem)) {
            event.isCancelled = true
            return
        }

        if(event.isShiftClick && gui.shiftProtection == SHIFT_PROTECTION.BLOCK){
            event.isCancelled = true
            return
        }

        // handle the clicked inventory within the Player inventory
        if (handlePlayerInventoryClick(event, gui)){
            return
        }

        // handle the clicked slot within the GUI inventory
        if(handleGuiSlotClick(event, gui)){
            return
        }
    }

    private fun isBlocked(item: ItemStack?): Boolean {
        if (item == null || item.type == Material.AIR) return false
        val itemMeta = item.itemMeta ?: return false
        return itemMeta.persistentDataContainer.get(blockedKey, PersistentDataType.BOOLEAN) == true
    }

    /**
     * Handles clicks on the Player inventory.
     * Returns true if the event was handled and should not be processed further.
     */
    private fun handlePlayerInventoryClick(event: InventoryClickEvent, gui: GUI): Boolean {
        if (event.clickedInventory!!.type != InventoryType.PLAYER) return false

        // Need to check for shift, because the slots might not be putable
        if (event.isShiftClick && !gui.isPutable) {
            event.isCancelled = true
            return true
        }

        return true
    }

    /**
     * Handles clicks on the GUI slots.
     * Returns true if the event was handled and should not be processed further.
     */
    private fun handleGuiSlotClick(event: InventoryClickEvent, gui: GUI) : Boolean {
        val slot = event.rawSlot
        if (slot < 0 || slot >= event.inventory.size) return false

        val guiItem = gui.items[slot]
        val isMovable = guiItem?.isMovable
        val isPutable = isMovable ?: gui.isPutable
        val isTakeable = isMovable ?: gui.isTakeable

        guiItem?.onClick?.invoke(event)
        if (event.isCancelled) return true

        if (event.isShiftClick && !gui.isTakeable) {
            event.isCancelled = true
            return true;
        }

        if(event.click.isKeyboardClick){
            if(!isTakeable && event.currentItem != null){
                event.isCancelled = true
                return true
            }

            if(!isPutable && event.currentItem == null){
                event.isCancelled = true
                return true
            }
        }

        // Prevent putting items (cursor -> GUI)
        if (!isPutable && event.cursor.type != Material.AIR) {
            event.isCancelled = true
            return true
        }

        // Prevent taking items (GUI -> cursor)
        if (!isTakeable && event.currentItem != null) {
            event.isCancelled = true
            return true
        }

        return false
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        val player = event.whoClicked as? Player ?: return
        val gui = GUIStorage.getByPlayer(player) ?: return

        for (slot in event.rawSlots) {
            if (slot < gui.size) {
                val guiItem = gui.items[slot]
                if (!(guiItem?.isMovable ?: gui.isPutable)) {
                    event.isCancelled = true
                    return
                }
            }
        }
    }
}