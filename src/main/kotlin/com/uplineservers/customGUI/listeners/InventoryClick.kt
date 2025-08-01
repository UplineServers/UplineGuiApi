package com.uplineservers.customGUI.listeners

import com.uplineservers.customGUI.CustomGUI
import com.uplineservers.customGUI.CustomGUI.Companion.instance
import com.uplineservers.customGUI.models.GUI
import com.uplineservers.customGUI.models.SHIFT_PROTECTION
import com.uplineservers.customGUI.services.GUIBuild
import com.uplineservers.customGUI.services.GUISync
import com.uplineservers.customGUI.storage.GUIStorage
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

        if (gui.isUpdating) {
            event.isCancelled = true
            return
        }

        val clickedSlot = event.rawSlot
        val clickedItem = event.currentItem
        val clickedInventory = event.clickedInventory

        if (clickedInventory == null || clickedSlot < 0){
            event.isCancelled = true
            return
        }

        if (event.click == ClickType.NUMBER_KEY) {
            val hotbarSlot = event.hotbarButton
            val hotbarItem = player.inventory.getItem(hotbarSlot)
            val meta = hotbarItem?.itemMeta
            if (meta?.persistentDataContainer?.get(blockedKey, PersistentDataType.BOOLEAN) == true) {
                event.isCancelled = true
                return
            }
        } else {
            val meta = clickedItem?.itemMeta
            if (meta?.persistentDataContainer?.get(blockedKey, PersistentDataType.BOOLEAN) == true) {
                event.isCancelled = true
                return
            }
        }

        if (clickedInventory.type == InventoryType.PLAYER)
            return handlePlayerInventoryClick(event, gui, clickedItem)

        handleGuiSlotClick(event, gui, clickedSlot, clickedItem)
        if (event.isCancelled) return

        GUISync.slotSync(gui, clickedSlot)
    }

    private fun handlePlayerInventoryClick(event: InventoryClickEvent, gui: GUI, clickedItem: ItemStack?) {
        val player = event.whoClicked as? Player ?: return

        gui.onPlayerInventoryClick?.invoke(event)

        if (gui.shiftProtection == SHIFT_PROTECTION.BLOCK) event.isCancelled = true
        if (event.isCancelled || gui.shiftProtection == SHIFT_PROTECTION.NONE) return
        if (event.isShiftClick && gui.shiftProtection == SHIFT_PROTECTION.SMART) {
            event.isCancelled = true
            if (clickedItem != null && clickedItem.type != Material.AIR)
                handleShiftClick(player, gui, event.slot, clickedItem)
        }
    }

    private fun handleGuiSlotClick(event: InventoryClickEvent, gui: GUI, slot: Int, clickedItem: ItemStack?) {
        val guiItem = gui.items[slot]
        val isMovable = guiItem?.isMovable
        val isPutable = isMovable ?: gui.isPutable
        val isTakeable = isMovable ?: gui.isTakeable

        guiItem?.onClick?.invoke(event)
        gui.onClick?.invoke(event)

        if (event.isCancelled) return

        // Handle number key swaps first
        if (event.click == ClickType.NUMBER_KEY) {
            if (!isPutable || !isTakeable) {
                event.isCancelled = true
                return
            }
        }

        // Prevent putting items (cursor -> GUI)
        if (!isPutable && event.cursor.type != Material.AIR) {
            event.isCancelled = true
            return
        }

        // Prevent taking items (GUI -> cursor)
        if (!isTakeable && clickedItem?.type != Material.AIR) {
            event.isCancelled = true
        }
    }

    private fun handleShiftClick(player: Player, gui: GUI, playerSlot: Int, clickedItem: ItemStack) {
        val guiInventory = gui.inventory ?: return
        val stackableSlots = mutableListOf<Int>()
        val emptySlots = mutableListOf<Int>()

        for (slot in 0 until gui.size) {
            val guiItem = gui.items[slot]
            val slotItem = guiInventory.getItem(slot)
            val isMovable = guiItem?.isMovable ?: gui.isPutable

            if (!isMovable) continue

            if (slotItem != null &&
                slotItem.type == clickedItem.type &&
                slotItem.amount < slotItem.maxStackSize
            ) stackableSlots += slot
            else if (slotItem == null || slotItem.type == Material.AIR)
                emptySlots += slot
        }

        for (slot in stackableSlots + emptySlots) {
            val inserted = tryInsertIntoGui(gui, slot, clickedItem)
            if (inserted) {
                updatePlayerInventory(player, playerSlot, clickedItem)
                GUISync.slotSync(gui, slot)
                return
            }
        }
    }

    private fun tryInsertIntoGui(gui: GUI, slot: Int, clickedItem: ItemStack): Boolean {
        val guiInventory = gui.inventory ?: return false
        val slotItem = guiInventory.getItem(slot)

        if (slotItem != null &&
            slotItem.type == clickedItem.type &&
            slotItem.amount < slotItem.maxStackSize
        ) {
            val spaceLeft = slotItem.maxStackSize - slotItem.amount
            val toAdd = clickedItem.amount.coerceAtMost(spaceLeft)

            slotItem.amount += toAdd
            clickedItem.amount -= toAdd
            return true
        }

        if (slotItem == null || slotItem.type == Material.AIR) {
            val toPlace = clickedItem.amount.coerceAtMost(clickedItem.maxStackSize)
            val toInsert = clickedItem.clone().apply { amount = toPlace }

            guiInventory.setItem(slot, toInsert)
            clickedItem.amount -= toPlace
            return true
        }

        return false
    }

    private fun updatePlayerInventory(player: Player, slot: Int, clickedItem: ItemStack) {
        if (clickedItem.amount <= 0)
            player.inventory.setItem(slot, null)
        else
            player.inventory.setItem(slot, clickedItem)
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